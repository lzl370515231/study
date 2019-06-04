// pcm_sample_cvt.cpp : 定义控制台应用程序的入口点。
//
 
#include "stdafx.h"
#include <windows.h>
 
 
#include <sys/stat.h> 
#include <time.h>
#include <stdint.h>
#include <stdlib.h>
 
 
extern "C"  
{  
#include <libavcodec/avcodec.h>  
#include <libavformat/avformat.h>  
//#include <libavfilter/avfiltergraph.h>  
//#include <libavfilter/avcodec.h>  
//#include <libavfilter/buffersink.h>  
//#include <libavfilter/buffersrc.h>  
//#include <libavutil/opt.h>  
//#include <libavutil/pixdesc.h>  
}  
 
#include <string>
 
 
 
/************************************************************/  
  
/****函数功能：音视频解码并重采样为规定采样率的WAV文件**************/  
  
/****作者:dreamboy*******************************************/  
  
/****参数：**************************************************/  
  
/****1.outfilename：输出WAV文件路径***************************/  
  
/****2.sample_rate：输出WAV文件的采样率***********************/  
  
/****3.channels：输出WAV文件的通道数量************************/  
  
/****4.inputfilename：输入WAV文件路径************************/  
  
/****说明：当sample_rate或channels为0是表示， 
 
               该参数在输入文件基础上保持不变
			   // 只支持采集点为16位的，即 aCodecCtx->sample_fmt=AV_SAMPLE_FMT_S16
			   // 如果源文件 采样率和通道数本为16000kHz、1，则不进行转换
			   ******************/  
  
