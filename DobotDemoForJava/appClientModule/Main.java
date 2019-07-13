import java.util.Timer;
import java.util.TimerTask;

import CPlusDll.DobotDll;
import CPlusDll.DobotDll.*;
import com.sun.jna.ptr.IntByReference;
import speechsdk.quickstart.*;
// tip: The demo must import Jna library, inner DobotDemo folder of this project

public class Main {
	public static void main(String[] args) {
		Listen listen = new Listen();
		String a = listen.listen();
//		listen.pipei("Left.");

		try {

			Main app = new Main();
			app.Start();

			IntByReference ib = new IntByReference();
			
			JOGCmd test = new JOGCmd();
			test.isJoint = 1;
//			while (true) {
				try {
					if(listen.pipei(a) == 1) {
						System.out.println("left.");
					test.cmd = 1;
					DobotDll.instance.SetJOGCmd(test, false, ib);
					Thread.sleep(500);
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