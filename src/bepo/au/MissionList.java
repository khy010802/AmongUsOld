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
	public final static E_ChartCourse chartcourse = 			new E_ChartCourse(MissionType.EASY, "ChartCourse", "�׷� ��ȹ", 1, new Location(Main.w, 0, 0, 0));
	public final static E_FixWriting fixwriting = 				new E_FixWriting(MissionType.EASY, "FixWriting", "�輱 ����", 3, new Location(Main.w, 0, 0, 0));
	public final static E_StablizeSteering stablizesteering = 	new E_StablizeSteering(MissionType.EASY, "StablizeSteering", "�׷� ����", 1, new Location(Main.w, 0, 0, 0));
	
	// HARD
	public final static H_Card card 		= 							new H_Card(MissionType.HARD, "Card", "ī�� �ܱ�", 1, new Location(Main.w, 0, 0, 0));
	public final static H_EmptyChute emptychute = 			new H_EmptyChute(MissionType.HARD, "EmptyChute", "������� û��", 1, new Location(Main.w, 0, 0, 0));
	public final static H_InspectSample inspectsample = 	new H_InspectSample(MissionType.HARD, "InspectSample", "���� �м�", 1, new Location(Main.w, 0, 0, 0));
	public final static H_Scanning scanning =					new H_Scanning(MissionType.HARD, "Scanning", "��ĵ ����", 1, new Location(Main.w, 0, 0, 0));

}
