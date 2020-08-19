package bepo.au.missions;




import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
 
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;



public class DivertPower implements Listener {
	 Main main;
	 Player p;
	 Inventory gui;
	 int roomNum;

	 
	 final Material[] material = {Material.RED_STAINED_GLASS,  //����0
										Material.REDSTONE_BLOCK,//Ȱ��ȭ�� ����1
										Material.YELLOW_STAINED_GLASS_PANE,//����2
										Material.GRAY_STAINED_GLASS_PANE,//ȸ�����3
										Material.WHITE_STAINED_GLASS_PANE,//�Ͼ���4
										Material.LIGHT_GRAY_STAINED_GLASS_PANE//��ȸ��5
	 };
	 final String[] rooms = {"��f�Ϻ� ����",  //����
			 "��� ����",
			 "�����",
			 "��ȣ�� �����",
			 "���ǵ��� ���� ��",
			 "���ؽ�",
			 "��Ž�",
			 "��� ���޽�",
			 "���Ƚ�"
			 };//���
	public  void divertPowerPt1(Player pl, Main m, int roomnumber) {
		main=m;
		p=pl;
		roomNum=roomnumber;
		gui = Bukkit.createInventory(p, 54, "DivertPower");
		reset1();
	}
	public  void divertPowerPt2(Player pl, Main m, int roomnumber) {
		main=m;
		p=pl;
		roomNum=roomnumber;
		gui = Bukkit.createInventory(p, 27, "DivertPower "+rooms[roomNum]);
		reset2();
	}
	public  void reset1() {
		for (int slot = 0; slot<54; slot++) {//gui�κ��丮
			int y = slot/9, x=slot%9;
			Util.debugMessage(slot+"�� �������� ä��ϴ�"+x+","+y);
			if (y==3||y==5) Util.Stack(gui, slot, x==roomNum ? Material.AIR : material[3], 1, " "); //��� ������ ä���
			else if(x==4) {
				Util.Stack(gui, slot, material[3], 1, " ");//��� ������ ä���
			}
			else if(y==4) { //y�� 7�϶�
					if(x==roomNum) {
					Util.Stack(gui, slot, material[1], 1, "��e"+rooms[x]+" ��f����","��7���� �÷� ������ �����ϼ���.");  //Ŭ������ ����
				}else {
					Util.Stack(gui, slot, material[0], 1, "��e"+rooms[x]+" ��f����","��4Ŭ���Ұ�");  //Ŭ���Ұ� ����
				}
			}
			else if(x==4) {
				Util.Stack(gui, slot, material[3], 1, " ");
			}
			else if(y==1||y==2) Util.Stack(gui, slot, material[2], 1," ");
			else if(y==0) Util.Stack(gui, slot, material[5], 1," ");
		}
		p.openInventory(gui);
	}
	public  void reset2() {
		for (int slot = 0; slot<27; slot++) {//gui�κ��丮
			int y = slot/9, x=slot%9;
			if (y==0 || y==2) Util.Stack(gui, slot, material[4], 1, " ");
			else if(x<4) Util.Stack(gui, slot, material[2], 1, " ");
			else if(x==4) Util.Stack(gui, slot, Material.OAK_FENCE_GATE, 1, "��f����", "��aŬ���� ������ �����ϼ���");
			else Util.Stack(gui, slot, material[3], 1, "�� ");
		}
		p.openInventory(gui);
	}
public void checkLever(int num){
		Util.debugMessage(num+"�� checklever ����");
		if (num/9==3) {
			Util.debugMessage("0��");
			Util.Stack(gui, num-27, material[2], 1, " ");
			Util.Stack(gui, num-18, material[2], 1, " ");
			for (int slot = 0; slot<18 ;slot++) {if (slot%9!=num%9) Util.Stack(gui, slot, material[5], 1, " ");}//������ ���� 1�ܰ�
			clear(1); //Ŭ����
		}
		if (num/9==4) {
			Util.debugMessage("1��");
			for (int slot = 0; slot<27 ;slot++) {
				if (slot/9==0) Util.Stack(gui, slot, material[5], 1, " ");//���� 2�ܰ�
				else Util.Stack(gui, slot, material[2], 1, " ");
				}
			}
		else if (num/9==5) {
			Util.debugMessage("2��");
			Util.Stack(gui, num-36, material[5], 1, " ");
			Util.Stack(gui, num-45, material[5], 1, " ");
			for (int slot = 0; slot<18 ;slot++) if (slot%9!=num%9) Util.Stack(gui, slot, material[2], 1, " ");//������ ���� 3�ܰ�
			}
		for (int slot = 4 ; slot<24;slot+=9) Util.Stack(gui, slot, material[3], 1, " ");
		new BukkitRunnable() {
			
			@Override
			public void run() {
				p.updateInventory();
				
			}
		}.runTaskLater(main, 0);
		
		
	}

public void clear(int part) {
	switch (part){
	case 1:
		/*
		 * ��Ʈ1 Ŭ����;
		 * 
		*/
		break;
	case 2:
		for (int slot=13;slot<18;slot++) Util.Stack(gui, slot, material[2], 1, " ");
		/*
		 * ��Ʈ2 Ŭ����;
		 * 
		*/
		break;
	}
}

	@EventHandler
public void onClick(InventoryClickEvent e) {
	if(e.getView().getTitle().split(" ")[0].equals("DivertPower")) {
	Util.debugMessage((e.getCurrentItem()==null)+","+(e.getCursor().getType()==material[1])+"Ŭ�� �νĵ�");
	int slot = e.getRawSlot();
	ItemStack itemstack = e.getCurrentItem();
		if(e.getCurrentItem()!=null) {
			Material item = itemstack.getType();
			if		(item==material[1]) ;
			else if	(item==Material.OAK_FENCE_GATE) {
				clear(2);
				e.setCancelled(true);}
			else	 e.setCancelled(true);
			}
		else {
			Util.debugMessage("���� Ŭ����");
			if(e.getCursor().getType()==material[1]) {
				Util.debugMessage("���彺�� ���� �������");
				checkLever(slot);
				}
		
		
			}
		}
	
	}
	@EventHandler
public void onDrag(InventoryDragEvent e) {
		
	if (e.getView().getTitle().equals("DivertPower")&&!e.getRawSlots().isEmpty()){
		Util.debugMessage("�巡�� �νĵ�");
		for (int slot:e.getRawSlots()) {
			if (gui.getItem(slot)==null) {
				checkLever(slot);
				break;
			}
	}
	}

}





}
	
	
	