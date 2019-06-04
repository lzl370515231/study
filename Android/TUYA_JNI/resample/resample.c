

#ifdef __cplusplus
extern "C" {
#endif

#include "resample.h"

/*void speex_resampler(int16_t *in_buffer,uint32_t in_len,int16_t *out_buffer,uint32_t out_size) {
	
    if (in_buffer != NULL && out_buffer != NULL) {
        int errcode = 0;
        int quality = SPEEX_RESAMPLER_QUALITY_MAX;
        SpeexResamplerState *resamplerState = speex_resampler_init(1, IN_SAMPLE, OUT_SAMPLE, quality, &errcode);
        if (errcode == RESAMPLER_ERR_SUCCESS) {
            spx_uint32 spx_in_len = (spx_uint32) in_len;
            spx_uint32 out_len = out_size;
            double startTime = now();
            errcode = speex_resampler_process_int(resamplerState, 0, in_buffer, &spx_in_len, out_buffer, &out_len);
            double time_interval = calcElapsed(startTime, now());
            printf("process: %s \n", speex_resampler_strerror(errcode));
            speex_resampler_destroy(resamplerState);
            printf("time interval: %d ms\n ", (int) (time_interval * 1000));
        } else {
            printf("error: %s \n", speex_resampler_strerror(errcode));
        }
    } else {
    }
}
*/

int speex_resampler(short *in_buffer,int in_len,short *out_buffer,int *out_size) {
	
	ALOGE("speex_resampler start ....");
	int errcode = 0;
    if (in_buffer != NULL && out_buffer != NULL) {
        
        //int quality = SPEEX_RESAMPLER_QUALITY_MAX;
        int quality = SPEEX_RESAMPLER_QUALITY_VOIP;
        //int quality = SPEEX_RESAMPLER_QUALITY_MIN;
        SpeexResamplerState *resamplerState = speex_resampler_init(1, IN_SAMPLE, OUT_SAMPLE, quality, &errcode);
        if (errcode == RESAMPLER_ERR_SUCCESS) {
            spx_uint32 spx_in_len = (spx_uint32) in_len;
            //spx_uint32 out_len = out_size;
            double startTime = now();
            errcode = speex_resampler_process_interleaved_int(resamplerState,in_buffer, &in_len, out_buffer, out_size);
            //errcode = speex_resampler_process_interleaved_int(resamplerState, in_buffer, &spx_in_len, out_buffer, &out_len);
            //double time_interval = calcElapsed(startTime, now());
           // ALOGE("process: %s \n", speex_resampler_strerror(errcode));
            speex_resampler_destroy(resamplerState);
            //ALOGE("time interval: %d ms\n ", (int) (time_interval * 1000));
        } else {
            //ALOGE("error: %s \n", speex_resampler_strerror(errcode));
        }
    } else {
    }
	ALOGE("speex_resampler end ....");
	return errcode;
}

FILE *fp;
FILE *fp_out;

void speex_resampler_fd(){
	int err =0;
	int readlen =0;
	SpeexResamplerState *st;
	int inlen=0;
	int outlen =0;
	int ret =0;
	st = speex_resampler_init(1, IN_SAMPLE, OUT_SAMPLE, 3, &err);
	short in[2048];
	short out[2048];
		do{
			readlen = fread(in, sizeof(short), 1024, fp);
			if (readlen > 0)
			{
				inlen = readlen;
				ALOGE("readlen:%d",readlen);
				outlen = 1024;
				ret = speex_resampler_process_interleaved_int(st,in, &inlen, out, &outlen);
				//ret =speex_resampler(in, inlen, out, &outlen);
				
				if (ret == RESAMPLER_ERR_SUCCESS)
				{
					fwrite(out, sizeof(short), outlen, fp_out);
				}
			}
		}while(readlen == 1024);
		fclose(fp);
		fclose(fp_out);
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

