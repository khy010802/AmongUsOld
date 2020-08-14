package bepo.au.missions;


import java.util.HashSet;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
 
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import bepo.au.Util;




public class H_EmptyChute implements Listener {
	 Main main;
	 Player p;
	 Inventory gui;
	 int maxLeaves=5;  //�ؾ��ϴ� ������ ���� HARD EASY ���� ����. �ִ� 36��.
	 int remainingLeaves=maxLeaves;
	 int maxslot=45;
	 
	public  void emptychute(Player pl, Main m) {
		main=m;
		p=pl;
		
		reset();
	}
	public  void reset() {
		gui = Bukkit.createInventory(p, maxslot, "EmptyChute");
		gui.setMaxStackSize(1);
		int index=0;
		HashSet<Integer> leafSlots = new HashSet<Integer>();
		while (true) {
			int leafslot = Util.random(3,8)+9*Util.random(0,4);
			Util.debugMessage(leafslot+"�� ������");
			leafSlots.add(leafslot);
			if(leafSlots.size()==maxLeaves) break;
			}
			
			for(int slot = 0 ; slot<maxslot; slot++) {
				int x=slot%9 ,y=slot/9;
				if (x<3) { 
					if(slot==20) Util.Stack(gui, slot, Material.HOPPER, 1, "��f�̰��� �������� ��� Ŭ�����ּ���");
					else if (slot==18) Util.Stack(gui, slot, Material.BOOK, 1, "��f��� ���� û���ϱ�", "��7�������� ��� ȣ�ۿ� Ŭ���ϼ���.");
					else if (x>0&&y>0&&y<=3) Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
					else  Util.Stack(gui, slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
				}
				else if (leafSlots.contains(slot)) {
					
					Util.Stack(gui, slot, Material.KELP, 1, "��f������", "��7�̰��� �� ä�� ȣ�۸� Ŭ���ϼ���.",(index++));
				}
		}
		p.openInventory(gui);
	}

	public void removeLeaf() {
		if (--remainingLeaves==0) {
			Util.debugMessage("Ŭ����!");
			return;
			}
		Util.debugMessage(remainingLeaves+"�� ����");
		
	}
	
	
	
	

@EventHandler
public void onClick(InventoryClickEvent e) {
	if(e.getView().getTitle().equals("EmptyChute")) {
	Util.debugMessage("Ŭ�� �νĵ�");
	//int slot = e.getRawSlot();
	ItemStack itemstack = e.getCurrentItem();
	
	//Inventory gui = e.getClickedInventory();
	//Player p = (Player) e.getWhoClicked();
	
	
		if(e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { //����Ŭ��,����ƮŬ�� ����
			Util.debugMessage("Ŭ�� �Ұ�");
			e.setCancelled(true);
		}
		if(itemstack!=null) {
			if(e.getCursor().getType()==Material.AIR&&itemstack.getType()==Material.KELP) Util.debugMessage("���� Ŭ�� �νĵ�");
			else if(e.getCursor().getType()==Material.KELP&&itemstack.getType()==Material.HOPPER) {
				removeLeaf();
				e.getWhoClicked().setItemOnCursor(null);
				e.setCancelled(true);
			}
			else {
				Util.debugMessage("Ŭ�� �Ұ�");
				e.setCancelled(true);
				}
			}
	}	
	}
	

	@EventHandler
public void onDrag(InventoryDragEvent e) {
	if (e.getView().getTitle().equals("EmptyChute")&&!e.getRawSlots().isEmpty()){
	;
	}
	

}





}
	
	
	
