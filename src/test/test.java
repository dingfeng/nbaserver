package test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import server.Server;
import dataservice.playerdataservice.PlayerDataService;

public class test {
    public static void main(String[] args) throws Exception
    {
//    	new Server().start();
    	String url = "rmi://127.0.0.1/PlayerData";
    	PlayerDataService playerData = null;
    	try {
			playerData = (PlayerDataService) Naming.lookup(url);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
    	if (playerData != null)
    	{
    		try {
				System.out.println(playerData.findPlayer("Kobe Bryant").getBirthday());
				tool.ImageTool.showImage(playerData.findPlayer("Kobe Bryant").getImage());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}
