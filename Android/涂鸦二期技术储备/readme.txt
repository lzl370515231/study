大小端数据：
	大端模式：
		指数据的高字节保存在内存的低地址中，而数据的低字节保存在内存的高地址中
	小端模式：
		是指数据的高字节保存在内存的高地址中，低字节保存在内存的低地址中。

1. java writeShort 写出来的数据是大端数据

2. ffplay 播放pcm数据，报错：SDL_OpenAudio (2 channels, 44100 Hz): WASAPI can't initialize audio client
	解决办法：增加系统变量 SDL_AUDIODRIVER=directsound 或者 winmm
	
3. ffplay 播放pcm数据的命令：
	ffplay -ar 16k -channels 1 -f s16be -i recoder.pcm
		-ar 		rate
		-channels	通道数
		s16be		16bit	big-endian
	
4. cool edit
	16-bit intel PCM(LSB,MSB)		小端数据
	16-bit Motorola PCM(MSB,LSB)	大端数据