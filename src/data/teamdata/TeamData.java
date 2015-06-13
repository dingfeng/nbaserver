package data.teamdata;

import java.awt.Image;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import po.HotPlayerTeam;
import po.TeamHighPO;
import po.TeamNormalPO;
import po.TeamPO;
import po.TeamPlayerImage;
import data.playerdata.PlayerData;
import dataservice.playerdataservice.SeasonType;
import dataservice.teamdataservice.TeamDataService;

public class TeamData extends UnicastRemoteObject implements TeamDataService{
    private Connection conn;
	public TeamData(Connection conn)throws RemoteException
	{
		this.conn = conn;
	}
	@Override
	public TeamPO[] getAllTeamData() {
		String sql = "select * from team";
		TeamPO[] teams = null;
		ArrayList<TeamPO> list = new ArrayList<TeamPO>();
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toTeamPO(results));
			}
			teams = new TeamPO[list.size()];
			list.toArray(teams);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return teams;
	}
    private TeamPO toTeamPO(ResultSet result) throws Exception
    {
    	Blob blob = result.getBlob(2);
    	Image image = PlayerData.blobToImage(blob);
    	String name = result.getString(3);
    	String nameAbridge = result.getString(4);
    	String address = result.getString(5);
		String matchArea = result.getString(6);
		String playerArea = result.getString(7);
		String manage = result.getString(8);
		int foundYear = result.getInt(9);
		TeamPO team = new TeamPO( image,  name,  nameAbridge,  address,
				 matchArea,  playerArea,  manage,  foundYear);
		return team;
    }
	@Override
	public TeamPO findTeam(String teamName) {
		String sql = "select * from team where name_total = ? or name_abr = ?";
		TeamPO team = null;
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, teamName);
			statement.setString(2, teamName);
			ResultSet results = statement.executeQuery();
			if (results.next())
			{
				team = toTeamPO(results);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return team;
	}
	@Override
	public TeamNormalPO getTeamNormalTotal(int season, String teama,
			SeasonType type) {
		String sql = null;
		TeamNormalPO team = null;
		switch(type)
		{
		case REGULAR:
			sql = "select * from team_season_normal where ave = ? and season = ? and teamName = ?";
			break;
		case PLAYOFF:
			sql = "select * from team_season_normal_playoff where ave = ? and season = ? and teamName = ?";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 0);
			statement.setInt(2, season);
			statement.setString(3, teama);
			ResultSet results = statement.executeQuery();
			if(results.next())
			{
			 team = toTeamNormal(results);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return team;
	}
	private TeamNormalPO toTeamNormal(ResultSet result) throws Exception
	{
		String name = result.getString(3);
		int matchNo = result.getInt(4);
		double hitNo = result.getDouble(5);
		double handNo = result.getDouble(6);
		double threeHitNo = result.getDouble(7);
		double threeHandNo = result.getDouble(8);
		double penaltyHitNo = result.getDouble(9);
		double penaltyHandNo = result.getDouble(10);
		double offenseRebs = result.getDouble(11);
		double defenceRebs = result.getDouble(12);
		double rebs = result.getDouble(13);
		double assistNo = result.getDouble(14);  
		double stealsNo = result.getDouble(15);
		double blockNo = result.getDouble(16);
		double mistakesNo = result.getDouble(17);
		double foulsNo = result.getDouble(18);
		double points = result.getDouble(19);
		double hitRate = result.getDouble(20);
		double threeHitRate = result.getDouble(21);
		double penaltyHitRate = result.getDouble(22);  
		double winRate = result.getDouble(23);
		int season = result.getInt("season");
		TeamNormalPO team = new TeamNormalPO( name,  matchNo,  hitNo,  handNo,
				 threeHitNo,  threeHandNo,  penaltyHitNo,
				 penaltyHandNo,  offenseRebs,  defenceRebs,
				 rebs,  assistNo,  stealsNo,  blockNo,
				 mistakesNo,  foulsNo,  points,  hitRate,
				 threeHitRate,  penaltyHitRate,  winRate,season);
		return team;
	}
	private TeamHighPO toTeamHigh(ResultSet result) throws Exception
	{
		String name = result.getString(2);
		double offenseRound = result.getDouble(3);
		double offenseEfficiency = result.getDouble(4);
		double defenceEfficiency = result.getDouble(5);
		double orebsEfficiency = result.getDouble(6);
		double drebsEfficiency = result.getDouble(7);
		double stealsEfficiency = result.getDouble(8);
		double assistEfficiency = result.getDouble(9);
		int season = result.getInt("season");
		TeamHighPO team = new TeamHighPO( name, offenseRound,  offenseEfficiency,
				 defenceEfficiency,  orebsEfficiency,
				 drebsEfficiency,  stealsEfficiency,
				 assistEfficiency,season);
		return team;
	}
	@Override
	public TeamNormalPO[] sortTeamNormalTotaln(int season, String sort, int n,
			SeasonType type) {
		String sql = null;
		TeamNormalPO[] teams = null;
		ArrayList<TeamNormalPO> list = new ArrayList<TeamNormalPO>(n);
		switch(type)
		{
		case REGULAR:
			sql  = "select * from team_season_normal where ave = ? and season = ? order by "+sort +" limit "+String.valueOf(n);
			break;
		case PLAYOFF:
			sql = "select * from team_season_normal_playoff where ave = ? and season = ? order by "+sort +" limit "+String.valueOf(n);
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
				list.add(toTeamNormal(results));
			}
			teams = new TeamNormalPO[list.size()];
			list.toArray(teams);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return teams;
	}
	@Override
	public TeamNormalPO getTeamNormalAve(int season, String teama,
			SeasonType type) {
		String sql = null;
		TeamNormalPO team = null;
		switch(type)
		{
		case REGULAR:
			sql = "select * from team_season_normal where ave = ? and season = ? and teamName = ?";
			break;
		case PLAYOFF:
			sql = "select * from team_season_normal_playoff where ave = ? and season = ? and teamName = ?";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 1);
			statement.setInt(2, season);
			statement.setString(3, teama);
			ResultSet results = statement.executeQuery();
			if(results.next())
			{
			 team = toTeamNormal(results);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return team;
	}
	@Override
	public TeamNormalPO[] sortTeamNormalAven(int season, String sort, int n,
			SeasonType type) {
		String sql = null;
		TeamNormalPO[] teams = null;
		ArrayList<TeamNormalPO> list = new ArrayList<TeamNormalPO>(n);
		switch(type)
		{
		case REGULAR:
			sql  = "select * from team_season_normal where ave = ? and season = ? order by "+sort +" limit "+String.valueOf(n);
			break;
		case PLAYOFF:
			sql = "select * from team_season_normal_playoff where ave = ? and season = ? order by "+sort +" limit "+String.valueOf(n);
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
				list.add(toTeamNormal(results));
			}
			teams = new TeamNormalPO[list.size()];
			list.toArray(teams);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return teams;
	}
	@Override
	public TeamHighPO getTeamHigh(int season, String teama, SeasonType type) {
		String sql = null;
		TeamHighPO team = null;
		switch(type)
		{
		case REGULAR:
			sql = "select * from team_season_high where season = ? and teamName = ?";
			break;
		case PLAYOFF:
			sql = "select * from team_season_high_playoff where season = ? and teamName = ?";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, season);
			statement.setString(2, teama);
			ResultSet results = statement.executeQuery();
			if (results.next())
			{
				team = toTeamHigh(results);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return team;
	}
	@Override
	public TeamHighPO[] sortTeamHighn(int season,String sort, int n, SeasonType type) {
		String sql = null;
		ArrayList<TeamHighPO> list = new ArrayList<TeamHighPO>(n);
		TeamHighPO[] teams = null;
		switch (type)
		{
		case REGULAR:
			sql = "select * from team_season_high where season = ? order by "+sort+" limit "+String.valueOf(n);
			break;
		case PLAYOFF:
			sql = "select * from team_season_high_playeroff where season = ? order by "+sort+" limit "+String.valueOf(n);
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, season);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toTeamHigh(results));
			}
			teams = new TeamHighPO[list.size()];
			list.toArray(teams);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return teams;
	}
	@Override
	public TeamNormalPO[] getTeamSeasonNormalTotal(String teama, SeasonType type) {
		String sql = null;
		ArrayList<TeamNormalPO> list = new ArrayList<TeamNormalPO>();
		TeamNormalPO[] teams = null;
		switch(type)
		{
		case REGULAR:
			sql = "select * from team_season_normal where ave = ? and teamName = ?";
			break;
		case PLAYOFF:
			sql = "select * from team_season_normal_playoff where ave = ? and teamName = ?";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 0);
			statement.setString(2, teama);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toTeamNormal(results));
			}
			teams = new TeamNormalPO[list.size()];
			list.toArray(teams);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return teams;
	}
	@Override
	public TeamNormalPO[] getTeamSeasonNormalAve(String teama, SeasonType type) {
		String sql = null;
		ArrayList<TeamNormalPO> list = new ArrayList<TeamNormalPO>();
		TeamNormalPO[] teams = null;
		switch(type)
		{
		case REGULAR:
			sql = "select * from team_season_normal where ave = ? and teamName = ?";
			break;
		case PLAYOFF:
			sql = "select * from team_season_normal_playoff where ave = ? and teamName = ?";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, 1);
			statement.setString(2, teama);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toTeamNormal(results));
			}
			teams = new TeamNormalPO[list.size()];
			list.toArray(teams);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return teams;
	}
	@Override
	public TeamHighPO[] getTeamSeasonHigh(String teama, SeasonType type) {
		String sql = null;
		ArrayList<TeamHighPO> list = new ArrayList<TeamHighPO>();
		TeamHighPO[] teams = null;
		switch(type)
		{
		case REGULAR:
			sql = "select * from team_season_high where teamName = ?";
			break;
		case PLAYOFF:
			sql = "select * from team_season_high_playoff where teamName = ?";
			break;
		}
		try
		{
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, teama);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toTeamHigh(results));
			}
			teams = new TeamHighPO[list.size()];
			list.toArray(teams);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return teams;
	}
	@Override
	public HotPlayerTeam[] getHotTeam(int season,String sort, SeasonType seasonType)
			throws RemoteException {
		String sql = null;
		HotPlayerTeam[] players = null;
		switch(seasonType)
		{
		case REGULAR:
			sql = "select m.name_total,m.name_abr,t."+sort+" ,m.photo from team m,team_season_normal t where t.season = ? and t.ave = 1 and m.name_abr = t.teamName order by t."+sort+" desc limit 5";
			break;
		case PLAYOFF:
			sql = "select m.name_total,m.name_abr,t."+sort+" ,m.photo from team m,team_season_normal_playoff t where t.season = ? and t.ave = 1 and m.name_abr = t.teamName order by t."+sort+" desc limit 5";
			break;
		}
		try
		{
			ArrayList<HotPlayerTeam> list = new ArrayList<HotPlayerTeam>();
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, season);
			ResultSet results = statement.executeQuery();
			while (results.next())
			{
				list.add(toPlayerTeam(results));
			}
			players = new HotPlayerTeam[list.size()];
			list.toArray(players);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}
	private HotPlayerTeam toPlayerTeam(ResultSet result) throws Exception
	{
		String name_total  = result.getString(1);
		String name_abr = result.getString(2);
		double hotData = result.getDouble(3);
		Image action = PlayerData.blobToImage(result.getBlob(4));
		String name = name_total + " / "+name_abr;
		return new  HotPlayerTeam( action,  name,  hotData);
	}
	@Override
	public TeamPlayerImage[] getAllTeams() throws RemoteException {
		String sql = "select name_abr,photo from team";
		TeamPlayerImage[] images =null;
		try
		{
			ArrayList<TeamPlayerImage> list = new ArrayList<TeamPlayerImage>();
			PreparedStatement statement =  conn.prepareStatement(sql);
	        ResultSet results = statement.executeQuery();
	        while (results.next())
	        {
	        	list.add(new TeamPlayerImage(PlayerData.blobToImage(results.getBlob("photo")),results.getString("name_abr")));
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

}
