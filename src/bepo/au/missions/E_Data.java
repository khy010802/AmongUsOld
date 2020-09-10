package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.base.Mission.MissionType;
import bepo.au.base.PlayerData;
import bepo.au.utils.Util;

public class E_Data extends Mission {
	
	public E_Data(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}
	
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 18, "Data Download");
		uploadInventory(p, 18, "Data Upload");
	}
	
	public void onStart(Player p, int i) {
		data(p, i);
		uploadInventory(p, 36, "ActivatingShield");
	}
	
	public void onStop(Player p, int i) {
		p.getInventory().remove(Material.ELYTRA);
	}
	
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	
	Timer timer;
	int percentage;
	int maxtimer = 160;
	int timertick = 1;
	String[] partname = { "Download", "Upload" };

	///////////////////////////////////////////////////
	public void data(Player p, int pt) {
		// Color.AQUA, Color.BLUE, Color.GRAY, Color.GREEN, Color.ORANGE, Color.PURPLE,
		// Color.RED, Color.WHITE, Color.YELLOW, Color.SILVER, Color.NAVY, Color.TEAL
		// LIGHT_BLUE, BLUE, GRAY, GREEN, ORANGE, PURPLE, RED, WHITE, YELLOW,
		// LIGHT_GRAY, NAVY, TEAL
		
		String color;
		color = PlayerData.getPlayerData(p.getName()).getColor().toString(); // <- �ʰ� ��ĥ�� �̷������� ���ּ�

		switch (color) {

		case "AQUA":
			color = "LIGHT_BLUE";
			break;
		case "NAVY":
			color = "BLUE";
			break;
		case "TEAL":
			color = "CYAN";
			break;
		case "SILVER":
			color = "LIGHT_GRAY";
			break;
		}
		Material shulker = Material.getMaterial(color + "_SHULKER_BOX");

		if (pt == 0) {
			Util.Stack(gui.get(0), 0, Material.CHEST, 1, " ");
			Util.Stack(gui.get(0), 8, shulker, 1, " ");
		} else {
			Util.Stack(gui.get(1), 0, shulker, 1, " ");
			Util.Stack(gui.get(1), 8, Material.BLAST_FURNACE, 1, " ");
		}
		start(p, pt);
	}

	public void start(Player p, int code) {
		p.openInventory(gui.get(code));
		timer = new Timer(p, code);
		timer.StartTimer(maxtimer, false, 1);
	}

	public void stop() {
		if(timer != null && timer.GetTimerRunning()) timer.StopTimer();
		timer = null;
	}

	public void clear(Player p, int code) {
		if (code == 0) {
			Util.debugMessage("�ٿ�ε� �Ϸ�");
		} else {
			Util.debugMessage("���ε� �Ϸ�");
			Util.debugMessage("Ŭ����!");
			onClear(p, code);
		}
	}

	
	
	public class Timer extends TimerBase {
		private int code;
		private Player p;
		int slot;
		double radius = 0.8;

		public Timer(Player p, int code) {
			this.p = p;
			this.code = code;
		}
		
		@Override
		public void EventStartTimer() { // Ÿ�̸�
			Util.debugMessage(" Ÿ�̸� ���۵�");
		}

		@Override
		public void EventRunningTimer(int count) {
			
			if(p == null || !p.isOnline()) {
				stop();
				return;
			}
			
			boolean changed = true;
			int Basispoint = (count * 10000 / maxtimer);
			final int term = (10000 / 5) / 8;
			//
			int paperslot = (Basispoint / term) % 8 + 1;
			Util.debugMessage(paperslot + ", ������ : " + Basispoint);
			if (Basispoint == 10000) {
				Util.Stack(gui.get(code), 6, Material.AIR, 1, "");
				Util.Stack(gui.get(code), 7, Material.AIR, 1, "");
			} else if (paperslot > 0) {
				if (paperslot < 8)
					Util.Stack(gui.get(code), paperslot, Material.PAPER, 1, "��fDATA", "��4Ŭ���Ұ�");
				if (paperslot > 1)
					Util.Stack(gui.get(code), paperslot - 1, Material.AIR, 1, "");
				else
					Util.Stack(gui.get(code), 7, Material.AIR, 1, "");
			}

			percentage = Basispoint / 100;
			Util.debugMessage(" 0.1�� ���, " + percentage + "% count:" + count);
			switch (percentage) {
			case 0:
				slot = -1;
				break;
			case 14:
				slot = 0 + 9;
				break;
			case 38:
				slot = 2 + 9;
				break;
			case 58:
			case 66:
				slot = 4 + 9;
				break;
			case 74:
			case 77:
			case 81:
				slot = 6 + 9;
				break;
			case 85:
			case 89:
			case 91:
				slot = 7 + 9;
				break;
			case 93:
			case 95:
			case 97:
			case 98:
			case 99:
			case 100:
				slot = 8 + 9;
				break;
			default:
				changed = false;
			}
			Util.debugMessage(slot + "���� ������");
			if (p.getOpenInventory().getTitle().split(" ")[0].equals("Data")) { // gui �����ִ��� Ȯ��
				Util.debugMessage("gui �νĵ�");
				if (changed) {
					InventoryView iv = p.getOpenInventory();
					ItemStack[] temp = iv.getTopInventory().getContents();
					gui.set(code, Bukkit.createInventory(p, 18, "Data " + partname[code] + " " + percentage + "%"));
					gui.get(code).setContents(temp);
					for (int i = slot; i > 8; i--)
						if (9 <= i && i < 18)
							Util.Stack(gui.get(code), i, Material.GREEN_STAINED_GLASS_PANE, 1, "��f" + (percentage) + "%",
									"��4Ŭ���Ұ�");
					p.openInventory(gui.get(code));
					if (count == maxtimer) { clear(p, code);
					}

				}

			} else { // �ƴϸ� ĵ��
				Util.debugMessage("�κ��丮 ���� Ȯ��");
				stop();
			}
		}

		@Override
		public void EventEndTimer() {
			Util.debugMessage("Ÿ�̸� �����");
		}

	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {

		if(e.getView().getTitle().contains("Data") && e.getCurrentItem() != null) {
			e.setCancelled(true);
		}
	}
}