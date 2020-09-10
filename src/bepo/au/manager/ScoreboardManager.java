package bepo.au.manager;

import java.util.List;

import org.bukkit.entity.Player;

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
		
		if(pd == null) {
			// °üÀüÀÚ
		} else {
			
			
			
		}
		
		return null;
	}
	

}
