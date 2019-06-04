音频焦点问题：	
	为了避免多个应用程序同时播放音乐，Android 系统使用音频焦点来进行统一管理，即只有获得了音频焦点的应用程序才可以播放音乐。
	
	应用程序在开始播放音频文件前，首先应该请求获得音频焦点，并且应该同时注册监听音频焦点的丢失通知，即如果音频焦点被系统或其他的应用程序抢占时，您的应用程序可以做出合适的响应。

	获取永久焦点：
		必须指定流类型（使用的是哪一个流）和音频焦点的类型（短暂的或是持久的）。
			瞬态焦点用来播放很短时间的音频（例如，播放导航指令）。 
			持久焦点用来播放较长一段时间的音频（例如，播放音乐）。
		
		通过调用 requestAudioFocus()  来实现，如果请求成功，返回 AUDIOFOCUS_REQUEST_GRANTED。
		
		如：请求音乐音频流的永久音频焦点。
			AudioManager am = mContext.getSystemService(Context.AUDIO_SERVICE);   
			...   
			   
			//获取音频焦点  
			int result = am.requestAudioFocus(afChangeListener,   
			//音乐音频流
			AudioManager.STREAM_MUSIC,   
			//获取永久音频焦点
			AudioManager.AUDIOFOCUS_GAIN);   
			   
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {   
				am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);   
				// Start playback.   
				// 开始播放音乐文件   
			}
		
	释放音频焦点：
		1. abandonAudioFocus()  方法来通知系统释放音频焦点
		2. 注销相关的 AudioManager.OnAudioFocusChangeListener
		
		am.abandonAudioFocus(afChangeListener); 
	
	
	获取短暂焦点：
	
		当请求瞬态音频焦点时有一个附加参数可供设置，即是否允许 “DUCK”。通常当应用程序失去了音频焦点时应该停止播放。如果获取短暂音频焦点的时候设置了”DUCK”附加参数，则允许其他的应用程序继续播放，不需要停止，只要降低音量就可以了，然后直到您的应用程序释放了焦点，其他应用程序再重新获得的时候，将音量还原到有一开始的状态。
		
		// Request audio focus for playback   
		int result = am.requestAudioFocus(afChangeListener,   
		// Use the music stream.   
		AudioManager.STREAM_MUSIC,   
		// Request permanent focus.   
		AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);   
		 
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {   
			// Start playback.   
		}
	
		“DUCK” 特别适合那种间歇性播放音频流的应用程序，如驾驶导航的声音提示。
	
	
	音频焦点丢失：
		当音频焦点丢失时，您注册的监听函数onAudioFocusChange()会收到一个事件通知，通知中的参数包括了具体的信息，比如是永久焦点丢失，短暂焦点丢失，还是短暂焦点且允许DUCK的焦点丢失。
		
		如：
			OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {   
				public void onAudioFocusChange(int focusChange) {   
					if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT   
						// Pause playback   
					} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {   
						// Resume playback   
					} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {   
						am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);   
						am.abandonAudioFocus(afChangeListener);   
						// Stop playback   
					}   
				}   
			};   
  
			如果丢失的短暂音频焦点允许DUCK状态，在这种情况下，应用程序降低音量继续播放，不需要暂停。  
			Duck!  
  
			DUCK：降低您应用程序的音量，从而不会打扰其他应用程序音频的播放。  
  
			在下面的代码片段中，当我们失去焦点的时候，降低了媒体播放的音量，重新获得焦点的时候，将音量恢复到原来的状态。  
  
			OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {   
				public void onAudioFocusChange(int focusChange) {   
					if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK   
						// Lower the volume   
					} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {   
						// Raise it back to normal   
					}   
				}   
			}; 
		
		
	音频策略的基础知识：
		stream_type、content_type、devices、routing_strategy
		stream_type：
			音频流的类型。在当前系统中，Android(6.0)一共定义了11种stream_type以供开发者使用。
			
			流类型					对应值
			STREAM_DEFAULT				-1
			STREAM_VOICE_CALL			0
			STREAM_SYSTEM				1
			STREAM_RING					2
			STREAM_MUSIC				3
			STREAM_ALARM				4
			STREAM_NOTIFICATION			5
			STREAM_BLUETOOTH_SCO		6
			STREAM_SYSTEM_ENFORCED		7
			STREAM_DTMF					8
			STREAM_TTS					9
			
		content_type:
			具体输出类型。虽然当前一共有11种stream_type,但一旦进入到Attribute，Android就只将其整理成几种类型。
		device:
			音频输入输出设备。
		routing_strategy:
			音频路由策略。默认情况下，Android是根据路由策略去选择设备负责输出输入音频的。
		
	
	AudioPolicy 和 AudioPolicyService
		AudioFinger是Audio系统的工作引擎，管理者系统中输入输出音频流，并承担音频数据混音，以及读写Audio硬件等工作以实现数据的输入输出功能。AudioPolicyService是Audio系统策略控制中心，具体负责掌管系统中声音设备的选择和切换，音量控制等功能。
		
		
		