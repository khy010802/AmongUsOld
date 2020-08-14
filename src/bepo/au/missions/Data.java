package bepo.au.missions;

import org.bukkit.Bukkit;


import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;







public class Data implements Listener{
	public   class Timer extends TimerBase{
		int slot;
		double radius= 0.8;
		@Override
		public void EventStartTimer() { //타이머
			Util.debugMessage(" 타이머 시작됨");
		}
		
		@Override
		public void EventRunningTimer(int count) {
			boolean changed =true;
			int Basispoint=(count*10000/maxtimer);
			final int term = (10000/5)/8;
			//
			int paperslot=(Basispoint/term)%8+1;
			Util.debugMessage(paperslot+ ", 만분율 : "+Basispoint);
			if (Basispoint==10000) {
				Util.Stack(gui, 6, Material.AIR, 1, "");
				Util.Stack(gui, 7, Material.AIR, 1, "");
			}
			else if (paperslot>0) {
				if(paperslot<8)Util.Stack(gui, paperslot, Material.PAPER, 1, "§fDATA","§4클릭불가");
				if(paperslot>1) Util.Stack(gui, paperslot-1, Material.AIR, 1, "");
				else Util.Stack(gui, 7, Material.AIR, 1, "");
			}
			
			percentage = Basispoint/100;
			Util.debugMessage(" 0.1초 경과, "+percentage+"% count:"+count);
			switch (percentage) {
			case 0:
				slot=-1;
				break;
			case 14:
				slot=0+9;
				break;
			case 38:
				slot=2+9;
				break;
			case 58:
			case 66:
				slot=4+9;
				break;
			case 74:
			case 77:
			case 81:
				slot=6+9;
				break;
			case 85:
			case 89:
			case 91:
				slot=7+9;
				break;
			case 93:
			case 95:
			case 97:
			case 98:
			case 99:
			case 100:
				slot=8+9;
				break;
			default:
				changed =false;
			}
			Util.debugMessage(slot+"슬롯 설정됨");
			if(p.getOpenInventory().getTitle().split(" ")[0].equals("Data")) { //gui 열고있는지 확인
				Util.debugMessage("gui 인식됨");
				if(changed) {
				ItemStack[] temp = 	gui.getContents();
					gui = Bukkit.createInventory(p, 18, "Data " +partname[part-1]+" " +percentage+"%");
					gui.setContents(temp);
					for (int i=slot;i>8;i--) if (9<=i&&i<18) Util.Stack(gui, i, Material.GREEN_STAINED_GLASS_PANE, 1, "§f"+(percentage)+"%","§4클릭불가");
					p.openInventory(gui);
				if (count==maxtimer) {//160틱 되면 클리어
					clear();
				}
				
				}
					
			}else { //아니면 캔슬
				Util.debugMessage("인벤토리 닫음 확인");
				stop();
				}
		}
		@Override
		public void EventEndTimer() {
				Util.debugMessage("타이머 종료됨");
			}
			
		}
		
	

	Main main;
	Player p;
	Inventory gui;
	Timer timer = new Timer();
	int percentage;
	int maxtimer=160;
	int timertick=1;
	int part;
	String[] partname = {"Download","Upload"};
	String color = "WHITE";
	///////////////////////////////////////////////////
	public void data(Player pl, Main m,int pt) {
		main = m;
		p = pl;
		part=pt;
		// Color.AQUA, Color.BLUE, Color.GRAY, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.RED, Color.WHITE, Color.YELLOW, Color.SILVER, Color.NAVY, Color.TEAL
		// LIGHT_BLUE, BLUE, GRAY, GREEN, ORANGE, PURPLE, RED, WHITE, YELLOW, LIGHT_GRAY, NAVY, TEAL
		if(false) color = PlayerData.getPlayerData(p.getName()).getColor().toString(); //<- 너가 합칠때 이런식으로 해주셈
		
		switch(color) {

		case "AQUA":
			color="LIGHT_BLUE";
			break;
		case "NAVY":
			color="BLUE";
			break;
		case "TEAL":
			color="CYAN";
			break;
		case "SILVER":
			color="LIGHT_GRAY";
			break;
		}
		Material shulker = Material.getMaterial(color+"_SHULKER_BOX");
		
		if (pt==1) {
			gui = Bukkit.createInventory(p, 18, "Data Download");
			Util.Stack(gui, 0, Material.CHEST, 1, " ");
			Util.Stack(gui, 8,shulker , 1, " ");
			start();
		} else {
			gui = Bukkit.createInventory(p, 18, "Data Upload");
			Util.Stack(gui, 0, shulker, 1, " ");
			Util.Stack(gui, 8, Material.BLAST_FURNACE, 1, " ");
			start();
		}
		
	}
	
	public void start() {
		p.openInventory(gui);
		timer.StartTimer(maxtimer,false,1);
	}
	public void stop() {
		timer.StopTimer();
	}
	public void clear() {
		if (part==1) {
			Util.debugMessage("다운로드 완료");
		}else {
			Util.debugMessage("업로드 완료");
			Util.debugMessage("클리어!");
		}
	}
	
	
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		//Inventory inv = e.getClickedInventory();
		//Player p = (Player) e.getWhoClicked();
		
		
		if(e.getView().getTitle().split(" ")[0].equals("Data")&&e.getCurrentItem()!=null) {
			e.setCancelled(true);
		}
	}
}
