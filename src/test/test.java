package test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import DataFactory.DataFactory;
import DataFactoryService.NBADataFactory;
import po.HotPlayerTeam;
import po.HotType;
import server.Server;
import dataservice.playerdataservice.PlayerDataService;

public class test {
    public static void main(String[] args) throws Exception
    {
//    	new Server().start();
//    	String url = "rmi://dingfeng/PlayerData";
    	 NBADataFactory factory = DataFactory.instance();
    	PlayerDataService playerData = factory.getPlayerData();
    	if (playerData != null)
    	{
    		try {
    			HotPlayerTeam[] hots = playerData.getDayHotPlayer(getDate("2014-06-10"), HotType.ASSIST);
    			print(hots);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    public static Date getDate(String date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    try {
			return format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;	
	}
	public static void print(Object[] objects)
    {
    	for (Object o : objects)
    	{
    		System.out.println(o);
    	}
    }
    public static void print(Object o)
    {
    	System.out.println(o);
    }

}
