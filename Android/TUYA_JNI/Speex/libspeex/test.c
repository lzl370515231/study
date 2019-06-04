#include <stdio.h>
#include <android/log.h>

#include "speex/speex_preprocess.h"
#include "speex/speex_resampler.h"

#define ENABLE_LOGD 0

#if ENABLE_LOGD
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#else
#define ALOGD(...)
#endif
#define ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define LOG_TAG    "HMD_SPEEX"

#ifndef IN_SAMPLE
#define IN_SAMPLE 16000
#endif

#ifndef OUT_SAMPLE
#define OUT_SAMPLE 8000
#endif

#ifdef __cplusplus
extern "C" {
#endif


FILE *fp;
FILE *fp_out;


void speex_resampler_fd(){
	int err =0;
	int readlen =0;
	SpeexResamplerState *st;
	int inlen=0;
	int outlen =0;
	int ret =0;
	
	ALOGE("speex_resampler_fd 1");
	
	SpeexPreprocessState *state = speex_preprocess_state_init(2048, 16000);
	ALOGE("speex_resampler_fd 2");
	int denoise = 1;
    int noiseSuppress = -25;
    speex_preprocess_ctl(state, SPEEX_PREPROCESS_SET_DENOISE, &denoise);
	ALOGE("speex_resampler_fd 3");
    speex_preprocess_ctl(state, SPEEX_PREPROCESS_SET_NOISE_SUPPRESS, &noiseSuppress);
	ALOGE("speex_resampler_fd 4");
    
    int i;
    i = 0;
    //speex_preprocess_ctl(state, SPEEX_PREPROCESS_SET_AGC, &i);
	ALOGE("speex_resampler_fd 5");
    i = 80000;
    //speex_preprocess_ctl(state, SPEEX_PREPROCESS_SET_AGC_LEVEL, &i);
	ALOGE("speex_resampler_fd 6");
    i = 0;
    //speex_preprocess_ctl(state, SPEEX_PREPROCESS_SET_DEREVERB, &i);
	ALOGE("speex_resampler_fd 7");
    float f = 0;
    //speex_preprocess_ctl(state, SPEEX_PREPROCESS_SET_DEREVERB_DECAY, &f);
	ALOGE("speex_resampler_fd 8");
    f = 0;
    //speex_preprocess_ctl(state, SPEEX_PREPROCESS_SET_DEREVERB_LEVEL, &f);
	ALOGE("speex_resampler_fd 9");
	
	st = speex_resampler_init(1, IN_SAMPLE, OUT_SAMPLE, 3, &err);
	ALOGE("speex_resampler_fd 10");
	
	short in[2048];
	short out[2048];
		do{
			readlen = fread(in, sizeof(short), 1024, fp);
			if (readlen > 0)
			{
				inlen = readlen;
				ALOGE("readlen:%d",readlen);
				outlen = 1024;
				ALOGE("speex_resampler_fd 11");
				speex_preprocess_run(state, (spx_int16_t*)(in));
				ALOGE("speex_resampler_fd 12");
				
				ret = speex_resampler_process_interleaved_int(st,in, &inlen, out, &outlen);
				
				ALOGE("speex_resampler_fd 13");
				//ret =speex_resampler(in, inlen, out, &outlen);
				
				if (ret == RESAMPLER_ERR_SUCCESS)
				{
					fwrite(out, sizeof(short), outlen, fp_out);
				}
			}
		}while(readlen == 1024);
		fclose(fp);
		fclose(fp_out);
		speex_preprocess_state_destroy(state);
		speex_resampler_destroy(st);
}



int initFile(){
	ALOGE("initFile start....");
	fp = fopen("/mnt/media_rw/sda1/recoder.pcm","rb");
	fp_out = fopen("/mnt/media_rw/sda1/resample.pcm","wb");
	return 0;	
}



int main(int argc, char** argv){
	initFile();
	speex_resampler_fd();
	return 0;
}

#ifdef __cplusplus
}
#endif