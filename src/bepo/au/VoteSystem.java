package bepo.au;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import bepo.au.base.PlayerData;
import bepo.au.manager.LocManager;
import bepo.au.utils.Util;

public class VoteSystem implements Listener  {
		String guiName = "투표";
		Inventory gui;
		
		private boolean isImposter;
		private static HashMap<String, Set<String>> voteMap;
		private static ArrayList<Location> SEATS;
		private static List<PlayerData> dataList;
		
		public void onAssigned(Player p) {
			dataList=PlayerData.getPlayerDataList();
			setGUI(p);
			setSeats();
		}
		
		public void setGUI(Player p) { //투표할때 GUI용
			int guiSize=(dataList.size()/9+1)*9; //플레이어 수에 따라 유동적으로 gui 크기 조절
			int idx=0;
			this.gui = Bukkit.createInventory(p, guiSize, "DistributePower");
			voteMap= new HashMap<String, Set<String>>();
			for (PlayerData pd : dataList) {
				String name = pd.getName();
				ItemStack item=Util.itemWithName(name);
				ItemMeta meta=item.getItemMeta();
				voteMap.put(pd.getName(), new HashSet<String>());
				
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
		
		Material[] clothes = {Material.LEATHER_BOOTS,Material.LEATHER_LEGGINGS,Material.LEATHER_CHESTPLATE};
		public void setSeats() { //투표가 시작될 때 자리 세팅
			SEATS=LocManager.getLoc("SEATS");
			if(dataList.size()>SEATS.size()) { 
				Util.debugMessage("플레이어 수보다 자리가 적습니다.");
				}
			for (int idx =0; idx<dataList.size();idx++) {
				Player currentPlayer =Bukkit.getPlayer(dataList.get(idx).getName());
				currentPlayer.teleport(SEATS.get(idx));//플레이어를 각 자리로 이동
				Vector dir = LocManager.getLoc("Center").get(0).clone().subtract(currentPlayer.getEyeLocation()).toVector();
			    Location loc = currentPlayer.getLocation().setDirection(dir);
			    currentPlayer.teleport(loc);//플레이어방향 바꾸기
				if(!dataList.get(idx).isAlive()) {//플레이어가 유령상태일때
					ArmorStand arm = (ArmorStand) currentPlayer.getWorld().spawnEntity(SEATS.get(idx), EntityType.ARMOR_STAND);
					arm.setInvulnerable(true);
					arm.setCustomName("§7"+currentPlayer.getName());
					arm.addScoreboardTag("forVote");
					EntityEquipment armeq = arm.getEquipment();
					
					armeq.setHelmet(new ItemStack(Material.SKELETON_SKULL));
					for(int idx1 = 0 ; idx1 < 3 ; idx1++) //옷입히기
					{
						ItemStack stack = new ItemStack(clothes[idx1]);
						LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
						meta.setColor(dataList.get(idx).getColor());
						stack.setItemMeta(meta);
						switch(idx1) {
						case 0:
							armeq.setBoots(stack);
							break;
						case 1:
							armeq.setLeggings(stack);
							break;
						case 2:
							armeq.setChestplate(stack);
							break;
						}
					}
				}
			}	
		}
		
		private void putVoter(String voter, String voted) {
			if(PlayerData.getPlayerData(voter).isAlive()) voteMap.get(voted).add(voter);
			else Util.debugMessage("죽은사람이 투표를 시도했습니다.");
		}
		
}
