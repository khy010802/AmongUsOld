package bepo.au;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import bepo.au.Mission.MissionType;
import bepo.au.missions.*;

public class MissionList {
	
	public final static List<Mission> CARDS = new ArrayList<Mission>();
	
	// EASY
	public final static E_FixWriting writing	= new E_FixWriting(MissionType.EASY, "FixWriting", "배선 수리하기", 3, new Location(Main.w, 0, 0, 0), new Location(Main.w, 0, 0, 0), new Location(Main.w, 0, 0, 0));
	
	// HARD
	public final static H_Card card 				= new H_Card(MissionType.HARD, "Card", "관리실 : 카드 긁기", 1, new Location(Main.w, 0, 0, 0));
	

}
