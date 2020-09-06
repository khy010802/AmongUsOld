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
		String guiName = "투표";
		Inventory gui;
		
		private boolean isImposter;
		private static HashMap<String, ArrayList<String>> voteMap;
		private static ArrayList<Location> SEATS;
		
		public void setGUI(Player p) { //투표할때 GUI용
			int guiSize=(PlayerData.getPlayerDataList().size()/9+1)*9; //플레이어 수에 따라 유동적으로 gui 크기 조절
			int idx=0;
			this.gui = Bukkit.createInventory(p, guiSize, "DistributePower");
			voteMap= new HashMap<String, ArrayList<String>>();
			for (PlayerData pd : PlayerData.getPlayerDataList()) {
				String name = pd.getName();
				ItemStack item=Util.itemWithName(name);
				ItemMeta meta=item.getItemMeta();
				voteMap.put(pd.getName(), new ArrayList<String>());
				
				isImposter=PlayerData.getPlayerData(p.getDisplayName()).isImposter();//임포스터일시 서로 구별용
				if (isImposter&&pd.isImposter()) {
					meta.setDisplayName("§4"+name);
					meta.addEnchant(Enchantment.MENDING, idx, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);		
				}
				else meta.setDisplayName("§f"+name);
				
				if (!pd.isAlive()) meta.setLore(Arrays.asList("§7사망")); //사망확인
				item.setItemMeta(meta);
				gui.setItem(idx, item);
			}
		}
		public void openGUI(Player p) {
			p.openInventory(this.gui);
		}
		public void setSeats() { //투표가 시작될 때 자리 세팅
			SEATS=LocManager.getLoc("SEATS");
			if(PlayerData.getPlayerDataList().size()>SEATS.size()) { 
				Util.debugMessage("플레이어 수보다 자리가 적습니다.");
				}
			for (int idx =0; idx<PlayerData.getPlayerDataList().size();idx++) {
				;
			}
		}
		private void putVoter(String voter, String voted) {
			voteMap.get(voted).add(voter);
		}
		
}
