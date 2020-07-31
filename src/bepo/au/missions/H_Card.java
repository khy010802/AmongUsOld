package bepo.au.missions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Main;
import bepo.au.Mission;

public class H_Card extends Mission{

	private List<String> lore = Arrays.asList("��7", "��71. ī�带 ���.", "��72. ���� �� ������ ���콺�� �����ٴ��.", "��73. ���� �� ���� ������ ��Ŭ���� ����ä�� �巡���Ѵ�.", "��71~3�� ������ ������ �ӵ��� �������ּ���.");
	
	public H_Card(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}

	@Override
	public void onAssigned(Player p) {
		
		uploadInventory(p, 27, "ī��Ű �̼�");
		
		for(int slot=0;slot<27;slot++) {
			int temp = slot/9;
			if(temp != 1) Stack(gui, slot, Material.GRAY_STAINED_GLASS_PANE, 1, "��f" + (temp < 1 ? "��" : "��") + "�� �� ���� ������ ī�带 �巡�����ּ���.", null);
		}
	}

	@Override
	public void onStart(Player p, int i) {
		Stack(gui, 17, Material.BOOK, 1, "��e�����Ͻ� ī�带 �ܾ��ּ���.", lore);
		p.openInventory(gui);
		p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0F, 1.0F);

		resetInv(p, gui);
	}

	@Override
	public void onClear(Player p, int i) {
		generalClear(p);
	}
	
	@Override
	public void onStop(Player p, int i) {
		p.getInventory().remove(Material.PAPER);
		if(ct != null) {
			ct.cancel();
			ct = null;
		}
	}
	
	private CardTimer ct = null;

	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if(!checkPlayer(event)) return;
		Player p = (Player) event.getWhoClicked();
		
		Set<Integer> set = event.getInventorySlots();
		if(set != null && set.size() == 8 && set.contains(16)) {
			int timer = ct.getTimer();
			int diff = Main.MISSION_DIFFICULTY;
			int period = 11 - diff;
			
			if(timer > 20 + period) {
				resetInv(p, event.getView().getTopInventory());
				Stack(gui, 17, Material.BARRIER, 1, "��c��l�ʹ� ������ �ܾ����ϴ�!", lore);
			} else if(timer < 20 - period) {
				resetInv(p, event.getView().getTopInventory());
				Stack(gui, 17, Material.BARRIER, 1, "��c��l�ʹ� ������ �ܾ����ϴ�!", lore);
			} else {
				onClear(p, 1);
			}
			
		} else {
			
			resetInv(p, event.getView().getTopInventory());
			Stack(gui, 17, Material.BARRIER, 1, "��c��lī�带 ����� �ܾ��ּ���!", lore);
		}
		
		if(ct != null) {
			ct.cancel();
			ct = null;
		}
		
	}
	
	private void resetInv(Player p, Inventory inv) {
		new BukkitRunnable() {
			public void run() {
				inv.remove(Material.PAPER);
				p.setItemOnCursor(new ItemStack(Material.AIR, 1));
				p.getInventory().remove(Material.PAPER);
				Stack(inv, 9, Material.PAPER, 8, "��f[�̼�] ī��Ű", Arrays.asList("��7ī��Ű �̼��� ����ϴ� ���� ���˴ϴ�.", "��7���콺�� ��� ī�� �νı��� �� ������ ������ �巡�����ּ���."));
				p.updateInventory();
			}
		}.runTaskLater(main, 1L);
		
		
	
		
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if(!checkPlayer(event)) return;
		
		Player p = (Player) event.getWhoClicked();

		
		
		if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER) {
			if(ct != null) {
				ct.cancel();
				ct = null;
				resetInv(p, event.getView().getTopInventory());
				Stack(gui, 17, Material.BARRIER, 1, "��c��lī�带 ����� �ܾ��ּ���!", lore);
				return;
			}
			CardTimer ct = new CardTimer(p);
			ct.runTaskTimer(main, 1L, 1L);
			this.ct = ct;
		} else {
			event.setCancelled(true);
		}
		
	}
	
	public class CardTimer extends BukkitRunnable {
		
		private int timer = 0;
		private Player p;
		public CardTimer(Player p) {
			this.p = p;
		}
		
		public void run() {
			p.sendMessage("" + timer);
			timer++;
			if(p == null || !p.isOnline()) cancel();
		}
		
		public int getTimer() {
			return this.timer;
		}
		
	}

}