int AudioConvertFunc(const char *outfilename,int sample_rate,int channels,const char *inputfilename)  
{  
 AVCodec *aCodec =NULL;  
 AVPacket *packet = NULL;  
 AVFormatContext *pFormatCtx =NULL;  
    AVCodecContext *aCodecCtx= NULL;  
 ReSampleContext* ResampleCtx=NULL;  
 AVFrame *decoded_frame = NULL;  
 int datasize;  
  
 unsigned int i;  
 int len, ret, buffer_size, count, audio_stream_index = -1, totle_samplenum = 0;  
  
 FILE *outfile = NULL;// *infile;  
 //head_pama pt;  
  
 int16_t *audio_buffer = NULL;  
 int16_t *resamplebuff = NULL;  
 int ResampleChange=0;  
 int ChannelsChange=0;  
   
  
 packet = (AVPacket*)malloc(sizeof(AVPacket));  
 if (packet==NULL)  
 {  
  return -1;  
 }  
 packet->data=NULL;  
  
 buffer_size = AVCODEC_MAX_AUDIO_FRAME_SIZE * 2;  
 audio_buffer = (int16_t *)av_malloc(buffer_size);  
 if (audio_buffer==NULL)  
 {  
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  return -2;  
 }  
   
 av_register_all();  
  
 av_init_packet(packet);  
  
 //pFormatCtx = avformat_alloc_context();  
 ret = avformat_open_input(&pFormatCtx, inputfilename, NULL,NULL);  
 
 if(ret < 0)  
 {  
  if (audio_buffer!=NULL)  
  {  
   av_free(audio_buffer);  
   audio_buffer=NULL;  
  }  
    
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  if (pFormatCtx!=NULL)  
  {  
   av_close_input_file(pFormatCtx);  
   pFormatCtx=NULL;  
  }  
    
  return 1;    
 }  
  
 ret = av_find_stream_info(pFormatCtx);  
  
 if( ret < 0)  
 {  
  if (audio_buffer!=NULL)  
  {  
   av_free(audio_buffer);  
   audio_buffer=NULL;  
  }  
  
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  if (pFormatCtx!=NULL)  
  {  
   av_close_input_file(pFormatCtx);  
   pFormatCtx=NULL;  
  }  
  
  return 2;  
 }  
  
 audio_stream_index=-1;  
 for(i=0; i< (signed)pFormatCtx->nb_streams; i++)  
 {  
  
  if(pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO && audio_stream_index < 0)  
  {  
   audio_stream_index = i;  
   break;  
  }  
 }  
  
 if(audio_stream_index == -1)  
 {  
  if (audio_buffer!=NULL)  
  {  
   av_free(audio_buffer);  
   audio_buffer=NULL;  
  }  
  
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  if (pFormatCtx!=NULL)  
  {  
   av_close_input_file(pFormatCtx);  
   pFormatCtx=NULL;  
  }  
  
  return 3;  
 }  
  
 aCodecCtx = pFormatCtx->streams[audio_stream_index]->codec;  
 if (aCodecCtx==NULL)  
 {  
  if (audio_buffer!=NULL)  
  {  
   av_free(audio_buffer);  
   audio_buffer=NULL;  
  }  
  
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  if (pFormatCtx!=NULL)  
  {  
   av_close_input_file(pFormatCtx);  
   pFormatCtx=NULL;  
  }  
  return 4;  
 }  
 aCodec = avcodec_find_decoder(aCodecCtx->codec_id);  
 if(!aCodec)   
 {  
  if (audio_buffer!=NULL)  
  {  
   av_free(audio_buffer);  
   audio_buffer=NULL;  
  }  
  
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  if (pFormatCtx!=NULL)  
  {  
   av_close_input_file(pFormatCtx);  
   pFormatCtx=NULL;  
  }  
  /*if (aCodecCtx!=NULL) 
  { 
   avcodec_close(aCodecCtx); 
   aCodecCtx=NULL; 
  }*/  
  return 5;  
 }  
 //resample init  
 if (channels==0)  
 {  
  channels=aCodecCtx->channels;  
 }  
 if (sample_rate==0)  
 {  
  sample_rate=aCodecCtx->sample_rate;  
 }  
 //if (aCodecCtx->channels!=channels)  
 //{  
 // ChannelsChange=1;  
 // ResampleChange=1;  
 //}  
 if (aCodecCtx->sample_rate!=sample_rate||aCodecCtx->channels!=channels)  
 {  
  ResampleChange=1;  
 }  
 if (ResampleChange==1) 
 {  
	 //ResampleCtx = av_audio_resample_init(channels,aCodecCtx->channels,sample_rate,aCodecCtx->sample_rate,aCodecCtx->sample_fmt,aCodecCtx->sample_fmt,8,10,0,1.0);
	 
  ResampleCtx = av_audio_resample_init(channels,aCodecCtx->channels,sample_rate,aCodecCtx->sample_rate,AV_SAMPLE_FMT_S16,AV_SAMPLE_FMT_S16,16,10,0,1.0);  
  if (ResampleCtx==NULL)  
  {  
   if (audio_buffer!=NULL)  
   {  
    av_free(audio_buffer);  
    audio_buffer=NULL;  
   }  
  
   if (packet->data!=NULL)  
   {  
    av_free_packet(packet);  
    packet->data=NULL;  
   }  
   if (packet!=NULL)  
   {  
    free(packet);  
    packet=NULL;  
   }  
   if (pFormatCtx!=NULL)  
   {  
    av_close_input_file(pFormatCtx);  
    pFormatCtx=NULL;  
   }  
   /*if (aCodecCtx!=NULL) 
   { 
    avcodec_close(aCodecCtx); 
    aCodecCtx=NULL; 
   }*/  
   ResampleChange=0;  
   return 6;  
  }  
  resamplebuff=(int16_t *)malloc(buffer_size);  
  if (resamplebuff==NULL)  
  {  
   if (audio_buffer!=NULL)  
   {  
    av_free(audio_buffer);  
    audio_buffer=NULL;  
   }  
  
   if (packet->data!=NULL)  
   {  
    av_free_packet(packet);  
    packet->data=NULL;  
   }  
   if (packet!=NULL)  
   {  
    free(packet);  
    packet=NULL;  
   }  
   if (pFormatCtx!=NULL)  
   {  
    av_close_input_file(pFormatCtx);  
    pFormatCtx=NULL;  
   }  
   /*if (aCodecCtx!=NULL) 
   { 
    avcodec_close(aCodecCtx); 
    aCodecCtx=NULL; 
   }*/  
     
   if (ResampleChange==1&&ResampleCtx!=NULL)  
   {  
    audio_resample_close(ResampleCtx);  
    ResampleCtx=NULL;  
   }  
   return 7;  
  }  
 }  
int sec=0;// 单位秒
if (pFormatCtx->duration>0)// 文件播放时长
{
	sec = pFormatCtx->duration/1000000;
}
 
 datasize=sec*sample_rate;  
 if(avcodec_open(aCodecCtx, aCodec)<0)  
 {  
  if (audio_buffer!=NULL)  
  {  
   av_free(audio_buffer);  
   audio_buffer=NULL;  
  }  
  
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  if (pFormatCtx!=NULL)  
  {  
   av_close_input_file(pFormatCtx);  
   pFormatCtx=NULL;  
  }  
  if (aCodecCtx!=NULL)  
  {  
   avcodec_close(aCodecCtx);  
   aCodecCtx=NULL;  
  }  
  
  if (ResampleChange==1&&ResampleCtx!=NULL&&resamplebuff!=NULL)  
  {  
   audio_resample_close(ResampleCtx);  
   ResampleCtx=NULL;  
   free(resamplebuff);  
   resamplebuff=NULL;  
  }  
  ResampleChange=0;  
  return 8;  
 }  
  
 //pt.bits = 16;  
 //pt.channels = channels;  
 //pt.rate = sample_rate;  
  
 outfile = fopen(outfilename, "wb");  
 if (!outfile)   
 {  
  if (audio_buffer!=NULL)  
  {  
   av_free(audio_buffer);  
   audio_buffer=NULL;  
  }  
  
  if (packet->data!=NULL)  
  {  
   av_free_packet(packet);  
   packet->data=NULL;  
  }  
  if (packet!=NULL)  
  {  
   free(packet);  
   packet=NULL;  
  }  
  if (pFormatCtx!=NULL)  
  {  
   av_close_input_file(pFormatCtx);  
   pFormatCtx=NULL;  
  }  
  if (aCodecCtx!=NULL)  
  {  
   avcodec_close(aCodecCtx);  
   aCodecCtx=NULL;  
  }  
  
  if (ResampleChange==1&&ResampleCtx!=NULL&&resamplebuff!=NULL)  
  {  
   audio_resample_close(ResampleCtx);  
   ResampleCtx=NULL;  
   free(resamplebuff);  
   resamplebuff=NULL;  
  }  
  ResampleChange=0;  
  return 9;  
 }  
  
 fseek(outfile,44,SEEK_SET);  
  
    while(av_read_frame(pFormatCtx, packet) >= 0)   
 {  
  //CheckMessageQueue();  
     if(packet->stream_index == audio_stream_index)  
     {  
   //while(packet->size > 0)  
   //{  
    buffer_size = AVCODEC_MAX_AUDIO_FRAME_SIZE * 100;  
    len = avcodec_decode_audio3(aCodecCtx, audio_buffer, &buffer_size, packet);  
  
    if (len < 0)   
    {  
     break;  
    }  
  
    if(buffer_size > 0)  
    {  
     //resample  
     if (ResampleChange==1)  
     {  
      int samples=buffer_size/ ((aCodecCtx->channels) * 2);  
      int resamplenum= 0;  
      resamplenum = audio_resample(ResampleCtx,   
       resamplebuff,   
       audio_buffer,   
       samples);  
      count = fwrite(resamplebuff, 2*channels, resamplenum, outfile);  
     }  
       
     else  
     {  
      count = fwrite(audio_buffer, 2*aCodecCtx->channels, buffer_size/((aCodecCtx->channels)*2), outfile);  
     }  
     totle_samplenum += count;  
    }  
    if (packet->data!=NULL)  
    {  
     av_free_packet(packet);  
     packet->data=NULL;  
    }  
    //packet->size -= len;  
    //packet->data += len;  
   //}  
   if (datasize!=0&&totle_samplenum>=datasize)  
   {  
    break;  
   }  
     }  
 }  
  
 fseek(outfile,0,SEEK_SET);  
//wav_write_header(outfile, pt, totle_samplenum);  
 
 if (outfile!=NULL)  
 {  
  fclose(outfile);  
  outfile=NULL;  
 }  
      
 if (audio_buffer!=NULL)  
 {  
  av_free(audio_buffer);  
  audio_buffer=NULL;  
 }  
   
 if (aCodecCtx!=NULL)  
 {  
  avcodec_close(aCodecCtx);  
  aCodecCtx=NULL;  
 }  
   
 if (packet!=NULL)  
 {  
  free(packet);//  
  packet=NULL;  
 }  
   
 if (pFormatCtx!=NULL)  
 {  
  av_close_input_file(pFormatCtx);  
  pFormatCtx=NULL;  
 }  
   
 if (ResampleChange==1)  
 {  
  if (resamplebuff!=NULL)  
  {  
   free(resamplebuff);  
   resamplebuff=NULL;  
  }  
  if (ResampleCtx!=NULL)  
  {  
   audio_resample_close(ResampleCtx);  
   ResampleCtx=NULL;  
  }  
 }  
 if (totle_samplenum<=sample_rate*5)  
 {  
  return 10;  
 }  
 return 0;  
}  
int main(int argc, char **argv)  
{ 
    AudioConvertFunc("test_48k_16.wav",48000,1,"test_8k_16.wav");
    return 0;
}  
