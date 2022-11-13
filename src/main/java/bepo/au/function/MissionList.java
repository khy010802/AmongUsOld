package bepo.au.function;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import bepo.au.base.Mission;
import bepo.au.base.Mission.MissionType;
import bepo.au.base.Sabotage;
import bepo.au.missions.*;
import bepo.au.sabo.*;

public class MissionList {
	
	public static List<Sabotage> SABOTAGE = new ArrayList<Sabotage>();
	public static List<Mission> COMMON = new ArrayList<Mission>();
	public static List<Mission> EASY = new ArrayList<Mission>();
	public static List<Mission> HARD = new ArrayList<Mission>();
	
	// COMMON 2개
	public final static C_FixWriting fixwriting = 				new C_FixWriting(MissionType.COMMON, "FixWiring", "배선 수리", 3, (Location) null);
	public final static C_Card card 		= 							new C_Card(MissionType.COMMON, "Card", "카드 긁기", 1, (Location) null);
	
	// EASY 8개
	public final static E_ChartCourse chartcourse = 			new E_ChartCourse(MissionType.EASY, "ChartCourse", "항로 계획", 1, (Location) null);
	public final static E_StablizeSteering stablizesteering = 	new E_StablizeSteering(MissionType.EASY, "StablizeSteering", "항로 조정", 1, (Location) null);
	public final static E_EmptyChute emptychute = 			new E_EmptyChute(MissionType.EASY, "EmptyChute", "산소필터 청소", 1, (Location) null);
	public final static E_ActivatingShield activatingshield = 	new E_ActivatingShield(MissionType.EASY, "ActivatingShield", "실드 고치기", 1, (Location) null);
	public final static E_DivertPower divertpower =			new E_DivertPower(MissionType.EASY, "DivertPower", "에너지 전환하기", 2, (Location) null);
	public final static E_DistributePower distributepower =	new E_DistributePower(MissionType.EASY, "DistributePower", "배전기 조정하기", 1, (Location) null);
	public final static E_OpenManifold openmanifold =		new E_OpenManifold(MissionType.EASY, "OpenManifold", "매니폴드 열기", 1, (Location) null);
	public final static E_Data data =								new E_Data(MissionType.EASY, "Data", "데이터 전송하기", 2, (Location) null);
	 
	// HARD 7개
	public final static H_InspectSample inspectsample = 	new H_InspectSample(MissionType.HARD, "InspectSample", "샘플 분석", 1, (Location) null);
	public final static H_Scanning scanning =					new H_Scanning(MissionType.HARD, "Scanning", "스캔 제출", 1, (Location) null);
	public final static H_ActivatingReactor activatingreactor = new H_ActivatingReactor(MissionType.HARD, "ActivatingReactor", "원자로 가동하기", 1, (Location) null);
	public final static H_AlignEngine alignengine =			new H_AlignEngine(MissionType.HARD, "AlignEngine", "엔진 출력 정렬시키기", 2, (Location) null);
	public final static H_Gas gas = 								new H_Gas(MissionType.HARD, "Gas", "연료 공급하기", 4, (Location) null);
	public final static H_Shooting shooting =					new H_Shooting(MissionType.HARD, "Shooting", "소행성 파괴하기", 1, (Location) null);
	public final static H_EmptyGarbage emptygarbage = 	new H_EmptyGarbage(MissionType.HARD, "EmptyGarbage", "쓰레기 버리기", 2, (Location) null);
	
	// SABOTAGE 5개
	public final static S_Communication communication =	new S_Communication(MissionType.SABOTAGE, "Communication", "통신 기기 수리", 1, (Location) null);
	public final static S_Fingerprint fingerprint	=				new S_Fingerprint(MissionType.SABOTAGE, "Fingerprint", "원자로 활성화", 1, (Location) null);
	public final static S_FixLights fixlights = 					new S_FixLights(MissionType.SABOTAGE, "FixLights", "전기 수리", 1, (Location) null);
	public final static S_Oxygen oxygen =						new S_Oxygen(MissionType.SABOTAGE, "Oxygen", "산소 활성화", 2, (Location) null);

}
