package server;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import dataservice.matchdataservice.MatchDataService;
import dataservice.playerdataservice.PlayerDataService;
import dataservice.teamdataservice.TeamDataService;
import DataFactory.DataFactory;
import DataFactoryService.NBADataFactory;


public class Server {
	   public static TeamDataService teamData;
	   public static PlayerDataService playerData;
	   public static MatchDataService matchData;
       public void start() throws Exception
       {
    	   NBADataFactory factory = DataFactory.instance();
    	   teamData = factory.getTeamData();
    	   playerData = factory.getPlayerData();
    	   matchData = factory.getMatchData();
    	   LocateRegistry.createRegistry(1099);
    	   Naming.rebind("PlayerData", playerData);
    	   System.out.println("PlayerData  server is ready!");
    	   Naming.rebind("TeamData",teamData);
    	   System.out.println("TeamData server is ready!");
    	   Naming.rebind("MatchData", matchData);
    	  System.out.println("MatchData server is  ready!");
       }
       
}
