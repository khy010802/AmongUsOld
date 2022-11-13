package bepo.au.missions;

import bepo.au.Main;
import bepo.au.base.Mission;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class C_Card extends Mission{

	private List<String> lore = Arrays.asList("§7", "§71. 카드를 든다.", "§72. 좌측 빈 공간에 마우스를 가져다댄다.", "§73. 우측 빈 공간 끝까지 우클릭을 누른채로 드래그한다.", "§71~3의 동작을 적절한 속도로 진행해주세요.");

	public C_Card(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}

	@Override
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 27, "카드키 미션");


	}

	@Override
	public void onStart(Player p, int i) {
		Stack(gui.get(0), 17, Material.BOOK, 1, "§e소유하신 카드를 긁어주세요.", lore);
		for(int slot=0;slot<27;slot++) {
			int temp = slot/9;
			if(temp != 1) Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, "§f" + (temp < 1 ? "하" : "상") + "단 빈 공간 끝까지 카드를 드래그해주세요.", null);
		}
		p.openInventory(gui.get(0));
		p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0F, 1.0F);

		resetInv(p, gui.get(0));
	}

	@Override
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	@Override
	public void onStop(Player p, int i) {
		new BukkitRunnable() {
			public void run() {
				p.getInventory().remove(Material.PAPER);
			}
		}.runTaskLater(Main.getInstance(), 0L);

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
			int diff = 5;
			int period = 11 - diff;

			if(timer > 20 + period) {
				resetInv(p, event.getView().getTopInventory());
				Stack(gui.get(0), 17, Material.BARRIER, 1, "§c§l너무 느리게 긁었습니다!", lore);
			} else if(timer < 20 - period) {
				resetInv(p, event.getView().getTopInventory());
				Stack(gui.get(0), 17, Material.BARRIER, 1, "§c§l너무 빠르게 긁었습니다!", lore);
			} else {
				onClear(p, 0);
			}

		} else {

			resetInv(p, event.getView().getTopInventory());
			Stack(gui.get(0), 17, Material.BARRIER, 1, "§c§l카드를 제대로 긁어주세요!", lore);
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
				Stack(inv, 9, Material.PAPER, 8, "§f[미션] 카드키", Arrays.asList("§7카드키 미션을 사용하는 데에 사용됩니다.", "§7마우스로 잡고 카드 인식기의 빈 공간에 끝까지 드래그해주세요."));
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
				Stack(gui.get(0), 17, Material.BARRIER, 1, "§c§l카드를 제대로 긁어주세요!", lore);
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
			timer++;
			if(p == null || !p.isOnline()) cancel();
		}

		public int getTimer() {
			return this.timer;
		}

	}

}
