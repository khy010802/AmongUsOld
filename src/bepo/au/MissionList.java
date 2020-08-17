package bepo.au;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import bepo.au.base.Mission;
import bepo.au.base.Mission.MissionType;
import bepo.au.missions.*;

public class MissionList {
	
	public static List<Mission> EASY = new ArrayList<Mission>();
	public static List<Mission> HARD = new ArrayList<Mission>();
	
	// EASY
	public final static E_ChartCourse chartcourse = 			new E_ChartCourse(MissionType.EASY, "ChartCourse", "항로 계획", 1, new Location(Main.w, 0, 0, 0));
	public final static E_FixWriting fixwriting = 				new E_FixWriting(MissionType.EASY, "FixWriting", "배선 수리", 3, new Location(Main.w, 0, 0, 0));
	public final static E_StablizeSteering stablizesteering = 	new E_StablizeSteering(MissionType.EASY, "StablizeSteering", "항로 조정", 1, new Location(Main.w, 0, 0, 0));
	
	// HARD
	public final static H_Card card 		= 							new H_Card(MissionType.HARD, "Card", "카드 긁기", 1, new Location(Main.w, 0, 0, 0));
	public final static H_EmptyChute emptychute = 			new H_EmptyChute(MissionType.HARD, "EmptyChute", "산소필터 청소", 1, new Location(Main.w, 0, 0, 0));
	public final static H_InspectSample inspectsample = 	new H_InspectSample(MissionType.HARD, "InspectSample", "샘플 분석", 1, new Location(Main.w, 0, 0, 0));
	public final static H_Scanning scanning =					new H_Scanning(MissionType.HARD, "Scanning", "스캔 제출", 1, new Location(Main.w, 0, 0, 0));

}
