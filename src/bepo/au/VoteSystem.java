package bepo.au;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import bepo.au.base.PlayerData;
import bepo.au.manager.LocManager;
import bepo.au.utils.Util;

public class VoteSystem extends BukkitRunnable implements Listener  {
		String guiName = "투표";
		Inventory gui;
		private Main main;
		private boolean isImposter;
		private static HashMap<String, ArrayList<String>> voteMap;
		private static ArrayList<Location> SEATS;
		private static List<PlayerData> DATALIST;
		private static ArrayList<String> SURVIVERS;
		private static ArrayList<String> VOTERS;
		private static int remainedVoter;
		private static int guiSize;
		private static ArrayList<ArmorStand> armorstandList;
		public static enum resultType {
					
					TIE,
					SKIP,
					CHOOSED
					
				}
		public static int voteTimer;
		
		public VoteSystem(Main main) {
			this.main=main;
			voteTimer=Main.VOTE_SEC*20;
		}
		private static resultType voteResult;
		
		/*
	   투표 시작 관련
	   */
		public void onAssigned(Player p) {//p는 주체
			DATALIST=PlayerData.getPlayerDataList();
			for (PlayerData pd : DATALIST) {
				if (pd.isAlive()) {SURVIVERS.add(pd.getName());}
			}
			remainedVoter=SURVIVERS.size();
			VOTERS.clear();
			setGUI(p);
			setSeats();
			this.runTaskTimer(main, 0L, 1L);
		}
		
		public void setGUI(Player p) { //투표할때 GUI용
			guiSize=((DATALIST.size()+3)/9+1)*9; //플레이어 수에 따라 유동적으로 gui 크기 조절
			int idx=0;
			this.gui = Bukkit.createInventory(p, guiSize, guiName);
			voteMap= new HashMap<String, ArrayList<String>>();
			for (PlayerData pd : DATALIST) {
				String name = pd.getName();
				ItemStack item=Util.getSkull(name);
				ItemMeta meta=item.getItemMeta();
				voteMap.put(pd.getName(), new ArrayList<String>());
				
				isImposter=PlayerData.getPlayerData(p.getDisplayName()).isImposter();//임포스터일시 서로 구별용
				if (isImposter&&pd.isImposter()) {
					meta.setDisplayName("§4"+name);
					meta.addEnchant(Enchantment.MENDING, idx, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);		
				}
				else meta.setDisplayName("§f"+name);
				
				if (!pd.isAlive()) { meta.setLore(Arrays.asList("§7사망")); } //사망확인
				else { 
					meta.setLore(Arrays.asList("§7투표"));
					}
				item.setItemMeta(meta);
				gui.setItem(idx, item);
				idx++;
			}
			Util.Stack(gui, guiSize-2, Material.CLOCK,voteTimer/20, "남은시간 : "+voteTimer/20);
			Util.Stack(gui, guiSize-1, Material.MAGENTA_GLAZED_TERRACOTTA,1, "SKIP","§7투표");
		}
		public void openGUI(Player p) {
			p.openInventory(this.gui);
		}		
		@EventHandler
		public void onClick(InventoryClickEvent e) {
			ItemStack its = e.getCurrentItem();
			if (guiName==e.getView().getTitle()&&its != null) {
				if(its.getItemMeta().hasLore()&&its.getItemMeta().getLore().get(0).equals("§7투표")) {
					putVoter(e.getWhoClicked().getName(),its.getItemMeta().getDisplayName());
				}
				e.setCancelled(true);
			}

		}
		Material[] clothes = {Material.LEATHER_BOOTS,Material.LEATHER_LEGGINGS,Material.LEATHER_CHESTPLATE};
		public void setSeats() { //투표가 시작될 때 자리 세팅
			SEATS=LocManager.getLoc("SEATS");
			if(DATALIST.size()>SEATS.size()) { 
				Util.debugMessage("플레이어 수보다 자리가 적습니다.");
				}
			for (int idx =0; idx<DATALIST.size();idx++) {
				Player currentPlayer =Bukkit.getPlayer(DATALIST.get(idx).getName());
				currentPlayer.teleport(SEATS.get(idx));//플레이어를 각 자리로 이동
				Vector dir = LocManager.getLoc("Center").get(0).clone().subtract(currentPlayer.getEyeLocation()).toVector();
			    Location loc = currentPlayer.getLocation().setDirection(dir);
			    currentPlayer.teleport(loc);//플레이어방향 바꾸기
			    
				if(!DATALIST.get(idx).isAlive()) {//플레이어가 유령상태일때
					ArmorStand arm = (ArmorStand) currentPlayer.getWorld().spawnEntity(SEATS.get(idx), EntityType.ARMOR_STAND);
					arm.setInvulnerable(true);
					arm.setCustomName("§7"+currentPlayer.getName());
					arm.addScoreboardTag("forVote");
					armorstandList.add(arm);
					EntityEquipment armeq = arm.getEquipment();
					
					armeq.setHelmet(new ItemStack(Material.SKELETON_SKULL));
					for(int idx1 = 0 ; idx1 < 3 ; idx1++) //옷입히기
					{
						ItemStack stack = new ItemStack(clothes[idx1]);
						LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
						meta.setColor(DATALIST.get(idx).getColor());
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
		/*
		   투표 진행 관련
		   */
		private void putVoter(String voter, String voted) {
			if(voteTimer<=0) return;
			if(VOTERS.contains(voter)) return;
			
			if(voted=="SKIP") {
				voteMap.get(voted).add(voter);
				remainedVoter--;
				VOTERS.add(voter);
			}
			else if(PlayerData.getPlayerData(voter).isAlive()) {
				voteMap.get(voted).add(voter);
				remainedVoter--;
				VOTERS.add(voter);
			}
			else Util.debugMessage("죽은사람이 투표를 시도했습니다.");
			if (remainedVoter==SURVIVERS.size()) {
				voteover();
			}
		}
		
		/*
		   투표 종료 관련
		   */
		private void timeover() { //시간 오버
			voteover();
		}
		
		public static String top=null;
		
		private void voteover() {//종료 페이즈
			int maximum = -1;
			int current;
			boolean tied = false;
			
			for (String voted : voteMap.keySet()) {
				current=voteMap.get(voted).size();
				if(maximum<current) {
					maximum=current;
					top=voted;
					tied = false;
				}else if(maximum==current) {
					tied = true;
				}
			}
			if (tied) {
				voteResult=resultType.TIE;
				top=null;
				}
			else if (top=="SKIP") {
				voteResult=resultType.SKIP;
			}
			else {
				voteResult=resultType.CHOOSED;
				}
				
			}

		/*
		 * 시간관련
		 */
		public int getRemainedTick() {
			return voteTimer;
		}
		@Override
		public void run() {
			voteTimer--;
			Util.Stack(gui, guiSize-2, Material.CLOCK,voteTimer/20, "남은시간 : "+voteTimer/20);
			switch(voteTimer) {//투표 종료 후 이벤트 관련
			case 0:
				timeover();
			case -25://투표 완전 종료.
				for (ArmorStand arm : armorstandList) {
					arm.remove();
				}
				this.cancel();
			}
		}

		public static resultType getVoteResult() {
			return voteResult;
		}

		public static void setVoteResult(resultType voteResult) {
			VoteSystem.voteResult = voteResult;
		}
}