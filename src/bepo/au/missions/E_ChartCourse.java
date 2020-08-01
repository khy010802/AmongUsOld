package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import bepo.au.Mission;
import bepo.au.Util;

//Cur_route가 4되면 끝
public class ChartCourse extends Mission{
	
	public static int Cur_route;
	static Player p;
	static Inventory inv;
	static int[] routeArray;
	public static void chartcourse(Player pl) { 
		Cur_route=0;
		p=pl;
		inv = Bukkit.createInventory(p, 54, "ChartCourse");
		reset();
	}
	
	public void onAssigned(Player p) {
		
	}
	
	public void a_reset() {
		 routeArray = resetRoutes();
		
		for(int Slot = 0; Slot<54;Slot ++) {
			inv.setItem(Slot, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));//우주공간
		}
		
		for(int i = 0; i<4;i++) {
			Util.fillAround(inv, routeArray[i], Material.GRAY_STAINED_GLASS_PANE);//주변 표시
			if(i!=0) inv.setItem(routeArray[i], new ItemStack(Material.RED_STAINED_GLASS_PANE));
		}
		inv.setItem(routeArray[0], new ItemStack(Material.ELYTRA));
		
		p.openInventory(inv);// gui열기
	}
	public static void updateCourse() {
			inv.setItem(routeArray[Cur_route], new ItemStack(Material.GREEN_STAINED_GLASS_PANE));//완료 표시
			if (Cur_route!=3) inv.setItem(routeArray[Cur_route+1], new ItemStack(Material.ELYTRA));
			else {
				inv.setItem(routeArray[Cur_route], new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
				missionClear();
			};
		p.updateInventory();
		Cur_route++;
	}
	private static void missionClear() {
		p.sendMessage("클리어");
		
	}
	public static int[] resetRoutes() {//좌표 랜덤설정
		int x=0 ,y;
		int maxy = 6;
		int[] routeList = new int[4];
		for(int i = 0; i<4 ; i++) {
			x=(i == 0 ? 1 :Util.random(x+2, x+3));
			y=maxy-Util.random(0, 5);
			routeList[i]=xyToSlot(x,y);
		}
		return routeList;
	}
	public static int xyToSlot(int x, int y) { //좌표를 0~53으로
		if (x>9) x=9; //x최대는 9
		int idx=(x-1)+(y-1)*9;
		return idx;
	}
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		//Inventory inv = e.getClickedInventory();
		//Player p = (Player) e.getWhoClicked();
		if(e.getCurrentItem()!=null) {
		if( e.getView().getTitle().equals("ChartCourse")&& e.getCurrentItem().getType() == Material.ELYTRA &&(
				e.getRawSlot()==routeArray[0]||
				e.getRawSlot()==routeArray[1]||
				e.getRawSlot()==routeArray[2]||
				e.getRawSlot()==routeArray[3]
				)) {
			e.setCancelled(true);
			updateCourse();
		}
		
		
		
		if(e.getView().getTitle().equals("ChartCourse") && //클릭 불가 아이템
				(e.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE ||
				e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE||
				e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE||
				e.getCurrentItem().getType() == Material.GREEN_STAINED_GLASS_PANE)) { //
			e.setCancelled(true);
		}
		}
	}

	
	
	
	
}
