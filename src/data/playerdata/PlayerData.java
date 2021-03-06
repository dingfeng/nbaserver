package data.playerdata;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;

import DBtool.MatchTeamPO;
import po.HPlayerPO;
import po.HotPlayerTeam;
import po.HotType;
import po.MatchPlayerPO;
import po.MatchesPO;
import po.PlayerHighPO;
import po.PlayerImage;
import po.PlayerNormalPO;
import po.PlayerPO;
import po.TeamPlayerImage;
import server.Server;
import tool.MinBinaryHeap;
import data.matchdata.MatchData;
import dataservice.playerdataservice.PlayerDataService;
import dataservice.playerdataservice.SeasonType;

public class PlayerData extends UnicastRemoteObject implements PlayerDataService{
    private Connection conn;
    private HashMap<String,Image> player_map = new HashMap<String,Image>();
    private String imagePath = "H:/image/player";
	public PlayerData(Connection conn) throws RemoteException
	{
		this.conn = conn;
		this.loadImage();
	}
	public PlayerPO[] getAllActivePlayerData() {
		String sql = "select player_name,num,position,heightfeet,heightinch,weight,birth,age,exp,school from mplayer";
		ArrayList<PlayerPO> list = new ArrayList<PlayerPO>(3500);
		PlayerPO[] players = null;
		try
		{
		  PreparedStatement statement = conn.prepareStatement(sql);
		  ResultSet results = statement.executeQuery();
		  while (results.next())
		  {
			list.add(toPlayerPO(results));
		  }
		  players = new PlayerPO[list.size()];
		  list.toArray(players);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}

	private PlayerPO toPlayerPO(ResultSet result) throws Exception
	{
		String name = result.getString("player_name");
		int number = result.getInt("num");
		String position = result.getString("position");
		int heightfeet = result.getInt("heightfeet");
		int heightinch = result.getInt("heightinch");
		int weight = result.getInt("weight");
		String birth = result.getString("birth");
		int age = result.getInt("age");
		int exp = result.getInt("exp");
		String school = result.getString("school");
		String[] teams = getTeam(name);
		PlayerPO player = new PlayerPO(   name,  number,
				 position,  heightfeet,  heightinch,  weight,
				 birth,  age,  exp,  school, teams[0],teams[1]);
		return player;
	}
	
	
	 public static  Image blobToImage(Blob blob)
	 {
		 if (blob == null)
			 return null;
		 byte[] bytes = null;
		 try {
			bytes = blob.getBytes(1,(int)blob.length());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 return Toolkit.getDefaultToolkit().createImage(bytes,0,bytes.length);
	 }
	@Override
	public HPlayerPO findPlayer(String playerName) {
		String sql = "select * from hplayerinfo  where player_name = ?";
		ArrayList<HPlayerPO> list = new ArrayList<HPlayerPO>(300);
		HPlayerPO player = null;
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, playerName);
			ResultSet results = statement.executeQuery();
		   String name = null;
		   String totalName = null;
		   String position = null;
		   String height = null;
		   String weight = null;
		   String birthday = null;
		   String birthCity = null;
		   String high_school = null;
		   String university = null;
		   String num = null;
		   if(results.next())
		   {
		     name = results.getString("player_name");
		     totalName = results.getString("total_name");
		     position = results.getString("position");
		     height = results.getString("height");
		     weight = results.getString("weight");
		     birthday = results.getString("birthday");
		     birthCity = results.getString("birthcity");
		     high_school = results.getString("high_school");
		     String[] teams  = getTeam(name);
		     player = new HPlayerPO( name,  totalName,  position,  height,
		    			 weight,  birthday,  birthCity,  high_school,
		    			 university,  num, teams[0],teams[1],getImage(name));
		     list.add(player);
		   }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return player;
	}
	@Override
	public PlayerHighPO getPlayerHigh(int season, String playerName, SeasonType type) {
		String sql = null;
		PlayerHighPO player = null;
		switch (type)
		{
		case REGULAR:
			sql = "select * from player_season_high where season = ? and playerName = ? ";
			break;
		case PLAYOFF:
			sql = "select * from player_season_high_playoff where season = ? and playerName = ?";
			break;
		}

		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, season);
			statement.setString(2, playerName);
			ResultSet result = statement.executeQuery();
			if (result.next())
			{
				player = toPlayerHigh(result);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return player;
	}
	private PlayerHighPO toPlayerHigh(ResultSet result) throws Exception
	{
		String playerName = result.getString("playerName");
		String teamName = result.getString("teamName");
		double efficiency = result.getDouble("efficiency");
		double gmScEfficiency = result.getDouble("GmScEfficiency");
		double trueHitRate = result.getDouble("trueHitRate");
		double hitEfficiency = result.getDouble("hitEfficiency");
		double rebEfficiency = result.getDouble("rebEfficiency");
		double offenseRebsEfficiency = result.getDouble("offenseRebsEfficiency");
		double defenceRebsEfficiency = result.getDouble("defenceRebsEfficiency");
		double assistEfficiency = result.getDouble("assistEfficiency");
		double stealsEfficiency = result.getDouble("stealsEfficiency");
		double blockEfficiency = result.getDouble("blockEfficiency");
		double mistakeEfficiency = result.getDouble("mistakeEfficiency");
		double useEfficiency = result.getDouble("useEfficiency");
		int season = result.getInt("season");
		PlayerHighPO player = new PlayerHighPO( playerName,  teamName,  efficiency,
				 gmScEfficiency,  trueHitRate,  hitEfficiency,
				 rebEfficiency,  offenseRebsEfficiency,
				 defenceRebsEfficiency,  assistEfficiency,
				 stealsEfficiency,  blockEfficiency,
				 mistakeEfficiency,  useEfficiency, season);
		return player;
	}
	@Override
	public PlayerHighPO[] sortPlayerHighn(int season, String sort, int n,
			SeasonType type) {
		String sql = null;
		PlayerHighPO[] players = null;
		ArrayList<PlayerHighPO> list = new ArrayList<PlayerHighPO>(n);
		switch (type)
		{
		case REGULAR:
			sql = "select * from player_season_high where season = ? order by "+sort +" limit "+String.valueOf(n);
			break;
		case PLAYOFF:
			sql = "select * from player_season_high_playoff where season = ?  order by "+sort +" limit "+String.valueOf(n);
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, season);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toPlayerHigh(results));
			}
		    players = new PlayerHighPO[list.size()];
		    list.toArray(players);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}
	@Override
	public PlayerNormalPO getPlayerNormalTotal(int season, String playerName,
			SeasonType type) {
		String sql = null;
		PlayerNormalPO player = null;;
		switch(type)
		{
		case REGULAR:
			sql = "select * from player_season_normal where ave = ? and season = ? and player_name = ? ";
			break;
		case PLAYOFF:
			sql = "select * from player_season_normal_playoff where ave = ? and season = ? and player_name = ?";
			break;
		}
		try
		{
		 	PreparedStatement statement = conn.prepareStatement(sql);
		 	statement.setInt(1, 0);
		 	statement.setInt(2, season);
		 	statement.setString(3, playerName);
		 	ResultSet result = statement.executeQuery();
		 	if(result.next())
		 	{
		 		player  = toPlayerNormal(result);
		 	}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return player;
	}
	private PlayerNormalPO toPlayerNormal(ResultSet result) throws Exception
	{
		String name = result.getString(3);
		String team = result.getString(4);
		int matchNo = result.getInt(5);
		double firstServiceNo = result.getDouble(6);
		double rebs = result.getDouble(7);
		double assistNo = result.getDouble(8);
		double time = result.getDouble(9);
		double offendRebsNo = result.getDouble(10);
		double defenceRebsNo = result.getDouble(11);
		double stealsNo = result.getDouble(12);
		double blockNo = result.getDouble(13);
		double mistakesNo = result.getDouble(14); 
		double foulsNo = result.getDouble(15);
		double points = result.getDouble(16);
		double minute = result.getDouble(17);
		double hitNo = result.getDouble(18);
		double handNo = result.getDouble(19);
		double hitRate = result.getDouble(20);
		double penaltyHandNo = result.getDouble(21);
		double penaltyHitNo = result.getDouble(22);
		double penaltyHitRate = result.getDouble(23);
		double threeHitNo = result.getDouble(24);
		double threeHandNo = result.getDouble(25);
		double threeHitRate = result.getDouble(26);
		double twoPair = result.getDouble(27);
		double points_uprate = result.getDouble(28);
		double rebs_uprate = result.getDouble(29);
		double help_uprate = result.getDouble(30);
		double scoring_rebound_assist = result.getDouble(31);
		int season = result.getInt("season");
		PlayerNormalPO player = new PlayerNormalPO( name,  team,  matchNo,
				 firstServiceNo,  rebs,  assistNo,  time,
				 offendRebsNo,  defenceRebsNo,  stealsNo,
				 blockNo,  mistakesNo,  foulsNo,  points,
				 minute,  hitNo,  handNo,  hitRate,
				 penaltyHandNo,  penaltyHitNo,  penaltyHitRate,
				 threeHitNo,  threeHandNo,  threeHitRate,
				 twoPair,  points_uprate,  rebs_uprate,
				 help_uprate,  scoring_rebound_assist,season);
		return player;
	}
	@Override
	public PlayerNormalPO[] sortPlayerNormalTotal(int season, String sort,
			int n, SeasonType type) {
		PlayerNormalPO[] players = null;
		ArrayList<PlayerNormalPO> list = new ArrayList<PlayerNormalPO>(n);
		String sql = null;
		switch (type)
		{
		case REGULAR:
			sql = "select * from player_season_normal where ave = ? and season = ? order by  "+sort+" limit "+String.valueOf(n); 
			break;
		case PLAYOFF:
			sql = "select * from player_season_normal_playoff where ave = ? and season = ? order by  "+sort+" limit "+String.valueOf(n);
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 0);
			statement.setInt(2, season);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toPlayerNormal(results));
			}
			players = new PlayerNormalPO[list.size()];
			list.toArray(players);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}
	@Override
	public PlayerNormalPO getPlayerNormalAve(int season, String playerName,
			SeasonType type) {
		String sql = null;
		PlayerNormalPO player = null;;
		switch(type)
		{
		case REGULAR:
			sql = "select * from player_season_normal where ave = ? and season = ? and player_name = ? ";
			break;
		case PLAYOFF:
			sql = "select * from player_season_normal_playoff where ave = ? and season = ? and player_name = ?";
			break;
		}
		try
		{
		 	PreparedStatement statement = conn.prepareStatement(sql);
		 	statement.setInt(1, 1);
		 	statement.setInt(2, season);
		 	statement.setString(3, playerName);
		 	ResultSet result = statement.executeQuery();
		 	if(result.next())
		 	{
		 		player  = toPlayerNormal(result);
		 	}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return player;
	}
	@Override
	public PlayerNormalPO[] sortPlayerNormalAven(int season, String sort, int n,
			SeasonType type) {
		PlayerNormalPO[] players = null;
		ArrayList<PlayerNormalPO> list = new ArrayList<PlayerNormalPO>(n);
		String sql = null;
		switch (type)
		{
		case REGULAR:
			sql = "select * from player_season_normal where ave = ? and season = ? order by  "+sort+" limit "+String.valueOf(n); 
			break;
		case PLAYOFF:
			sql = "select * from player_season_normal_playoff where ave = ? and season = ? order by  "+sort+" limit "+String.valueOf(n);
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 1);
			statement.setInt(2, season);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toPlayerNormal(results));
			}
			players = new PlayerNormalPO[list.size()];
			list.toArray(players);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}
	@Override
	public PlayerNormalPO[] getPlayerAllSeasonsTotal(String playerName,
			SeasonType type) {
		String sql = null;
		PlayerNormalPO[] players = null;
		ArrayList<PlayerNormalPO> list = new ArrayList<PlayerNormalPO>();
		switch (type)
		{
		case REGULAR:
			sql = "select * from player_season_normal where ave = ? and player_name  = ? order by season desc";
			break;
		case PLAYOFF:
			sql = "select * from player_season_normal_playoff where ave = ? and player_name = ? order by season desc";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 0);
			statement.setString(2, playerName);
			ResultSet results = statement.executeQuery();
			while(results.next())
			{
				list.add(toPlayerNormal(results));
			}
			players = new PlayerNormalPO[list.size()];
			list.toArray(players);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}
	@Override
	public PlayerNormalPO[] getPlayerAllSeasonsAve(String playerName,
			SeasonType type) {
		String sql = null;
		PlayerNormalPO[] players = null;
		ArrayList<PlayerNormalPO> list = new ArrayList<PlayerNormalPO>();
		switch (type)
		{
		case REGULAR:
			sql = "select * from player_season_normal where ave = ? and player_name  = ? order by season desc";
			break;
		case PLAYOFF:
			sql = "select * from player_season_normal_playoff where ave = ? and player_name = ? order by season desc";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 1);
			statement.setString(2, playerName);
			ResultSet results = statement.executeQuery();
			while(results.next())
			{
				list.add(toPlayerNormal(results));
			}
			players = new PlayerNormalPO[list.size()];
			list.toArray(players);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}
	@Override
	public PlayerHighPO[] getPlayerAllSeasonsHigh(String playerName, SeasonType type) {
		String sql = null;
		PlayerHighPO[] players = null;
		ArrayList<PlayerHighPO> list = new ArrayList<PlayerHighPO>();
		switch(type)
		{
		case REGULAR:
			sql = "select * from player_season_high where playerName = '"+playerName+"' order by season desc";
			break;
		case PLAYOFF:
			sql = "select * from player_season_high_playoff where playerName = '"+playerName+"' order by season desc";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toPlayerHigh(results));
			}
			players = new PlayerHighPO[list.size()];
			list.toArray(players);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}
   public double getSeasonAveData(int season,String data)
   {
	   String sql  = "select sum(?)/sum(matchNo) where season = ? group by season";
	   double result = -1;
	   try
	   {
		   PreparedStatement statement = conn.prepareStatement(sql);
		   statement.setString(1, data);
		   statement.setInt(2, season);
		   ResultSet results = statement.executeQuery();
		   if (results.next())
		   {
			   result = results.getDouble(1);
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return result;
   }
@Override
public String[] fuzzilySearch(String info) {
	String sql = "select player_name from hplayerinfo where player_name like '"+info+"%'";
	ArrayList<String> list = new ArrayList<String>();
	String[] names = null;
	try
	{
		PreparedStatement statement = conn.prepareStatement(sql);
		ResultSet results = statement.executeQuery();
		while (results.next())
		{
			list.add(results.getString(1));
		}
		names = new String[list.size()];
		list.toArray(names);
	}
	catch (Exception  e)
	{
		e.printStackTrace();
	}
	return names;
}

@Override
public HPlayerPO[] getHPlayerByIni(String ini) {
	String sql = "select * from hplayerinfo  where player_name like '"+ini+"%'";
	ArrayList<HPlayerPO> list = new ArrayList<HPlayerPO>(300);
	HPlayerPO[] players = null;
	HPlayerPO player = null;
	try
	{
		PreparedStatement statement = conn.prepareStatement(sql);
		ResultSet results = statement.executeQuery();
	   String name = null;
	   String totalName = null;
	   String position = null;
	   String height = null;
	   String weight = null;
	   String birthday = null;
	   String birthCity = null;
	   String high_school = null;
	   String university = null;
	   String num = null;
	   while (results.next())
	   {
	     name = results.getString("player_name");
	     totalName = results.getString("total_name");
	     position = results.getString("position");
	     height = results.getString("height");
	     weight = results.getString("weight");
	     birthday = results.getString("birthday");
	     birthCity = results.getString("birthcity");
	     high_school = results.getString("high_school");
	     String[] teams  = getTeam(name);
	     player = new HPlayerPO( name,  totalName,  position,  height,
	    			 weight,  birthday,  birthCity,  high_school,
	    			 university,  num, teams[0],teams[1],getImage(name));
	     list.add(player);
	   }
	   players = new HPlayerPO[list.size()];
	   list.toArray(players);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return players;
}

  private String[] getTeam(String playerName)
  {
	  String[] teams = new String[2];
//	  String sql = "select a.teama from match_player a where a.player_name = ? and a.match_id = (select max(m.match_id) from "
//	  		+ " match_player m where m.player_name = ? group by m.player_name)";
//	  String s1 = "select match_area from team where name_abr = ?";
	  String sql = "select t.name_abr,t.match_area from team t where t.name_abr = (select a.team  from hplayerinfo a where a.player_name = ?)";
	  try
	  {
		  PreparedStatement statement = conn.prepareStatement(sql);
		  statement.setString(1, playerName);
		  ResultSet result = statement.executeQuery();
		  if (result.next())
		  {
			  teams[0] = result.getString(1);
			  teams[1] = result.getString(2);
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return teams;
  }

  public Image getImage(String playerName)
  {
	  
//	  String sql = "select photo_portrait from mplayer where player_name = ?";
//	  Image image = null;
//	  try
//	  {
//		  PreparedStatement statement = conn.prepareStatement(sql);
//		  statement.setString(1, playerName);
//		  ResultSet result = statement.executeQuery();
//		  if (result.next())
//		  {
//			  image = blobToImage(result.getBlob(1));
//		  }
//	  }
//	  catch (Exception e)
//	  {
//		  e.printStackTrace();
//	  }
	  return player_map.get(playerName);
  }
  
@Override
public PlayerNormalPO[] getSeasonPlayerNormalAve(int season, SeasonType type) {
	return this.sortPlayerNormalAven(season, "player_name", 1000, type);
}
@Override
public PlayerNormalPO[] getSeasonPlayerNormalTotal(int season, SeasonType type) {
	return this.sortPlayerNormalTotal(season, "player_name", 1000, type);
}
@Override
public PlayerHighPO[] getSeasonPlayerHigh(int season, SeasonType type) {
	return this.sortPlayerHighn(season, "player_name", 1000, type);
}
@Override
public PlayerPO[] getPlayersOfTeam(String team) {
	String sql = "select a.player_name,a.num,a.position,a.heightfeet,a.heightinch,a.weight,a.birth,a.age,a.exp,a.school from mplayer a "
			+ " where a.team = ?";
		ArrayList<PlayerPO> list = new ArrayList<PlayerPO>(3500);
		PlayerPO[] players = null;
		 PreparedStatement statement = null;
		try
		{
		 statement = conn.prepareStatement(sql);
		  statement.setString(1, team);
		  ResultSet results = statement.executeQuery();
		  while (results.next())
		  {
			list.add(toPlayerPO(results));
		  }
		  players = new PlayerPO[list.size()];
		  list.toArray(players);
		}
		catch (Exception e)
		{
			System.out.println(statement);
			e.printStackTrace();
		}
		return players;
}
@Override
public PlayerPO[] screenPlayer(String sort, String match_area, String player_area,String position,
		int n) {
	String sql = "select * from mplayer m ";
	String s1 = " m.position like "+"'%"+position+"%'  ";
	String s2 = "exists(select p.match_area from team  p where ";
	String s31 = " p.match_area = '"+match_area+"'";
	String s4 = " p.player_area = '"+player_area+"'";
	String s5 = " and p.name_abr = m.team)";
	if (match_area != null || player_area != null || position != null)
	{
		sql += " where ";
	}
	if (sort == null)
	{
		sort = " player_name desc ";
	}
	String s3 = " order by "+" "+sort +" limit "+n;
	if (position!=null)
	{
		sql+=s1;
	}
	if (match_area != null || player_area != null)
	{
		if (position != null)
		{
			sql += " and ";
		}
		sql += s2;
		if (match_area != null)
		{
			sql += s31;
		}
		if (player_area != null)
		{
			if (match_area != null)
				sql += " and ";
			sql += s4;
		}
		sql += s5;
	}
	sql += s3;
	ArrayList<PlayerPO> list = new ArrayList<PlayerPO>(3500);
	PlayerPO[] players = null;
	 PreparedStatement statement = null;
	try
	{
	  statement = conn.prepareStatement(sql);
	  ResultSet results = statement.executeQuery();
	  while (results.next())
	  {
		list.add(toPlayerPO(results));
	  }
	  players = new PlayerPO[list.size()];
	  list.toArray(players);
	}
	catch (Exception e)
	{
		System.out.println(statement);
		e.printStackTrace();
	}
	return players;
    }

private MatchPlayerPO toMatchPlayer(ResultSet results) throws Exception
{
	MatchPlayerPO matchPlayer = null;
	String name= null;
	String location= "";
	double time=0;
	int hitNo=0;
	int handNo=0;
	int threeHitNo=0;
	int threeHandNo=0;
	int penaltyHitNo=0;
	int penaltyHandNo=0;
	int offenseRebs=0;
	int defenceRebs=0;
	int rebs=0;
	int help=0;
	int stealsNo=0;
	int blockNo=0;
	int mistakesNo=0;
	int foulsNo=0;;
	int first = 0;
	int matchId = -1;
	String teama = null;
//		"select player_name,courtTime,hitNo,handNo,threeHitNo,threeHandNo,penaltyHitNo,penaltyHandNo,"
//    			+ "offenseRebs,defenceRebs,rebs,assist,steal,blockno,mistakeno,fouls,score, "
//    			+ "from match_player where match_id = ? and teama = ?";
	    matchId = results.getInt(1);
		name = results.getString(2);
		time = results.getInt(4);
		hitNo = results.getInt(5);
		handNo=results.getInt(6);
		threeHitNo = results.getInt(7);
		threeHandNo = results.getInt(8);
		penaltyHitNo = results.getInt(9);
		penaltyHandNo = results.getInt(10);
		offenseRebs = results.getInt(11);
		defenceRebs = results.getInt(12);
		rebs = results.getInt(13);
		help = results.getInt(14);
		stealsNo = results.getInt(15);
		blockNo = results.getInt(16);
		mistakesNo = results.getInt(17);
		foulsNo = results.getInt(18);
		teama = results.getString(20);
		first = results.getInt(21);
		matchPlayer = new MatchPlayerPO( name,  location,  time,  hitNo,
				 handNo,  threeHitNo,  threeHandNo,  penaltyHitNo,
				 penaltyHandNo,  offenseRebs,  defenceRebs,  rebs,
				 help,  stealsNo,  blockNo,  mistakesNo,
				 foulsNo);
		if (first == 1)
		{
			matchPlayer.setFirst();
		}
		matchPlayer.setTeamnameAbridge(teama);
		matchPlayer.setMatchId(matchId);
		
	return matchPlayer;
}

 private String[] getMatchInfo(int matchId)
 {
	 String sql = "select team_host,team_guest,match_date from matches where match_id = ?";
	 String[] results = null;
	 try
	 {
		 PreparedStatement statement = conn.prepareStatement(sql);
		 statement.setInt(1, matchId);
		 ResultSet result = statement.executeQuery();
		 String teamHost = null;
		 String teamGuest = null;
		 String date = null;
		 if (result.next())
		 {
			 teamHost =  result.getString("team_host");
			 teamGuest = result.getString("team_guest");
			 date = result.getString("match_date");
		 }
		 results = new String[3];
		 results[0] = teamHost;
		 results[1] = teamGuest;
		 results[2] =  date;
	 }
	 catch(Exception e)
	 {
		 e.printStackTrace();
	 }
	 return results;
 }

public MatchPlayerPO[] getSeasonMatches(int season, String name, SeasonType type) {
	String sql = "select * from nba.match_player where match_id > ? and match_id < ? and player_name = ? order by match_id desc";
	int[] id_scope = null;
	MatchPlayerPO[] players;
	ArrayList<MatchPlayerPO> list = new ArrayList<MatchPlayerPO>();
	
	switch (type)
	{
	case REGULAR:
		id_scope = MatchData.getMatchIdScope(season);
		break;
	case PLAYOFF:
		id_scope = MatchData.getPlayerOffMatchId(season);
		break;
	}
	try
	{
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, id_scope[0]);
		statement.setInt(2, id_scope[1]);
		statement.setString(3, name);
		ResultSet results = statement.executeQuery();
		MatchPlayerPO player = null;
		String[] matchInfo = null;
		String team = null;
		String vsTeam = null;
		while (results.next())
		{
			player = toMatchPlayer(results);
			list.add(player);
			matchInfo = this.getMatchInfo(player.getMatchId());
			team = player.getTeamnameAbridge();
			vsTeam = matchInfo[0].equals(team) ? matchInfo[1] : matchInfo[0];
			player.setDate(matchInfo[2]);
			player.setVSTeam(vsTeam);
		}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	players = new MatchPlayerPO[list.size()];
	list.toArray(players);
	return players;
}
@Override
public PlayerNormalPO[] getSeasonPlayerNormalOfTeam(int season, SeasonType type,
		String teamName) {
	String sql = null;
	PlayerNormalPO[] result = null;
	switch (type)
	{
	case REGULAR:
		sql =  "select * from player_season_normal where season = ? and team = ? and ave = 1";
		break;
	case PLAYOFF:
		sql = "select * from player_season_normal_playoff where season = ? and team =? and ave = 1";
		break;
	}
	try
	{
		ArrayList<PlayerNormalPO> list = new  ArrayList<PlayerNormalPO>();
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, season);
		statement.setString(2, teamName);
		ResultSet results = statement.executeQuery();
		while (results.next())
		{
			list.add(this.toPlayerNormal(results));
		}
		result = new PlayerNormalPO[list.size()];
		list.toArray(result);
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return result;
}

@Override
public PlayerImage getPlayerImage(String playerName) {
	return new PlayerImage(this.getImage(playerName));
}
@Override
public PlayerPO findActivePlayerPO(String playerName) throws RemoteException
{
	String sql = "select player_name,num,position,heightfeet,heightinch,weight,birth,age,exp,school from mplayer where player_name = ?";
	PlayerPO player = null;
	try
	{
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, playerName);
		ResultSet result = statement.executeQuery();
		if (result.next())
		{
			player = toPlayerPO(result);
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return player;
}
@Override
public String[] fuzzilySearchActivePlayer(String info) throws RemoteException {
	String sql = "select player_name from mplayer where player_name like '"+info+"%'";
	String[] result = null;
	try
	{
		PreparedStatement statement = conn.prepareStatement(sql);
		ResultSet results = statement.executeQuery();
		ArrayList<String> list = new ArrayList<String>();
		while (results.next())
		{
			list.add(results.getString(1));
		}
		result = new String[list.size()];
		list.toArray(result);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return result;
}
@Override
public PlayerPO[] fuzzilySearchAvtivePlayerPO(String info)
		throws RemoteException {
	String sql = "select player_name,num,position,heightfeet,heightinch,weight,birth,age,exp,school from mplayer where player_name like '"+info+"%'";
	ArrayList<PlayerPO> list = new ArrayList<PlayerPO>(50);
	PlayerPO[] players = null;
	try
	{
	  PreparedStatement statement = conn.prepareStatement(sql);
	  ResultSet results = statement.executeQuery();
	  while (results.next())
	  {
		list.add(toPlayerPO(results));
	  }
	  players = new PlayerPO[list.size()];
	  list.toArray(players);
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return players;
}

@Override
public TeamPlayerImage[] getAllPlayerImage() throws RemoteException {
	String sql = "select player_name from mplayer";
	TeamPlayerImage[] images = null;
	try
	{
		ArrayList<TeamPlayerImage> list = new ArrayList<TeamPlayerImage>();
		PreparedStatement statement = conn.prepareStatement(sql);
		ResultSet results = statement.executeQuery();
		while (results.next())
		{
			String name = results.getString("player_name");
			list.add(new TeamPlayerImage(getImage(name),name));
		}
		images = new TeamPlayerImage[list.size()];
		list.toArray(images);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return images;
}

int season1;
private  String convertDate(Date date)
{
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	String result = format.format(date);
	this.season1 = Integer.parseInt(result.substring(0, 4));
	String s = result.substring(5, 10);
	if (s.charAt(0) == '0')
	{
		--season1;
	}
	return s;
}
@Override
public HotPlayerTeam[] getDayHotPlayer(Date date1,HotType type) throws RemoteException 
{
	MatchesPO[] matches= Server.matchData.getMatches(date1);
	MinBinaryHeap<MatchPlayerPO> heap = new MinBinaryHeap<MatchPlayerPO>();
	MatchPlayerPO[] temp = null;
	HotPlayerTeam[] players = new HotPlayerTeam[5];
	MatchesPO match = null;
	for (int i = 0; i < matches.length; ++i)
	{
		match = Server.matchData.getMatchById(matches[i].getMatchId());
		temp = match.getTeam1().getPlayers();
		for (MatchPlayerPO p : temp)
		{
			setHotData(p,type);
			heap.insert(p);
		}
		temp = match.getTeam2().getPlayers();
		for (MatchPlayerPO p : temp)
		{
			setHotData(p,type);
			heap.insert(p);
		}
	}
	String name = null;
	Image image = null;
	double hotData = -1;
	MatchPlayerPO player = null;
	for (int i = 0; i < 5; ++i)
	{
		player = heap.deleteMin();
		name = player.getName();
		image = this.getImage(name);
		hotData = player.getHotData();
		players[i] = new HotPlayerTeam(image,name,hotData);
	}
	return players;
}

private void  setHotData(MatchPlayerPO player, HotType type)
{
	double hotData = -1;
	switch (type)
	{
	case ASSIST :
		hotData = player.getHelp();
		break;
	case BLOCK:
		hotData = player.getBlockNo();
		break;
	case REBS:
		hotData = player.getRebs();
		break;
	case SCORES:
		hotData = player.getPoints();
		break;
	case STEALS:
		hotData = player.getStealsNo();
		break;
	}
	player.setHotData(hotData);
}
private void loadImage()
{
	File file = new File(imagePath);
	File[] files = file.listFiles();
	String[] array = null;
	String filename = null;
	Image image = null;
	for (File f:files)
	{
		filename = f.getName();
		array = filename.split("\\.");
		try {
			image = ImageIO.read(f);
			player_map.put(array[0], image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public MatchPlayerPO[] getSeasonMatchesn(int season, String name, SeasonType type,int n) {
	String sql = "select * from nba.match_player where match_id > ? and match_id < ? and player_name = ? order by match_id desc limit "+n;
	int[] id_scope = null;
	MatchPlayerPO[] players;
	ArrayList<MatchPlayerPO> list = new ArrayList<MatchPlayerPO>();
	
	switch (type)
	{
	case REGULAR:
		id_scope = MatchData.getMatchIdScope(season);
		break;
	case PLAYOFF:
		id_scope = MatchData.getPlayerOffMatchId(season);
		break;
	}
	try
	{
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, id_scope[0]);
		statement.setInt(2, id_scope[1]);
		statement.setString(3, name);
		ResultSet results = statement.executeQuery();
		MatchPlayerPO player = null;
		String[] matchInfo = null;
		String team = null;
		String vsTeam = null;
		while (results.next())
		{
			player = toMatchPlayer(results);
			list.add(player);
			matchInfo = this.getMatchInfo(player.getMatchId());
			team = player.getTeamnameAbridge();
			vsTeam = matchInfo[0].equals(team) ? matchInfo[1] : matchInfo[0];
			player.setDate(matchInfo[2]);
			player.setVSTeam(vsTeam);
		}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	players = new MatchPlayerPO[list.size()];
	list.toArray(players);
	return players;
}
}
