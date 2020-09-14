package bepo.au.missions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.utils.Util;

//8ƽ -> 2ƽ
public class H_InspectSample extends Mission{
	
	public H_InspectSample(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}
	
	public void onAssigned(Player p) {
		assign(p);
	}
	
	public void onStart(Player p, int code) {
		uploadInventory(p, 54, "InspectSample");
		inspectsample(p);
	}
	
	public void onClear(Player p, int code) {
		generalClear(p, code);
	}
	
	public void onStop(Player p, int code) {
		
	}
	
	public class Timer extends TimerBase {

		@Override
		public void EventStartTimer() { // Ÿ�̸� ����
			Util.debugMessage(" Ÿ�̸� ���۵�");
			P_timer = new PreparingTimer();
			P_timer.StartTimer(45, false, 1);
		}

		@Override
		public void EventRunningTimer(int count) {
			Util.debugMessage(" 1�� ���");
			Player p = getPlayer();
			if (p != null && p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
				ItemStack[] temp = gui.get(0).getContents();
				gui.set(0, Bukkit.createInventory(p, 54, "InspectSample " + count));
				gui.get(0).setContents(temp);
				p.openInventory(gui.get(0));
			}
		}

		@Override
		public void EventEndTimer() {
			ItemStack[] temp = gui.get(0).getContents();
			Player p = getPlayer();
			if(p != null) {
				gui.set(0, Bukkit.createInventory(p, 54, "InspectSample"));
				gui.get(0).setContents(temp);
				status = 4;
				
				if (p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
					inspectsampleagain(p);
				}
			}
			

		}

	}

