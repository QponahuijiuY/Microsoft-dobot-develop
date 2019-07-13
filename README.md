---
title: 微软在线语音识别+DOBOT机械臂二次开发
date: 
author: Mutong
top: top
cover: true
categories: 
tags: java
---
# 微软在线语音识别+DOBOT机械臂二次开发

## 写在前面
本文主要介绍通过语音识别，控制DOBOT机械臂运动。
比如：语音输入“Left.“，机械臂就会往左运动，语音输入”Forward“，机械臂就会往前运动。

## 语音服务简介

目前市面上的第三方语音识别APT有很多，主要介绍几种，[百度AI开放平台](https://ai.baidu.com),[讯飞开放平台](https://www.xfyun.cn),[Microsoft Azure](https://azure.microsoft.com)。目前实现了以上三种的语音识别的服务，但还有很多尚未尝试，希望有兴趣的小伙伴可以一起交流

三者也有许多异同
* 百度，科大国内的平台，支持中文识别。微软属于国外平台，目前笔者还没有实现中文，只能之别英文
* 百度实现了访问本地的pcm文件然后输出转化，笔者尚未实现在线麦克风输入。科大，微软现在可以麦克风语音输入。
* 百度，科大讯飞代码较多，微软代码较少，且容易理解。
* 三者都只能联网，断网后连接失败，需要访问网络上的语音库吧。

## 微软语音识别

微软语音识别SDK可以说是最良心的了。手把手的教你创建项目到运行。我也就不多介绍了。[附上连接](https://docs.microsoft.com/zh-cn/azure/cognitive-services/speech-service/quickstart-java-jre),改的东西并不到，只是自己需要注册一个账号，获取`YourSubscriptionKey`和`YourServiceRegion`。最后就可以运行了。微软上也提供了[java代码示例](https://github.com/Azure-Samples/cognitive-services-speech-sdk/tree/master/quickstart/java-jre)。
代码示例：**这是微软官方的代码，之后需要对它进行修改。**
```
package speechsdk.quickstart;

import java.util.concurrent.Future;
import com.microsoft.cognitiveservices.speech.*;

/**
 * Quickstart: recognize speech using the Speech SDK for Java.
 */
public class Main {

    /**
     * @param args Arguments are ignored in this sample.
     */
    public static void main(String[] args) {
        try {
            // Replace below with your own subscription key
            String speechSubscriptionKey = "YourSubscriptionKey";
            // Replace below with your own service region (e.g., "westus").
            String serviceRegion = "YourServiceRegion";

            int exitCode = 1;
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            assert(config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);

            System.out.println("Say something...");

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            SpeechRecognitionResult result = task.get();
            assert(result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                System.out.println("We recognized: " + result.getText());
                exitCode = 0;
            }
            else if (result.getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
            else if (result.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(result);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }
            }

            reco.close();
            
            System.exit(exitCode);
        } catch (Exception ex) {
            System.out.println("Unexpected exception: " + ex.getMessage());

            assert(false);
            System.exit(1);
        }
    }
}
```
## DOBOT利用java的机械臂二次开发
[dobot接口API文档](https://max.book118.com/html/2018/0107/147678711.shtm)，主要通过这个api文档将语音识别的字符串作为参数传递给DOBOT的动作主函数。
[dobotdemoforjava](https://cn.dobot.cc/downloadcenter.html?sub_cat=72#sub-download),这个dobot的下载中心，找到Dobot Demo v2.0，这个里面有各种不同语言的动作源代码。
我利用的是java所有，下载之后选择dobotdemoforjava即可。
dobotdemoforjava代码示例：**这是dobot官方的动作源代码，之后需要传递语音识别的参数。**
```
import java.util.Timer;
import java.util.TimerTask;

import CPlusDll.DobotDll;
import CPlusDll.DobotDll.*;
import com.sun.jna.ptr.IntByReference;

// tip: The demo must import Jna library, inner DobotDemo folder of this project
public class Main {
	public static void main(String[] args) {
		try {

			Main app = new Main();
			app.Start();

			IntByReference ib = new IntByReference();

			JOGCmd test = new JOGCmd();
			test.isJoint = 1;
			while (true) {
				try {
					test.cmd = 1;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);
					test.cmd = 2;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/*
			 * while(true) { try{ PTPCmd ptpCmd = new PTPCmd(); ptpCmd.ptpMode = 0; ptpCmd.x
			 * = 260; ptpCmd.y = 0; ptpCmd.z = 50; ptpCmd.r = 0;
			 * DobotDll.instance.SetPTPCmd(ptpCmd, true, ib); //Thread.sleep(200);
			 * 
			 * ptpCmd.ptpMode = 0; ptpCmd.x = 220; ptpCmd.y = 0; ptpCmd.z = 80; ptpCmd.r =
			 * 0; DobotDll.instance.SetPTPCmd(ptpCmd, true, ib); //Thread.sleep(200);
			 * 
			 * } catch (Exception e) { e.printStackTrace(); } }
			 */
			// DobotDll.instance.DisconnectDobot();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void Start() {
		System.out.printf("the number is: Start");		
		DobotResult ret = DobotResult.values()[DobotDll.instance.ConnectDobot((char) 0, 115200,(char)0,(char)0)];
		// 开始连接
		if (ret == DobotResult.DobotConnect_NotFound || ret == DobotResult.DobotConnect_Occupied) {
			System.out.printf("the number is: if");
			Msg("Connect error, code:" + ret.name());
			return;
		}
		Msg("connect success code:" + ret.name());

		StartDobot();

		StartGetStatus();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		super();
	}

	private void StartDobot() {
		IntByReference ib = new IntByReference();
		EndEffectorParams endEffectorParams = new EndEffectorParams();
		endEffectorParams.xBias = 71.6f;
		endEffectorParams.yBias = 0;
		endEffectorParams.zBias = 0;
		DobotDll.instance.SetEndEffectorParams(endEffectorParams, false, ib);
		JOGJointParams jogJointParams = new JOGJointParams();
		for (int i = 0; i < 4; i++) {
			jogJointParams.velocity[i] = 200;
			jogJointParams.acceleration[i] = 200;
		}
		DobotDll.instance.SetJOGJointParams(jogJointParams, false, ib);

		JOGCoordinateParams jogCoordinateParams = new JOGCoordinateParams();
		for (int i = 0; i < 4; i++) {
			jogCoordinateParams.velocity[i] = 200;
			jogCoordinateParams.acceleration[i] = 200;
		}
		DobotDll.instance.SetJOGCoordinateParams(jogCoordinateParams, false, ib);

		JOGCommonParams jogCommonParams = new JOGCommonParams();
		jogCommonParams.velocityRatio = 50;
		jogCommonParams.accelerationRatio = 50;
		DobotDll.instance.SetJOGCommonParams(jogCommonParams, false, ib);

		PTPJointParams ptpJointParams = new PTPJointParams();
		for (int i = 0; i < 4; i++) {
			ptpJointParams.velocity[i] = 200;
			ptpJointParams.acceleration[i] = 200;
		}
		DobotDll.instance.SetPTPJointParams(ptpJointParams, false, ib);

		PTPCoordinateParams ptpCoordinateParams = new PTPCoordinateParams();
		ptpCoordinateParams.xyzVelocity = 200;
		ptpCoordinateParams.xyzAcceleration = 200;
		ptpCoordinateParams.rVelocity = 200;
		ptpCoordinateParams.rAcceleration = 200;
		DobotDll.instance.SetPTPCoordinateParams(ptpCoordinateParams, false, ib);

		PTPJumpParams ptpJumpParams = new PTPJumpParams();
		ptpJumpParams.jumpHeight = 20;
		ptpJumpParams.zLimit = 180;
		DobotDll.instance.SetPTPJumpParams(ptpJumpParams, false, ib);

		DobotDll.instance.SetCmdTimeout(3000);
		DobotDll.instance.SetQueuedCmdClear();
		DobotDll.instance.SetQueuedCmdStartExec();
	}

	private void StartGetStatus() {
		Timer timerPos = new Timer();
		timerPos.schedule(new TimerTask() {
			public void run() {
				Pose pose = new Pose();
				DobotDll.instance.GetPose(pose);

				Msg("joint1Angle=" + pose.jointAngle[0] + "  " + "joint2Angle=" + pose.jointAngle[1] + "  "
						+ "joint3Angle=" + pose.jointAngle[2] + "  " + "joint4Angle=" + pose.jointAngle[3] + "  " + "x="
						+ pose.x + "  " + "y=" + pose.y + "  " + "z=" + pose.z + "  " + "r=" + pose.r + "  ");
			}
		}, 100, 500);//
	}

	private void Msg(String string) {
		System.out.println(string);
	}
}
```
## 传递语音参数

主要思想：
* 在微软语音识别的源代码中，说出一个字符串，比如说“Left.”，然后返回这个字符串。
* 动作源代码中，1代表向左转，2代表向右转，3代表向前，4代表向后，……一共0-10是个数字分别代表一个动作，0代表空闲(静止)状态。
* 我们创建一个匹配方法，返回int类型，判断当前字符串是否是Left，如果是，返回1；判断当前字符串是否是Right，如果是，返回2.
```
public int pipei(String a) {
		if(a.equals("Left.")) {
			return 1;
		}
		else if(a.equals("Right.")) {
			return 2;
		}
		else if(a.equals("Forward.")) {
			return 3;
		}
		else if(a.equals("Backward.")) {
			return 4;
		}
		else if(a.equals("Down.")) {
			return 5;
		}
		else if(a.equals("Up.")) {
			return 6;
		}
		return 0;
	}
```
* 把微软的语音源代码封装成一个类Listen，这个类Listen包含两个方法，一个`listen()`,一个`pipei()`。
* 在动作源代码中，创建Listen对象`Listen listen = new Listen();`，然后用一个字符串a接受传递过来的“Left.”`String a = listen.listen();`
* 在动作源代码中，在执行动作之前，做一个判断，这个`pipei(a)`值是否等于1，等于的话在执行向左转的操作。
```
if(listen.pipei(a) == 1) {
	System.out.println("left.");
	test.cmd = 1;
	DobotDll.instance.SetJOGCmd(test, false, ib);
	Thread.sleep(500);
	test.cmd = 0;
	DobotDll.instance.SetJOGCmd(test, false, ib);
	Thread.sleep(2000);
}
```
* 因为本身动作源码是一个不断循环的过程，然后把`while(true)`语句注释掉即可。

## 整体的代码
微软语音代码更改之后：
```
package speechsdk.quickstart;

import java.util.concurrent.Future;

import javax.xml.transform.Result;

import com.microsoft.cognitiveservices.speech.*;

public class Listen {

	public int pipei(String a) {
		if(a.equals("Left.")) {
			return 1;
		}
		else if(a.equals("Right.")) {
			return 2;
		}
		else if(a.equals("Forward.")) {
			return 3;
		}
		else if(a.equals("Backward.")) {
			return 4;
		}
		else if(a.equals("Down.")) {
			return 5;
		}
		else if(a.equals("Up.")) {
			return 6;
		}
		return 0;
	}
	
    public String listen() {
    	
        try {
            // Replace below with your own subscription key
            String speechSubscriptionKey = "7c79167c0c5844fca966b6f83306ae4f";
            // Replace below with your own service region (e.g., "westus").
            String serviceRegion = "westus";

            int exitCode = 1;
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            assert(config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);
            System.out.println("***************Start doing speech recognition***************");
            System.out.println("***************Say something...***************");

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            SpeechRecognitionResult result = task.get();
            assert(result != null);
            if (result.getReason() == ResultReason.RecognizedSpeech) {
                System.out.println("We recognized: " + result.getText());
                exitCode = 0;
                return result.getText();
            }
            else if (result.getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
            else if (result.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(result);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());
                if (cancellation.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }
            }
            reco.close();            
            System.exit(exitCode);            
        } catch (Exception ex) {
            System.out.println("Unexpected exception: " + ex.getMessage());
            assert(false);
            System.exit(1);         
        }
		return null;
    }
}
```
动作源代码：
```
import java.util.Timer;
import java.util.TimerTask;

import CPlusDll.DobotDll;
import CPlusDll.DobotDll.*;
import com.sun.jna.ptr.IntByReference;
import speechsdk.quickstart.*;
// tip: The demo must import Jna library, inner DobotDemo folder of this project
//动作源代码
public class Main {
	public static void main(String[] args) {
		//创建Listen的对象
		Listen listen = new Listen();
		String a = listen.listen();


		try {

			Main app = new Main();
			app.Start();

			IntByReference ib = new IntByReference();
			
			JOGCmd test = new JOGCmd();
			test.isJoint = 1;
//			while (true) {//注释掉while防止无限循环
				try {
					//判断参数是否等于1
					if(listen.pipei(a) == 1) {
						System.out.println("left.");
					test.cmd = 1;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					//0代表静止
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);
					}
					
					else if(listen.pipei(a) == 2) {
						System.out.println("right.");
					test.cmd = 2;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);
					}
					
					else if(listen.pipei(a) == 3) {
						System.out.println("Forward.");
					test.cmd = 3;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);
					}
					
					else if(listen.pipei(a) == 4) {
						System.out.println("Backword.");
					test.cmd = 4;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);
					}
					
					else if(listen.pipei(a) == 5) {
						System.out.println("Up.");
					test.cmd = 5;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);
					}
					
					else if(listen.pipei(a) == 6) {
						System.out.println("Down.");
					test.cmd = 6;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
					test.cmd = 0;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(2000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
//			}

			/*
			 * while(true) { try{ PTPCmd ptpCmd = new PTPCmd(); ptpCmd.ptpMode = 0; ptpCmd.x
			 * = 260; ptpCmd.y = 0; ptpCmd.z = 50; ptpCmd.r = 0;
			 * DobotDll.instance.SetPTPCmd(ptpCmd, true, ib); //Thread.sleep(200);
			 * 
			 * ptpCmd.ptpMode = 0; ptpCmd.x = 220; ptpCmd.y = 0; ptpCmd.z = 80; ptpCmd.r =
			 * 0; DobotDll.instance.SetPTPCmd(ptpCmd, true, ib); //Thread.sleep(200);
			 * 
			 * } catch (Exception e) { e.printStackTrace(); } }
			 */
			// DobotDll.instance.DisconnectDobot();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void Start() {
		System.out.printf("the number is: Start");		
		DobotResult ret = DobotResult.values()[DobotDll.instance.ConnectDobot((char) 0, 115200,(char)0,(char)0)];
		// 开始连接
		if (ret == DobotResult.DobotConnect_NotFound || ret == DobotResult.DobotConnect_Occupied) {
			System.out.printf("the number is: if");
			Msg("Connect error, code:" + ret.name());
			return;
		}
		Msg("connect success code:" + ret.name());

		StartDobot();

		//StartGetStatus();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		super();
	}

	private void StartDobot() {
		IntByReference ib = new IntByReference();
		EndEffectorParams endEffectorParams = new EndEffectorParams();
		endEffectorParams.xBias = 71.6f;
		endEffectorParams.yBias = 0;
		endEffectorParams.zBias = 0;
		DobotDll.instance.SetEndEffectorParams(endEffectorParams, false, ib);
		JOGJointParams jogJointParams = new JOGJointParams();
		for (int i = 0; i < 4; i++) {
			jogJointParams.velocity[i] = 200;
			jogJointParams.acceleration[i] = 200;
		}
		DobotDll.instance.SetJOGJointParams(jogJointParams, false, ib);

		JOGCoordinateParams jogCoordinateParams = new JOGCoordinateParams();
		for (int i = 0; i < 4; i++) {
			jogCoordinateParams.velocity[i] = 200;
			jogCoordinateParams.acceleration[i] = 200;
		}
		DobotDll.instance.SetJOGCoordinateParams(jogCoordinateParams, false, ib);

		JOGCommonParams jogCommonParams = new JOGCommonParams();
		jogCommonParams.velocityRatio = 50;
		jogCommonParams.accelerationRatio = 50;
		DobotDll.instance.SetJOGCommonParams(jogCommonParams, false, ib);

		PTPJointParams ptpJointParams = new PTPJointParams();
		for (int i = 0; i < 4; i++) {
			ptpJointParams.velocity[i] = 200;
			ptpJointParams.acceleration[i] = 200;
		}
		DobotDll.instance.SetPTPJointParams(ptpJointParams, false, ib);

		PTPCoordinateParams ptpCoordinateParams = new PTPCoordinateParams();
		ptpCoordinateParams.xyzVelocity = 200;
		ptpCoordinateParams.xyzAcceleration = 200;
		ptpCoordinateParams.rVelocity = 200;
		ptpCoordinateParams.rAcceleration = 200;
		DobotDll.instance.SetPTPCoordinateParams(ptpCoordinateParams, false, ib);

		PTPJumpParams ptpJumpParams = new PTPJumpParams();
		ptpJumpParams.jumpHeight = 20;
		ptpJumpParams.zLimit = 180;
		DobotDll.instance.SetPTPJumpParams(ptpJumpParams, false, ib);

		DobotDll.instance.SetCmdTimeout(3000);
		DobotDll.instance.SetQueuedCmdClear();
		DobotDll.instance.SetQueuedCmdStartExec();
	}

	private void StartGetStatus() {
		Timer timerPos = new Timer();
		timerPos.schedule(new TimerTask() {
			public void run() {
				Pose pose = new Pose();
				DobotDll.instance.GetPose(pose);

				Msg("joint1Angle=" + pose.jointAngle[0] + "  " + "joint2Angle=" + pose.jointAngle[1] + "  "
						+ "joint3Angle=" + pose.jointAngle[2] + "  " + "joint4Angle=" + pose.jointAngle[3] + "  " + "x="
						+ pose.x + "  " + "y=" + pose.y + "  " + "z=" + pose.z + "  " + "r=" + pose.r + "  ");
			}
		}, 100, 500);//
	}

	private void Msg(String string) {
		System.out.println(string);
	}
}
```


## 总结
总之做下来也有不少的收获，但也有一些问题
* 还是不能进行中文输入，只能英文，但是自己的口语发音不准确，我只能在手机单词软件上读取单词的录音，然后让麦克风对准手机麦克风，
