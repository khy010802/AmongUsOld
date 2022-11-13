package bepo.au.sabo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import bepo.au.base.Sabotage;
import bepo.au.utils.Util;

public class S_Oxygen extends Sabotage {

	public static boolean Activated = false;
	public static int CLEARED = 0;
	
	private static int Oxygen_password[] = { 0, 0 };
	
	private int Oxygen_answer[];
	private int Oxygen_count[];

	public S_Oxygen(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc, SaboType.OXYG, 0);
	}
	
	public void onRestart() {
		
	}

	public void onAssigned(Player p) {
		assign(p);
		Oxygen_answer = new int[2];
		Oxygen_count = new int[2];
		for(int i=0;i<2;i++) {
			Oxygen_answer[i] = 0;
			Oxygen_count[i] = 5;
			uploadInventory(p, 36, "Oxygen " + i);
		}
		
		int answer = Util.random(10000, 99999);
		
		if(Activated == false) {
			Oxygen_password[0] = answer; // 패스워드 지정 10000~99999
			Oxygen_password[1] = answer;
			CLEARED = 0;
			Activated = true;
		}
	}

	public void onStart(Player p, int i) {
		resetInv(p, i);
		sabo_Oxygen(p, i);
	}

	public void onStop(Player p, int i) {

	}

	public void onClear(Player p, int i) {
		saboGeneralClear(i);
	}

	public void resetInv(Player p, int code) {
		
		gui.get(code).clear();
		
		int number = 9;
		for (int i = 3; i >= 0; i--) {
			for (int j = 8; j >= 0; j--) {
				if (j >= 1 && j <= 3 && i < 3) {
					Util.Stack(gui.get(code), j + 9 * i, Material.WHITE_WOOL, 1, "§f" + number);
					number--;
				} else if (j <= 4) {
					Util.Stack(gui.get(code), j + 9 * i, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				}
			}
		}
		Util.Stack(gui.get(code), 30, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(gui.get(code), 29, Material.WHITE_WOOL, 1, "§f" + "0");
		Util.Stack(gui.get(code), 28, Material.RED_WOOL, 1, "§c" + "CANCEL");
		Util.Stack(gui.get(code), 16, Material.PAPER, 1, "§f" + Oxygen_password[code]);
		
		Oxygen_count[code] = 5;
		Oxygen_answer[code] = 0;
	}

	public void sabo_Oxygen(Player p, int i) {

		p.openInventory(gui.get(i));

	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		if (!checkPlayer(e))
			return;

		if(!(e.getAction() == InventoryAction.PICKUP_ALL || e.getAction() == InventoryAction.PICKUP_HALF || e.getAction() == InventoryAction.PICKUP_ONE)) return;
		
		Player p = (Player) e.getWhoClicked();
		int code = getCode(e.getView().getTitle());
		
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.WHITE_WOOL) { // 흰 양털을 클릭하면 양털의 이름을 가져옴
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
			String numstr = e.getCurrentItem().getItemMeta().getDisplayName();
			int num = Integer.parseInt(numstr.substring(2));
			switch (Oxygen_count[code]) { // 양털의 이름을 num에 저장 저장할때마다 count가 1씩 줄어듬
			case 1:
				Oxygen_answer[code] += num;
				Oxygen_count[code]--;
				if (Oxygen_answer[code] == Oxygen_password[code]) {
					CLEARED++;
					onClear(null, code);
					if(CLEARED == 2) {
						Sabotage.saboClear(0);
					}
					p.closeInventory();
					return;
				} else {
					Oxygen_answer[code] = 0;
					Oxygen_count[code] = 5;
					p.sendMessage("§c비밀번호가 틀립니다.");
					p.closeInventory();
					return;
				}
			case 2:
				Oxygen_answer[code] += num * 10;
				Oxygen_count[code]--;
				break;
			case 3:
				Oxygen_answer[code] += num * 100;
				Oxygen_count[code]--;
				break;
			case 4:
				Oxygen_answer[code] += num * 1000;
				Oxygen_count[code]--;
				break;
			case 5:
				Oxygen_answer[code] = num * 10000;
				Oxygen_count[code]--;
				break;
			}
		}
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.RED_WOOL) {
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
			Oxygen_answer[code] = 0;
			Oxygen_count[code] = 5;
		}
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER) {
			e.setCancelled(true);
		}

	}

}
