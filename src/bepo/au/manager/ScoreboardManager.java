package bepo.au.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import bepo.au.GameTimer;
import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import io.github.thatkawaiisam.assemble.AssembleAdapter;

public class ScoreboardManager implements AssembleAdapter{

	private int count = 0;
	
	@Override
	public void tick() {
		count++;
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
			for(String name : GameTimer.IMPOSTER) {
				if(PlayerData.getPlayerData(name) != null) {
					line.add(PlayerData.getPlayerData(name).getChatColor() + (PlayerData.getPlayerData(name).isAlive() ? "" : "§n") + name);
				}
			}
			line.add("§f ");
			line.add("일과 진행도 " + GameTimer.CLEARED_MISSION + "/" + GameTimer.REQUIRED_MISSION);
		} else {
			
			boolean alive = pd.isAlive();
			boolean imposter = GameTimer.IMPOSTER.contains(player.getName());
			
			if(imposter) {
				line.add("§7하단 미션은 위장용 미션입니다. ㅇㅇ");
			}
			
			if(alive) {
				if(imposter) line.add("§c당신은 죽었지만, 여전히 사보타지를 사용할 수 있습니다.");
				else line.add("§7당신은 죽었지만, 여전히 일과를 수행할 수 있습니다.");
			}
			
			for(int index=0;index<pd.getMissions().size();index++) {
				line.add(pd.getMissions().get(index).getScoreboardMessage());
			}
		}
		
		return line;
	}
	

}
