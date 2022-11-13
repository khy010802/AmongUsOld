package bepo.au.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import bepo.au.GameTimer;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.sabo.S_Fingerprint;
import bepo.au.sabo.S_Oxygen;
import io.github.thatkawaiisam.assemble.AssembleAdapter;

public class ScoreboardManager implements AssembleAdapter{
	
	@Override
	public void tick() {
	}
	
	@Override
	public String getTitle(Player player) {
		return "AMONG US";
	}

	@Override
	public List<String> getLines(Player player) {
		
		PlayerData pd = PlayerData.getPlayerData(player.getName());
		List<String> line = new ArrayList<String>();
		
		if(pd == null) {
			line.add("§7당신은 관전자입니다.");
			line.add("§f");
			line.add("생존한 임포스터 (" + GameTimer.getRemainImposter() + "명)");
			for(String name : GameTimer.ALIVE_IMPOSTERS) {
				line.add(PlayerData.getPlayerData(name).getColor().getChatColor() + name);
			}
			line.add("§f ");
			line.add("일과 진행도 " + GameTimer.CLEARED_MISSION + "/" + GameTimer.REQUIRED_MISSION);
		} else {
			
			boolean alive = pd.isAlive();
			boolean imposter = GameTimer.IMPOSTER.contains(player.getName());
			
			if(imposter) {
				line.add("§7하단 미션은 위장용 미션입니다.");
			}
			
			if(!alive) {
				if(imposter) {
					line.add("§c당신은 죽었지만, ");
					line.add("§c여전히 사보타지를 사용할 수 있습니다.");
				}
				else {
					line.add("§7당신은 죽었지만,");
					line.add("§7여전히 일과를 수행할 수 있습니다.");
				}
			}
			
			if(Sabotage.isActivating(0)) {
				switch(Sabotage.Sabos.getType()) {
				case COMM: line.add("§c통신 기기 파손"); return line;
				case NUCL:
					int i = (S_Fingerprint.lowerPlayerList.size() > 0 ? 1 : 0) + (S_Fingerprint.upperPlayerList.size() > 0 ? 1 : 0);
					line.add("§c원자로 용해까지 (" + i + "/2) (" + (Sabotage.Remain_Tick[0]/20+1) + "s)");
					break;
				case OXYG:
					int i2 = (S_Oxygen.CLEARED);
					line.add("§c산소 고갈까지 (" + i2 + "/2) (" + (Sabotage.Remain_Tick[0]/20+1) + "s)");
					break;
				case DOOR:
					break;
				case ELEC:
					line.add("§c전등 고치기");
					break;
				}
			}
			for(int index=0;index<pd.getMissions().size();index++) {
				if(!(pd.getMissions().get(index) instanceof Sabotage))
				line.add(pd.getMissions().get(index).getScoreboardMessage());
			}
		}
		
		return line;
	}
	

}
