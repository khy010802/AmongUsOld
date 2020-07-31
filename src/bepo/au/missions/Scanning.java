package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import amongyours.Main;
import amongyours.TimerBase;
import amongyours.Util;



public class Scanning implements Listener{
	public   class Timer extends TimerBase{
		double radius= 0.8;
		@Override
		public void EventStartTimer() { //Ÿ�̸�
			Util.debugMessage(" Ÿ�̸� ���۵�");
		}

		@Override
		public void EventRunningTimer(int count) {
			Util.debugMessage(" 0.1�� ���");
			int ccount=count/20;
			int slot = 9-ccount;
			Util.debugMessage(slot+"���� ������");
			if(p.getOpenInventory().getTitle().split(" ")[0].equals("Scanning")) { //gui �����ִ��� Ȯ��
				
				//Location location = p.getLocation().add(0, 1, 0);
				for (double x = 0-radius-0.2; x<=(radius+0.2); x+=0.05) for (double z = 0-radius-0.2; z<=(radius+0.2); z+=0.05) {
					if ((x*x+z*z)<=radius&&(x*x+z*z)>=(radius-0.1)) {
						double y = (count >=100 ? 200-count : count); //count�� 200 -> 0 ||| y�� 0->-100 �� 100->0��
						//Util.debugMessage("count : "+count);
						y = ((y-50)*0.01);
						//Util.debugMessage("y : "+y);
						Location location = p.getLocation();
						location.add(x, 1+y, z);
						p.getWorld().spawnParticle(Particle.COMPOSTER, location, 1, 0.0, 0.0, 0.0, 1.0,null,true);
					}
					
				}
				//p.getWorld().spawnParticle(Particle.COMPOSTER, location, 15, 0.7, 1.0, 0.7, 1.0,null,true);
				if(count%2==0) {
				Util.debugMessage("gui �νĵ�");
				ItemStack[] temp = 	gui.getContents();
				gui = Bukkit.createInventory(p, 9, "Scanning " + ccount);
				gui.setContents(temp);
				if (0<=slot&&slot<=8) Util.Stack(gui, slot, Material.GREEN_STAINED_GLASS_PANE, 1, "��f"+(10-ccount)+"0%","��4Ŭ���Ұ�");
				p.openInventory(gui);
				}
			}else { //�ƴϸ� ĵ��
				Util.debugMessage("�κ��丮 ���� Ȯ��");
				stopScan();
			}
			if (p.getOpenInventory().getTitle().split(" ")[0].equals("Scanning")&&count==0) { //0�� �Ǹ� Ŭ����
				ItemStack[] temp = 	gui.getContents();
				gui = Bukkit.createInventory(p, 9, "Scanning");
				p.openInventory(gui);
				gui.setContents(temp);
		
				if(p.getOpenInventory().getTitle().equals("Scanning")) {
					status=false;
					p.closeInventory();
					Util.debugMessage("Ŭ����!");
					}
			}
		}
		@Override
		public void EventEndTimer() {
				Util.debugMessage("Ÿ�̸� �����");
			}
			
		}
		
	
	Main main;
	Player p;
	Inventory gui;
	Timer timer = new Timer();
	static boolean status = false;// �� ���� �� �÷��̾ ����,
	
	
	///////////////////////////////////////////////////
	public void scanning(Player pl, Main m) {
		main = m;
		p = pl;
		gui = Bukkit.createInventory(p, 9, "Scanning");
		if (!status) {
			onScan();
		} else {
			p.sendMessage("�̹� �ٸ� �÷��̾ ������Դϴ�.");
		}
		
	}
	
	public void onScan() {
		p.openInventory(gui);
		status=true;
		timer.StartTimer(200,true,1);
	}
	public void stopScan() {
		status=false;
		timer.StopTimer();
	}
	
	
	
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		//Inventory inv = e.getClickedInventory();
		//Player p = (Player) e.getWhoClicked();
		
		
		if(e.getView().getTitle().split(" ")[0].equals("Scanning")&&e.getCurrentItem()!=null) {
			e.setCancelled(true);
		}
	


	

	}
}
