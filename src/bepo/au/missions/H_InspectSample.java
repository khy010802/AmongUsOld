package bepo.au.missions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import org.bukkit.inventory.ItemStack;


//8ƽ -> 2ƽ
public class H_InspectSample implements Listener {
	public   class Timer extends TimerBase{
		
		@Override
		public void EventStartTimer() { // Ÿ�̸� ����
			Util.debugMessage(" Ÿ�̸� ���۵�");
			P_timer.StartTimer(45,false,1);
		}

		@Override
		public void EventRunningTimer(int count) {
			Util.debugMessage(" 1�� ���");
			if(p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
				ItemStack[] temp = 	gui.getContents();
				gui = Bukkit.createInventory(p, 54, "InspectSample " + count);
				gui.setContents(temp);
				p.openInventory(gui);
			}
		}

		@Override
		public void EventEndTimer() {
			ItemStack[] temp = 	gui.getContents();
			gui = Bukkit.createInventory(p, 54, "InspectSample");
			gui.setContents(temp);
			status = 4;
			if(p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
				inspectsampleagain();
			}
			
		}
		
	}
	public   class PreparingTimer extends TimerBase{//ȣ�� �ű�� Ÿ�̸�
		int hopperidx = 0;
		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void EventRunningTimer(int count) {
			
			

			
			if (count==45) {
				Util.debugMessage("ȣ�� �̵� Ÿ�� 0");
				Util.Stack(gui, hopperidx, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				hopperidx=0;
				Util.Stack(gui, hopperidx, Material.HOPPER, 1, " ");
				
			}
			else if (count%10==8&&count<43) {
				Util.debugMessage("ȣ�� �̵� Ÿ�� 1");
				hopperidx++;
				Util.Stack(gui, hopperidx-1, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.Stack(gui, hopperidx, Material.HOPPER, 1, " ");
				
			}
			else if(count%10==2&&count<43&&count>8) {
				Util.debugMessage("ȣ�� �̵� Ÿ�� 2");
				hopperidx++;
				Util.Stack(gui, hopperidx-1, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.StackPotion(gui, 18+hopperidx, Color.BLUE, 1, "��fȮ�ε��� ���� �þ�");
				if(p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.2f);
				Util.Stack(gui, hopperidx, Material.HOPPER, 1, " ");
				
			}else if(count==0) {
				Util.StackPotion(gui, 18+hopperidx, Color.BLUE, 1, "��fȮ�ε��� ���� �þ�");
				if(p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.2f);
			}
			if(p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
				p.openInventory(gui);
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			
		}
		
	}
	  
	  Timer timer = new Timer();
	  PreparingTimer P_timer = new PreparingTimer();
	  Main main;
	  Player p;
	  Inventory gui;
	  int status = 0 ; //0 ����ȵ� | 1,2 ����� | 3 �þ�м��� | 4 �м����� | 100 Ŭ���� ����
	  int bad;
	  final int time = 5; //��ٸ� �ð�

	 
	  private List<String> lore = Arrays.asList("��7", 
			"��71. ������ �Ʒ� �Ķ��� ��ư�� ������.", 
			"��72. "+time+"�� ���� ��ٸ���.", 
			"��73. �̻� ǥ���� �����Ѵ�", 
			"��7�߸��� ǥ���� �����ϸ� �ٽ� �����մϴ�.",
			"��7��ٸ��� ���� �ٸ� ���� ���� �˴ϴ�.");
	
	public void inspectsample(Player pl, Main m) {
		Util.debugMessage("inspctsample ����");
		switch (status) {
		case 0: //���� ��
			
			
			
			Util.debugMessage("status 0 ����");
			bad=Util.random(0, 4); //�̻� ǥ�� �����
			main=m;
			p=pl;
			gui = Bukkit.createInventory(p, 54, "InspectSample");
			for(int slot = 0; slot<54;slot ++) {
				switch (slot) {
				case 0 :
					Util.Stack(gui, slot, Material.HOPPER, 1, " ");
					break;
				case 18:
				case 20:
				case 22:
				case 24:
				case 26:
					Util.Stack(gui, slot, Material.GLASS_BOTTLE, 1, "��f�� �þ� ��", "��4Ŭ���Ұ�");
					break;
				case 36:
				case 38:
				case 40:
				case 42:
				case 44:
					Util.Stack(gui, slot, Material.GRAY_STAINED_GLASS_PANE, 1, " " ,"��4Ŭ���Ұ�");
					break;
				case 49:
					Util.Stack(gui, slot, Material.BOOK, 1, "��f��lǥ�� �м�",lore);
					break;
				case 53:
					Util.Stack(gui, slot, Material.BLUE_STAINED_GLASS_PANE, 1, "��a��l�þ� �߰��ϱ�");
					break;
				default :
					Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " "); //���
				}
			}
			
			
			status = 1;
		case 1: // �Ʒ� ���� �ݺ� ����
			p.openInventory(gui); //gui ����
			break;
		case 2: //�þ� �غ���
			Util.debugMessage("status 2 ����");
			Util.Stack(gui, 53, Material.ORANGE_STAINED_GLASS_PANE, 1, " " ,"��4Ŭ���Ұ�");
			timer.StartTimer(time,true,20);
			status = 3;
			p.openInventory(gui);
			break;
		case 4:
			Util.debugMessage("status 4 ����");
			prepareSample();//�þ� �غ� �Ϸ�
			p.openInventory(gui);
			break;
		}
		Util.debugMessage("switch�� ��������");
		p.openInventory(gui); //gui ����
		}
	public   void inspectsampleagain() {
		inspectsample(p,main);
	}
	public void prepareSample() {
		Util.debugMessage("�þ� �غ�Ϸ� �ܰ�");
		
		for(int slot = 36; slot<=44 ; slot+=2) Util.Stack(gui, slot, Material.GREEN_STAINED_GLASS_PANE, 1,"��fǥ���� �����ϼ���");
		for (int slot = 18 ; slot<27 ; slot+=2) {
			if ((slot-18)/2!=bad) Util.StackPotion(gui, slot, Color.BLUE, 1, "��f���� �þ�");
			else Util.StackPotion(gui, slot, Color.RED, 1, "��c�̻� �þ�");
			}
		
	}
	public void setStatus(int num) {
		status=num;
	}
	public void checkSample(int num) {
		Util.debugMessage(num+"��"+bad+"��");
		if (num==bad) {
			for(int slot = 36; slot<=44 ; slot+=2) Util.Stack(gui, slot, Material.GRAY_STAINED_GLASS_PANE, 1, " " ,"��4Ŭ���Ұ�");
			status = 100;
			Util.debugMessage(" Ŭ����!");
		}else {
			p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.1f);
			Util.debugMessage(" Ʋ��, �����");
			status=0;
			inspectsampleagain();
		}
			
	}
	
	
@EventHandler
public void onClick(InventoryClickEvent e) {
	
	//Inventory inv = e.getClickedInventory();
	//Player p = (Player) e.getWhoClicked();
	
	
	if(e.getView().getTitle().split(" ")[0].equals("InspectSample")&&e.getCurrentItem()!=null) {
		Material item = e.getCurrentItem().getType();
		if(item!=Material.GREEN_STAINED_GLASS_PANE && item != Material.BLUE_STAINED_GLASS_PANE) {
			e.setCancelled(true);
		}
		if(item == Material.BLUE_STAINED_GLASS_PANE) {
			status = 2;
			inspectsampleagain();
		}
		if(item == Material.GREEN_STAINED_GLASS_PANE) {
			checkSample((e.getRawSlot()%9)/2);
			e.setCancelled(true);
		}
		}
	}
}