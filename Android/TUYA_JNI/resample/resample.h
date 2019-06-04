#include "timing.h"
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <android/log.h>

#include "speex_resampler.h"

int speex_resampler(short *in_buffer,int in_len,short *out_buffer,int *out_size);

#ifndef MIN
#define MIN(a, b) ((a) < (b) ? (a) : (b))
#endif

#ifndef IN_SAMPLE
#define IN_SAMPLE 16000
#endif

#ifndef OUT_SAMPLE
#define OUT_SAMPLE 8000
#endif

#define ENABLE_LOGD 0

#if ENABLE_LOGD
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#else
#define ALOGD(...)
#endif
#define ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define LOG_TAG    "HMD_TUYA_JNI"

#define BigtoLittle16(A)   (( ((short)(A) & 0xff00) >> 8 )|(( (short)(A) & 0x00ff ) << 8))


/*
	speex_resampler_init			创建实例
		函数原型：
			SpeexResamplerState *speex_resampler_init(spx_uint32_t nb_channels,spx_uint32_t in_rate,spx_uint32_t out_rate,int quality,int *err);
		参数说明：		
			nb_channels [in] 通道数
			in_rate     [in] 输入音频的采样率
			out_rate    [in] 输出音频的采样率
			quality     [in] 重采样质量
			err         [out] 错误码
		返回值：
			成功返回实例句柄，失败返回NULL
	
	speex_resampler_process_int		重采样整数序列
		函数原型:				
			int speex_resampler_process_int(SpeexResamplerState *st,spx_uint32_t channel_index,const float *in,spx_uint32_t *in_len,float *out,spx_uint32_t *out_len);
		参数说明：		
			St                [in]句柄
			channel_index     [in]通道号
			in                [in]输入缓存
			in_len            [in]输入长度（返回处理的采样数）
			out               [in]输出缓存
			out_len           [in]输出长度（返回写入的采样数）
		返回值：
			错误码
	
	speex_resampler_destroy			销毁实例
		函数原型：
			void speex_resampler_destroy(SpeexResamplerState *st)
		参数说明：
			St [in] 	实例句柄
		返回值：
			void
		
		
*/


/*

	把16K的音频转换为8k的音频
	
		st = speex_resampler_init(1, 16000, 8000, 10, &err);
		do{
			readlen = fread(in, sizeof(short), 1024, fin);
			if (readlen > 0)
			{
				inlen = readlen;
				outlen = 1024;
				ret = speex_resampler_process_int(st, 0, in, &inlen, out, &outlen);
				if (ret == RESAMPLER_ERR_SUCCESS)
				{
					fwrite(out, sizeof(short), outlen, fout);
				}
			}
		}while(readlen == 1024);
		speex_resampler_destroy(st);

*/