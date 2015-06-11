/**
 *Project Name : nba
 *File Name: TeamDataService.java
 *Package Name : dataservice.teamdataservice
 *Date:2015-6-11下午 13:00：01
 * Copyright(c) 2015,南京大学软件学院13级宇宙无敌工作组 All Rights Reserved
 * 
 */
package dataservice.teamdataservice;

import java.rmi.Remote;
import java.rmi.RemoteException;

import dataservice.playerdataservice.SeasonType;
import po.TeamHighPO;
import po.TeamNormalPO;
import po.TeamPO;

public interface TeamDataService extends Remote{
	//得到所有球队数据
	public TeamPO[] getAllTeamData()throws RemoteException;
	//球队缩写查找球队
	public TeamPO findTeam(String teamName)throws RemoteException;
	//获得球队的赛季赛季总数据
	public TeamNormalPO getTeamNormalTotal(int season, String teama,SeasonType type)throws RemoteException;
	//获得球队排序赛季总数据
	public TeamNormalPO[] sortTeamNormalTotaln(int season,String sort, int n,SeasonType type)throws RemoteException;
	//获得球队赛季场均数据
	public TeamNormalPO getTeamNormalAve(int season, String teama,SeasonType type)throws RemoteException;
	//获得球员排序场均数据
	public TeamNormalPO[] sortTeamNormalAven(int season, String sort, int n,SeasonType type)throws RemoteException;
	//获得球员某个赛季的高阶数据
	public TeamHighPO getTeamHigh(int season, String teama,SeasonType type)throws RemoteException;
	//获得球员排序赛季高阶数据
	public TeamHighPO[] sortTeamHighn(int season, String sort, int n,SeasonType type)throws RemoteException;
//	public TeamHighPO[] sortTeamHighHis(String sort, int n);
	//获得某个球队的所有赛季的总数据
	public TeamNormalPO[] getTeamSeasonNormalTotal(String teama,SeasonType type)throws RemoteException;
	//获得某个球员所有赛季的场均数据
	public TeamNormalPO[] getTeamSeasonNormalAve(String teama,SeasonType type)throws RemoteException;
	//获得某个球员所有赛季的高阶数据
	public TeamHighPO[] getTeamSeasonHigh(String teama,SeasonType type)throws RemoteException;
}
