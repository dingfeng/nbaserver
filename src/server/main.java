package server;

import java.util.Timer;
import java.util.TimerTask;

public class main {
  public static void main(String[] args) throws Exception
  {
	  Server server = new Server();
	  server.start();
	  Timer timer = new Timer();
	  TimerTask task = new TimerTask()
	  {
		@Override
		public void run() {
			System.gc();
		}
	  };
	  timer.schedule(task, 0,5000);
  }
}
