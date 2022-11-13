package bepo.au.missions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.utils.Util;

//Cur_route가 4되면 끝
public class E_ChartCourse extends Mission {
	
	public E_ChartCourse(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}

	private int Cur_route;
	private int[] routeArray;
	
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 54, "ChartCourse");
		
		Cur_route = 0;
		
	}
	
	public void onStart(Player p, int i) {
		a_reset();
		p.openInventory(gui.get(0));
	}
	
	public void onStop(Player p, int i) {
		new BukkitRunnable() {
			public void run() {
				p.getInventory().remove(Material.ELYTRA);
			}
		}.runTaskLater(Main.getInstance(), 0L);
		
	}
	
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}


	private void a_reset() {
		Cur_route = 0;
		routeArray = resetRoutes();

		for (int Slot = 0; Slot < 54; Slot++) {
			Util.Stack(gui.get(0), Slot, Material.BLACK_STAINED_GLASS_PANE, 1, " "); //우주공간
		}

		for (int i = 0; i < 4; i++) {
			Util.fillAround(gui.get(0), routeArray[i], Material.GRAY_STAINED_GLASS_PANE);// 주변 표시
			if (i != 0)
				Util.Stack(gui.get(0), routeArray[i], Material.RED_STAINED_GLASS_PANE, 1, " ");
		}
		Util.Stack(gui.get(0), routeArray[0], Material.ELYTRA, 1, "§f항로");
	}

	private void updateCourse(Player p) {
		Util.Stack(gui.get(0), routeArray[Cur_route], Material.GREEN_STAINED_GLASS_PANE, 1, " ");// 완료 표시
		if (Cur_route != 3){
			Util.Stack(gui.get(0), routeArray[Cur_route+1], Material.ELYTRA, 1, "§f항로");
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
		}
		else {
			Util.Stack(gui.get(0), routeArray[Cur_route], Material.GREEN_STAINED_GLASS_PANE, 1, " ");
			onClear(p, 0);
		}

		p.updateInventory();
		Cur_route++;
	}
	
	public static int[] resetRoutes() {// 좌표 랜덤설정
		int x = 0, y;
		int maxy = 6;
		int[] routeList = new int[4];
		for (int i = 0; i < 4; i++) {
			x = (i == 0 ? 1 : Util.random(x + 2, x + 3));
			y = maxy - Util.random(0, 5);
			routeList[i] = xyToSlot(x, y);
		}
		return routeList;
	}

	public static int xyToSlot(int x, int y) { // 좌표를 0~53으로
		if (x > 9)
			x = 9; // x최대는 9
		int idx = (x - 1) + (y - 1) * 9;
		return idx;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(!checkPlayer(e)) return;

		if (e.getCurrentItem() != null) {
			if (e.getCurrentItem().getType() == Material.ELYTRA
					&& (e.getRawSlot() == routeArray[0] || e.getRawSlot() == routeArray[1]
							|| e.getRawSlot() == routeArray[2] || e.getRawSlot() == routeArray[3])) {
				e.setCancelled(true);
				updateCourse((Player) e.getWhoClicked());
			}

			if (
					(e.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE
							|| e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE
							|| e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE
							|| e.getCurrentItem().getType() == Material.GREEN_STAINED_GLASS_PANE)) { //
				e.setCancelled(true);
			}
		}
	}

}