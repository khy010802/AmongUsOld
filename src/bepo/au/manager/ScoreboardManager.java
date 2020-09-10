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
			line.add("��7����� �������Դϴ�.");
			line.add("��f");
			line.add("������ �������� (" + GameTimer.getRemainImposter() + "��)");
			for(String name : GameTimer.IMPOSTER) {
				if(PlayerData.getPlayerData(name) != null) {
					line.add(PlayerData.getPlayerData(name).getChatColor() + (PlayerData.getPlayerData(name).isAlive() ? "" : "��n") + name);
				}
			}
			line.add("��f ");
			line.add("�ϰ� ���൵ " + GameTimer.CLEARED_MISSION + "/" + GameTimer.REQUIRED_MISSION);
		} else {
			
			boolean alive = pd.isAlive();
			boolean imposter = GameTimer.IMPOSTER.contains(player.getName());
			
			if(imposter) {
				line.add("��7�ϴ� �̼��� ����� �̼��Դϴ�. ����");
			}
			
			if(alive) {
				if(imposter) line.add("��c����� �׾�����, ������ �纸Ÿ���� ����� �� �ֽ��ϴ�.");
				else line.add("��7����� �׾�����, ������ �ϰ��� ������ �� �ֽ��ϴ�.");
			}
			
			for(int index=0;index<pd.getMissions().size();index++) {
				line.add(pd.getMissions().get(index).getScoreboardMessage());
			}
		}
		
		return line;
	}
	

}
