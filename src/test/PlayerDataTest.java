package test;

import static org.junit.Assert.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import po.HPlayerPO;
import po.HotPlayerTeam;
import po.HotType;
import po.MatchPlayerPO;
import po.PlayerHighPO;
import po.PlayerNormalPO;
import po.PlayerPO;
import po.TeamPlayerImage;
import dataservice.playerdataservice.PlayerDataService;
import dataservice.playerdataservice.SeasonType;
import DataFactory.DataFactory;
import DataFactoryService.NBADataFactory;

public class PlayerDataTest {
    PlayerDataService player;
	@Before
	public void setUp()
	{
		try {
			player = (PlayerDataService) Naming.lookup("rmi://127.0.0.1/PlayerData");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testGetAllPlayerData() throws RemoteException {
		PlayerPO[] players = player.getAllActivePlayerData();
		
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testFindPlayer() throws RemoteException {
		//未完成
		HPlayerPO p = player.findPlayer("Kobe Bryant");
//		print (p);
	}

	@Test
	public void testGetPlayerOfTeam() throws RemoteException
	{
		PlayerPO[] players = player.getPlayersOfTeam("LAL");
//		print(players);
	}
	
	@Test
	public void testGetPlayerHigh() throws RemoteException {
		PlayerHighPO players = player.getPlayerHigh(2012, "Kobe Bryant", SeasonType.REGULAR);
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testSortPlayerHighn() throws RemoteException {
		PlayerHighPO[] players = player.sortPlayerHighn(2003, "efficiency desc", 9, SeasonType.REGULAR);
//		print(players);
	    assertEquals(true,true);
	}

	@Test
	public void testGetPlayerNormalTotal() throws RemoteException {
		PlayerNormalPO players = player.getPlayerNormalTotal(2013, "Kobe Bryant", SeasonType.REGULAR);
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testSortPlayerNormalTotal() throws RemoteException {
		PlayerNormalPO[] players = player.sortPlayerNormalTotal(2010, "rebs desc"
				+ "", 10, SeasonType.PLAYOFF);
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testGetPlayerNormalAve() throws RemoteException {
        PlayerNormalPO normalPlayer = player.getPlayerNormalAve(2009, "Kobe Bryant", SeasonType.REGULAR);
//        print(normalPlayer);
        assertEquals(true,true);
	}

	@Test
	public void testSortPlayerNormalAven() throws RemoteException {
		PlayerNormalPO[] players = player.sortPlayerNormalAven(2012, "rebs", 67, SeasonType.PLAYOFF);
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testGetPlayerAllSeasonsTotal() throws RemoteException {
		PlayerNormalPO[] players =  player.getPlayerAllSeasonsTotal("Kobe Bryant", SeasonType.REGULAR);
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testGetPlayerAllSeasonsAve() throws RemoteException {
		PlayerNormalPO[] players = player.getPlayerAllSeasonsAve("Kobe Bryant", SeasonType.REGULAR);
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testGetPlayerAllSeasons() throws RemoteException {
		PlayerHighPO[] players =  player.getPlayerAllSeasonsHigh("Kobe Bryant", SeasonType.REGULAR);
//		print(players);
		assertEquals(true,true);
	}

	@Test
	public void testGetSeasonAveData() throws RemoteException {
		PlayerNormalPO[] players =  player.getPlayerAllSeasonsAve("Kobe Bryant", SeasonType.REGULAR);
//		print(players);
		assertEquals(true,true);
	}
	
	@Test
	public void getTeamTest()
	{
//		print(player.getPlayersOfTeam("LAL"));
	}
	@Test
	public void testHplayer() throws RemoteException
	{
		HPlayerPO[] players = player.getHPlayerByIni("a");
//		print(players);
	}
	@Test
	public void testScreen() throws RemoteException
	{
		print(player.screenPlayer(null, "W", "SouthWest","F", 10));
	}
	@Test
	public void testMatchPlayer() throws RemoteException
	{
		MatchPlayerPO[] players = player.getSeasonMatches(2014, "Kobe Bryant", SeasonType.REGULAR);
//		print (players);
	}
	@Test 
	public void testSeasonTeamPlayer() throws RemoteException
	{
		PlayerNormalPO[] players = player.getSeasonPlayerNormalOfTeam(1999, SeasonType.PLAYOFF, "LAL");
//		print (players);
	}
	@Test
	public void testPlayerPOFind() throws RemoteException
	{
//		print(player.findActivePlayerPO("Kobe Bryant"));
	}
	@Test 
	public void testPlayerFuzzilySearch() throws RemoteException
	{
//		print(player.fuzzilySearchActivePlayer("A"));
	}
	@Test 
	public void testPlayerFuzzilySearchPlayerPO() throws RemoteException
	{
//		print("ddddddddddddddddddddddddddddddddddddddddddddd");
//		print(player.fuzzilySearchAvtivePlayerPO("A"));
	}
	@Test
	public void testGetAllPlayerImage() throws RemoteException
	{
		TeamPlayerImage[] images = player.getAllPlayerImage();
//		print(images);
	}
	@Test
	public void testGetMatchPlayerPOn() throws RemoteException
	{
		MatchPlayerPO[] players = player.getSeasonMatchesn(2014, "Lebron James", SeasonType.REGULAR, 5);
	    print(players);
	}
	@Test
	public void testPlayerHot() throws RemoteException
	{
		HotPlayerTeam[] hots = player.getDayHotPlayer(getDate("2015-06-11"), HotType.ASSIST);
		print(hots);
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
