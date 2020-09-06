package bepo.au;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import bepo.au.base.PlayerData;
import bepo.au.manager.LocManager;
import bepo.au.utils.Util;

public class VoteSystem implements Listener  {
		String guiName = "��ǥ";
		Inventory gui;
		
		private boolean isImposter;
		private static HashMap<String, ArrayList<String>> voteMap;
		private static ArrayList<Location> SEATS;
		
		public void setGUI(Player p) { //��ǥ�Ҷ� GUI��
			int guiSize=(PlayerData.getPlayerDataList().size()/9+1)*9; //�÷��̾� ���� ���� ���������� gui ũ�� ����
			int idx=0;
			this.gui = Bukkit.createInventory(p, guiSize, "DistributePower");
			voteMap= new HashMap<String, ArrayList<String>>();
			for (PlayerData pd : PlayerData.getPlayerDataList()) {
				String name = pd.getName();
				ItemStack item=Util.itemWithName(name);
				ItemMeta meta=item.getItemMeta();
				voteMap.put(pd.getName(), new ArrayList<String>());
				
				isImposter=PlayerData.getPlayerData(p.getDisplayName()).isImposter();//���������Ͻ� ���� ������
				if (isImposter&&pd.isImposter()) {
					meta.setDisplayName("��4"+name);
					meta.addEnchant(Enchantment.MENDING, idx, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);		
				}
				else meta.setDisplayName("��f"+name);
				
				if (!pd.isAlive()) meta.setLore(Arrays.asList("��7���")); //���Ȯ��
				item.setItemMeta(meta);
				gui.setItem(idx, item);
			}
		}
		public void openGUI(Player p) {
			p.openInventory(this.gui);
		}
		public void setSeats() { //��ǥ�� ���۵� �� �ڸ� ����
			SEATS=LocManager.getLoc("SEATS");
			if(PlayerData.getPlayerDataList().size()>SEATS.size()) { 
				Util.debugMessage("�÷��̾� ������ �ڸ��� �����ϴ�.");
				}
			for (int idx =0; idx<PlayerData.getPlayerDataList().size();idx++) {
				;
			}
		}
		private void putVoter(String voter, String voted) {
			voteMap.get(voted).add(voter);
		}
		
}