	public class PreparingTimer extends TimerBase {// ȣ�� �ű�� Ÿ�̸�
		int hopperidx = 0;

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void EventRunningTimer(int count) {
			Player p = getPlayer();
			if(p == null) return;
			
			
			
			if (count == 45) {
				Util.debugMessage("ȣ�� �̵� Ÿ�� 0");
				Util.Stack(gui.get(0), hopperidx, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				hopperidx = 0;
				Util.Stack(gui.get(0), hopperidx, Material.HOPPER, 1, " ");

			} else if (count % 10 == 8 && count < 43) {
				Util.debugMessage("ȣ�� �̵� Ÿ�� 1");
				hopperidx++;
				Util.Stack(gui.get(0), hopperidx - 1, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.Stack(gui.get(0), hopperidx, Material.HOPPER, 1, " ");

			} else if (count % 10 == 2 && count < 43 && count > 8) {
				Util.debugMessage("ȣ�� �̵� Ÿ�� 2");
				hopperidx++;
				Util.Stack(gui.get(0), hopperidx - 1, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.StackPotion(gui.get(0), 18 + hopperidx, Color.BLUE, 1, "��fȮ�ε��� ���� �þ�");
				if (getPlayer().getOpenInventory().getTitle().split(" ")[0].equals("InspectSample"))
					p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.2f);
				Util.Stack(gui.get(0), hopperidx, Material.HOPPER, 1, " ");

			} else if (count == 0) {
				Util.StackPotion(gui.get(0), 18 + hopperidx, Color.BLUE, 1, "��fȮ�ε��� ���� �þ�");
				if (p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample"))
					p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.2f);
			}
			if (p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
				p.openInventory(gui.get(0));
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub

		}

	}

	Timer timer;
	PreparingTimer P_timer;
	int status = 0; // 0 ����ȵ� | 1,2 ����� | 3 �þ�м��� | 4 �м����� | 100 Ŭ���� ����
	int bad;
	final int time = 5; // ��ٸ� �ð�

	private List<String> lore = Arrays.asList("��7", "��71. ������ �Ʒ� �Ķ��� ��ư�� ������.", "��72. " + time + "�� ���� ��ٸ���.",
			"��73. �̻� ǥ���� �����Ѵ�.", "��7�߸��� ǥ���� �����ϸ� �ٽ� �����մϴ�.", "��7��ٸ��� ���� �ٸ� ���� ���� �˴ϴ�.");

	
	public void inspectsample(Player p) {
		Util.debugMessage("inspctsample ����");
		switch (status) {
		case 0: // ���� ��

			Util.debugMessage("status 0 ����");
			bad = Util.random(0, 4); // �̻� ǥ�� �����
			gui.set(0, Bukkit.createInventory(p, 54, "InspectSample"));
			for (int slot = 0; slot < 54; slot++) {
				switch (slot) {
				case 0:
					Util.Stack(gui.get(0), slot, Material.HOPPER, 1, " ");
					break;
				case 18:
				case 20:
				case 22:
				case 24:
				case 26:
					Util.Stack(gui.get(0), slot, Material.GLASS_BOTTLE, 1, "��f�� �þ� ��", "��4Ŭ���Ұ�");
					break;
				case 36:
				case 38:
				case 40:
				case 42:
				case 44:
					Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ", "��4Ŭ���Ұ�");
					break;
				case 49:
					Util.Stack(gui.get(0), slot, Material.BOOK, 1, "��f��lǥ�� �м�", lore);
					break;
				case 53:
					Util.Stack(gui.get(0), slot, Material.BLUE_STAINED_GLASS_PANE, 1, "��a��l�þ� �߰��ϱ�");
					break;
				default:
					Util.Stack(gui.get(0), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " "); // ���
				}
			}

			status = 1;
		case 1: // �Ʒ� ���� �ݺ� ����
			p.openInventory(gui.get(0)); // gui.get(0) ����
			break;
		case 2: // �þ� �غ���
			Util.debugMessage("status 2 ����");
			Util.Stack(gui.get(0), 53, Material.ORANGE_STAINED_GLASS_PANE, 1, " ", "��4Ŭ���Ұ�");
			timer = new Timer();
			timer.StartTimer(time, true, 20);
			status = 3;
			p.openInventory(gui.get(0));
			break;
		case 4:
			Util.debugMessage("status 4 ����");
			prepareSample();// �þ� �غ� �Ϸ�
			p.openInventory(gui.get(0));
			break;
		}
		Util.debugMessage("switch�� ��������");
		p.openInventory(gui.get(0)); // gui.get(0) ����
	}

	public void inspectsampleagain(Player p) {
		inspectsample(p);
	}

	public void prepareSample() {
		Util.debugMessage("�þ� �غ�Ϸ� �ܰ�");

		for (int slot = 36; slot <= 44; slot += 2)
			Util.Stack(gui.get(0), slot, Material.GREEN_STAINED_GLASS_PANE, 1, "��fǥ���� �����ϼ���");
		for (int slot = 18; slot < 27; slot += 2) {
			if ((slot - 18) / 2 != bad)
				Util.StackPotion(gui.get(0), slot, Color.BLUE, 1, "��f���� �þ�");
			else
				Util.StackPotion(gui.get(0), slot, Color.RED, 1, "��c�̻� �þ�");
		}

	}

	public void setStatus(int num) {
		status = num;
	}

	public void checkSample(Player p, int num) {
		Util.debugMessage(num + "��" + bad + "��");
		if (num == bad) {
			for (int slot = 36; slot <= 44; slot += 2)
				Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ", "��4Ŭ���Ұ�");
			status = 100;
			onClear(p, 0);
		} else {
			p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.1f);
			Util.debugMessage(" Ʋ��, �����");
			status = 0;
			inspectsampleagain(p);
		}

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		if(!checkPlayer(e)) return;
		
		Player p = (Player) e.getWhoClicked();
		
		// Inventory inv = e.getClickedInventory();
		// Player p = (Player) e.getWhoClicked();

		if (e.getView().getTitle().split(" ")[0].equals("InspectSample") && e.getCurrentItem() != null) {
			Material item = e.getCurrentItem().getType();
			if (item != Material.GREEN_STAINED_GLASS_PANE && item != Material.BLUE_STAINED_GLASS_PANE) {
				e.setCancelled(true);
			}
			if (item == Material.BLUE_STAINED_GLASS_PANE) {
				status = 2;
				inspectsampleagain(p);
			}
			if (item == Material.GREEN_STAINED_GLASS_PANE) {
				checkSample(p, (e.getRawSlot() % 9) / 2);
				e.setCancelled(true);
			}
		}
	}
}
