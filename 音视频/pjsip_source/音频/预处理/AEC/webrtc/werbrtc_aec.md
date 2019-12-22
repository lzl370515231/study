### webrtc_aec



```c
typedef struct webrtc_ec
{
    void       *AEC_inst;
    NsHandle   *NS_inst;
    unsigned    options;
    unsigned	samples_per_frame;
    unsigned	tail;
    unsigned    clock_rate;
    unsigned	channel_count;
    unsigned    subframe_len;		//子帧长度 AGC 可用来计算能量
    sample      tmp_buf[BUF_LEN];
    sample      tmp_buf2[BUF_LEN];
#if SHOW_DELAY_METRICS
    unsigned	counter;
#endif
} webrtc_ec;
```



回声消除，计量单位  10ms 数据

每 20ms 计算一次



//TODO 

WebRtcAec_Process 参数解释