package server;

//timer Runnable class - this used to count server running time

import java.util.Timer;
import java.util.TimerTask;

import csv.ReadCsv;

public class ServerTimerRunnable implements Runnable{
	
	private int ssTime = 0;
	private int spTime = 1000;
	private static int slTime;
	Timer sTimer = new Timer();
	private static int sPass;
	
	TimerTask sTimerTask = new TimerTask() {
		@Override
		public void run() {
			sPass++;
			System.out.println("Sec" + sPass);
			// stop timer
			if(sPass == slTime/1000) {
				sTimer.cancel(); // stop timer
				ReadCsv reo = new ReadCsv(1);
				reo.setStopBiddingStates(); // to set as bidding time is end
				System.out.println("Time is outdate");
				//stopConn();
				System.out.println("All Compleated. Servrt Bidding time Sign off");
			}
		}
	};;
	
	//constructor to get input
	public ServerTimerRunnable(int lt) {
		slTime = lt*60*1000;
	}
	
	//Constructor without parameters
	public ServerTimerRunnable() {
		
	}
	
	@Override
	public void run() {
		start();
	}
	
	private void start() {
		sTimer.scheduleAtFixedRate(sTimerTask,ssTime,spTime);
	}
	
	// add minute to the server runtime if the server in last minute  
	public synchronized void addTimerMintifLmin() {
		if(sPass*1000 > slTime - 1000*60) {
			slTime = slTime + 60*1000;
		}
	}

}
