package bepo.au.missions;

import java.util.Arrays;												

import java.util.List;												
												
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;												
import org.bukkit.entity.Player;												
												
import org.bukkit.event.EventHandler;												
												
import org.bukkit.event.Listener;												
import org.bukkit.event.inventory.InventoryClickEvent;												
import org.bukkit.event.inventory.InventoryDragEvent;												
import org.bukkit.inventory.Inventory;												
import org.bukkit.inventory.ItemStack;												
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Main;
import bepo.au.Mission;
import bepo.au.Util;
import bepo.au.Mission.MissionType;

public class E_FixWriting extends Mission implements Listener {
	
	public E_FixWriting(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}
									
		static int[] wirecolorArray;											
		static boolean[] connected = {false,false,false,false};											
		static Material[] WIRECOLORARRAY = {Material.RED_STAINED_GLASS_PANE,									
											Material.BLUE_STAINED_GLASS_PANE,
											Material.GREEN_STAINED_GLASS_PANE,
											Material.PURPLE_STAINED_GLASS_PANE};
		
		public void onAssigned(Player p) {
			uploadInventory(p, 54, "FixWriting");	
			wirecolorArray = Util.difrandom(0, 3, 4);
			for (int slot = 0; slot<54; slot++) {//gui인벤토리										
				int y = slot/9, x=slot%9;									
				if (y==0||y==2||y==3||y==5) {									
					Util.debugMessage(" wirecolorArray 확인 "+wirecolorArray[yToidx(y)]);								
					if(x == 8) Util.Stack(gui, slot, Material.BLACK_STAINED_GLASS_PANE, 1, " ");//검정색표시								
					else if(x == 0) Util.Stack(gui, slot, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");//노랑 표시								
					else if(x == 1 || x == 2) fillWire(slot,wirecolorArray[yToidx(y)],x);//왼쪽 와이어 채우기(랜덤)								
					else if(x == 7) fillWire(slot,yToidx(y)); //오른쪽 와이어 채우기(고정)								
					else if(x == 6) ; //오른쪽 와이어 빈공간								
					else Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " "); //배경								
				}									
				else Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " "); //배경									
			}
		}
		
		public void onStaer() {
			
		}
		
		public void a_reset(boolean open) {											
								
			
			p.openInventory(gui);										
		}											
		public static int yToidx(int y) {											
			int idx = -1;										
			switch (y){										
			case 0:										
				idx=0;									
				break;									
			case 2:										
				idx=1;									
				break;									
			case 3:										
				idx=2;									
				break;									
			case 5:										
				idx=3;									
				break;									
				}									
			return idx;										
		}											
													
		public static void fillWire(int slot, int color, int num) {											
			List<String> lore = (num == 1 ? Arrays.asList("§4클릭불가") : Arrays.asList("§7우클릭만 가능합니다."));										
			switch (color){										
			case 0:										
				Util.Stack(gui, slot, WIRECOLORARRAY[0], num, "§cRed §fWire", lore);									
				break;									
			case 1:										
				Util.Stack(gui, slot, WIRECOLORARRAY[1], num, "§9Blue §fWire", lore);									
				break;									
			case 2:										
				Util.Stack(gui, slot, WIRECOLORARRAY[2], num, "§aGreen §fWire", lore);;									
				break;									
			case 3:										
				Util.Stack(gui, slot, WIRECOLORARRAY[3], num, "§dPurple §fWire", lore);									
				break;									
			case -1:										
				gui.setItem(slot,  new ItemStack(Material.BARRIER,num));									
				break;									
		}}											
		public static void fillWire(int slot, int color) {											
			fillWire(slot,color,1);										
		}											
													
		public static void checkConnection(int slot) {											
			new BukkitRunnable(){										
			public void run(){										
				if(slot%9==6&&slot<54&&slot/9!=1&&slot/9!=4) {									
					int idx  = yToidx(slot/9);								
					Util.debugMessage(slot+"슬롯 연결 확인");								
					if(!(gui.getItem(slot)==null) &&gui.getItem(slot).getType() == WIRECOLORARRAY[idx] )	{						
						Util.debugMessage("연결됨"+(slot+2)+"에 노란 유리");							
						Util.Stack(gui, slot+2, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");//전기 들어옴 표시							
						connected[idx]=true;							
						for (int i=0; i<4 ; i++)if (connected[i]==false) {							
							Util.debugMessage(i+"가 연결안됨");						
							return;						
						}							
						Util.debugMessage("클리어!"); //클리어!							
					}else {								
						Util.debugMessage("연결안됨"+(slot+2)+"에 검은 유리");							
					Util.Stack(gui, slot+2, Material.BLACK_STAINED_GLASS_PANE, 1, " ");//전기 끊김 표시								
					connected[idx]=false;								
					}								
				}									
			}										
			}.runTaskLater(main, 0L);										
													
	}												
													
													
													
													
		@EventHandler											
	public void onClick(InventoryClickEvent e) {												
		if(e.getView().getTitle().equals("FixWiring")) {											
		Util.debugMessage("클릭 인식됨");											
		int slot = e.getRawSlot();											
		ItemStack itemstack = e.getCurrentItem();											
													
		//Inventory gui = e.getClickedInventory();											
		//Player p = (Player) e.getWhoClicked();											
													
													
													
			if(e.isRightClick()) Util.debugMessage("우클릭 인식됨");										
			if(e.getCurrentItem()!=null) {										
				if    ( !e.isRightClick() ||//우클릭만 허용									
					(slot%9 !=2 && slot%9!=6 )||//클릭 가능한 x좌표								
					slot/9==1||slot/9==4 //클릭 불가인 y좌표								
						) { //							
				Util.debugMessage("클릭 불가");									
				e.setCancelled(true);									
				}									
				if((e.getCursor().getType()!=Material.AIR||itemstack.getAmount()==1 )									
						&&(slot%9 ==1||slot%9 ==2|| slot%9 ==7)) {//아이템 하나일시 클릭 불가 &							
					Util.debugMessage("클릭 불가");								
					e.setCancelled(true);								
					}								
				}									
			checkConnection(slot);										
				}									
			}										
													
													
		@EventHandler											
	public void onDrag(InventoryDragEvent e) {												
		if (!e.getRawSlots().isEmpty()){											
		for (int slot:e.getRawSlots()) {											
			checkConnection(slot);										
		}											
		}											
													
	}
}