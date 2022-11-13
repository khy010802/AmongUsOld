package bepo.au.function;

import bepo.au.GameTimer;
import bepo.au.GameTimer.Status;
import bepo.au.Main;
import bepo.au.Main.SETTING;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.manager.LocManager;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VoteSystem extends BukkitRunnable implements Listener {

	public static VoteSystem PROGRESSED_VOTE = null;
	private static World world;

	public static void start(World w, String name, boolean reported, String corpse) {
		PROGRESSED_VOTE = new VoteSystem();
		world = w;
		Vent.closeAll();
		Util.spawnEmergArmorStand(w);
		Util.resetCorpse();
		GameTimer.EMERG_REMAIN_TICK = SETTING.EMER_BUTTON_COOL_SEC.getAsInteger() * 20;
		Main.gt.setStatus(Status.VOTING);
		Bukkit.getPluginManager().registerEvents(PROGRESSED_VOTE, Main.getInstance());
		String title = reported ? "§c[시체 발견]" : "§a[긴급 소집]";
		String msg = PlayerData.getPlayerData(name).getColor().getChatColor() + name + (reported ? "§f님께서 " + PlayerData.getPlayerData(corpse).getColor().getChatColor() + corpse + "§f님의 시체를 발견하셨습니다." : "§f님께서 긴급 소집 버튼을 눌렀습니다.");
		for (Player ap : Bukkit.getOnlinePlayers()) {
			PlayerData pd = PlayerData.getPlayerData(ap.getName());
			if (pd != null) {
				PROGRESSED_VOTE.onAssigned(ap);
			}

			ap.sendTitle(title, msg, 0, 80, 20);
			ap.setItemOnCursor(new ItemStack(Material.AIR));
			ap.closeInventory();
		}
		
		Bukkit.broadcastMessage("§f=======================");
		Bukkit.broadcastMessage("§f");
		Bukkit.broadcastMessage(title + " " + msg);
		Bukkit.broadcastMessage("§f");
		Bukkit.broadcastMessage("§f=======================");
		
		new BukkitRunnable() {
			public void run() {

				if (Main.gt == null || Main.gt.getStatus() != Status.VOTING || PROGRESSED_VOTE == null)
					return;
				/*
				 * int temp = 0; List<Location> locs = LocManager.getLoc("SEATS");
				 * 
				 * for (Player ap : Bukkit.getOnlinePlayers()) { if
				 * (PlayerData.getPlayerData(ap.getName()) != null) { PlayerData pd =
				 * PlayerData.getPlayerData(ap.getName());
				 * 
				 * if (pd.isAlive()) { if (locs.size() <= temp) temp = 0;
				 * ap.teleport(locs.get(temp)); PlayerUtil.sitChair(ap, locs.get(temp)); temp++;
				 * } else { ap.teleport(locs.get(0)); } } else { ap.teleport(locs.get(0)); } }
				 */

				PROGRESSED_VOTE.setSeats();
				PROGRESSED_VOTE.user_setting();
				PROGRESSED_VOTE.runTaskTimer(Main.getInstance(), 0L, 1L);
			}
		}.runTaskLater(Main.getInstance(), 100L);
	}

	public static void end_check() {

		if (top != null && voteResult == resultType.CHOOSED)
			PlayerData.getPlayerData(top).kill(true);

		if (GameTimer.WIN_REASON != null)
			return;

		for (int i = 0; i < DATALIST.size(); i++) {
			PlayerData pd = DATALIST.get(i);
			Location loc = LocManager.getLoc("SEATS").get(i);
			Player p = Bukkit.getPlayer(pd.getName());
			if (p != null) {
				p.teleport(loc);
				p.getInventory().setHeldItemSlot(0);
				pd.resetKillCool(true);
				if (pd.isAlive()) {
					p.setAllowFlight(false);
					p.setFlying(false);
					PlayerUtil.setInvisible(p, false);
					for (Player ap : Bukkit.getOnlinePlayers())
						PlayerUtil.showPlayer(ap, p);
					p.setGameMode(GameMode.ADVENTURE);
				} else {
					p.setGameMode(GameMode.SPECTATOR);
				}
			}
		}
		
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(PlayerData.getPlayerData(ap.getName()) == null) ap.teleport(LocManager.getLoc("SEATS").get(0));
		}

		Sabotage.saboResetAll(false);
		VoteSystem.PROGRESSED_VOTE = null;
		Main.gt.setStatus(Status.WORKING);

	}

	public static void resetArmorStand() {
		if (armorstandList == null)
			return;
		for (ArmorStand as : armorstandList)
			as.remove();
		armorstandList.clear();
	}

	public void voteover() {// 종료 페이즈
		int maximum = -1;
		int current;
		boolean tied = false;

		for (Player ap : Bukkit.getOnlinePlayers()) {
			ap.closeInventory();
			gui_list.remove(ap.getName());
		}

		resetArmorStand();

		Util.spawnEmergArmorStand(world);

		HandlerList.unregisterAll(PROGRESSED_VOTE);

		for (String voted : voteMap.keySet()) {
			current = voteMap.get(voted).size();
			if (maximum < current) {
				maximum = current;
				top = voted;
				tied = false;
			} else if (maximum == current) {
				tied = true;
			}
		}
		if (tied) {
			voteResult = resultType.TIE;
			top = null;
		} else if (top == "SKIP") {
			voteResult = resultType.SKIP;
		} else {
			voteResult = resultType.CHOOSED;
		}

		startNotice();
	}

	String guiName = "투표";
	private boolean isImposter;

	private static HashMap<String, Inventory> gui_list = new HashMap<String, Inventory>();

	private static HashMap<String, HashSet<String>> voteMap;
	private static ArrayList<Location> SEATS;
	private static List<PlayerData> DATALIST;
	private static ArrayList<String> SURVIVORS;
	public static ArrayList<String> VOTERS;
	public static int remainedVoter;
	private static int guiSize;

	private static ArrayList<ArmorStand> armorstandList;

	public static VoteResultTimer vrt;

	public static enum resultType {

		TIE, SKIP, CHOOSED

	}

	public static int voteTimer;

	public VoteSystem() {
		voteTimer = (SETTING.VOTE_MAIN_SEC.getAsInteger() + SETTING.VOTE_PREPARE_SEC.getAsInteger()) * 20;
		SURVIVORS = new ArrayList<String>();
		VOTERS = new ArrayList<String>();
		DATALIST = PlayerData.getPlayerDataList();
		voteMap = new HashMap<String, HashSet<String>>();
		voteMap.put("SKIP", new HashSet<String>());
		for (PlayerData pd : DATALIST) {
			if (pd.isAlive()) {
				SURVIVORS.add(pd.getName());
			}
		}
		remainedVoter = SURVIVORS.size();
		VOTERS.clear();
		top = null;
		voteResult = null;

	}

	private static resultType voteResult = null;

	/*
	 * 투표 시작 관련
	 */
	public void onAssigned(Player p) {// p는 주체
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		pd.confirmVent(p, true);
		if (pd.isWatchingCCTV())
			pd.exitCCTV(p);
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 10, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 250, true));
		p.getInventory().setHeldItemSlot(8);
		setGUI(p);

	}

	public void setGUI(Player p) { // 투표할때 GUI용
		guiSize = ((DATALIST.size() + 3) / 9 + 1) * 9; // 플레이어 수에 따라 유동적으로 gui 크기 조절
		int idx = 0;

		Inventory gui = Bukkit.createInventory(p, guiSize, guiName);
		for (PlayerData pd : DATALIST) {
			String name = pd.getName();
			ItemStack item = pd.isAlive() ? pd.getHead() : new ItemStack(Material.SKELETON_SKULL);
			ItemMeta meta = item.getItemMeta();
			voteMap.put(pd.getName(), new HashSet<String>());

			isImposter = GameTimer.IMPOSTER.contains(p.getName());// 임포스터일시 서로 구별용
			if (isImposter && GameTimer.IMPOSTER.contains(name)) {
				meta.setDisplayName("§l§n§4"+ name);
				meta.addEnchant(Enchantment.MENDING, idx, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			} else
				meta.setDisplayName(pd.getColor().getChatColor() + name);

			if (!pd.isAlive()) {
				meta.setLore(Arrays.asList("§7사망"));
			} // 사망확인
			else {
				meta.setLore(Arrays.asList("§7투표"));
			}
			item.setItemMeta(meta);
			gui.setItem(idx, item);
			idx++;
		}
		Util.Stack(gui, guiSize - 2, Material.CLOCK, voteTimer / 20 + 1, "남은시간 : " + (voteTimer / 20 + 1));
		Util.Stack(gui, guiSize - 1, Material.MAGENTA_GLAZED_TERRACOTTA, 1, "§fSKIP", "§7투표");
		gui_list.put(p.getName(), gui);
	}

	public void updateGUI(Inventory inv) {
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack is = inv.getItem(i);
			if (is != null && is.getType() == Material.PLAYER_HEAD) {
				String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
				if (VOTERS.contains(name)) {
					inv.setItem(i, Util.enchantItem(is));
				}
			}
		}
	}

	public void openGUI(Player p) {
		if (!gui_list.containsKey(p.getName()))
			return;

		updateGUI(gui_list.get(p.getName()));
		p.openInventory(gui_list.get(p.getName()));
	}

	// @EventHandler
	public void onToggleFlight(PlayerToggleFlightEvent event) {
		if (vrt != null && event.isFlying())
			event.setCancelled(true);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		ItemStack its = e.getCurrentItem();
		if (guiName == e.getView().getTitle()) {
			e.setCancelled(true);
			if (its != null && voteResult == null) {
				if (its.getItemMeta().hasLore() && its.getItemMeta().getLore().get(0).equals("§7투표")) {
					putVoter((Player) e.getWhoClicked(), its);
				}
			}
		}
	}

	Material[] clothes = { Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE };

	public void user_setting() {
		for (Player ap : Bukkit.getOnlinePlayers()) {
			PlayerData pd = PlayerData.getPlayerData(ap.getName());
			if (pd != null && pd.isAlive())
				PlayerUtil.setInvisible(ap, false);
			for (Player ap2 : GameTimer.ALIVE_PLAYERS) {
				ap.showPlayer(Main.getInstance(), ap2);
			}
			ap.removePotionEffect(PotionEffectType.BLINDNESS);
			ap.removePotionEffect(PotionEffectType.SLOW);
			ap.removePotionEffect(PotionEffectType.JUMP);
		}
	}

	public void user_sit(boolean first) {
		for (int idx = 0; idx < DATALIST.size(); idx++) {
			Player currentPlayer = Bukkit.getPlayer(DATALIST.get(idx).getName());
			if (currentPlayer != null && currentPlayer.getVehicle() == null && DATALIST.get(idx).isAlive()) {

				if (first)
					currentPlayer.teleport(SEATS.get(idx));// 플레이어를 각 자리로 이동

				PlayerUtil.sitChair(currentPlayer, SEATS.get(idx));
			}

		}
	}

	/*final Material[] WHITELIST = { Material.LEATHER_HELMET, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS,
			Material.LEATHER_CHESTPLATE, ItemList.I_SABOTAGE_CRIT.getType(), ItemList.I_SABOTAGE_DOOR.getType(),
			ItemList.I_SABOTAGE_GUI.getType(), ItemList.I_SWORD.getType(), ItemList.VOTE_PAPER.getType() };
*/
	public void setSeats() { // 투표가 시작될 때 자리 세팅

		SEATS = LocManager.getLoc("SEATS");
		if (DATALIST.size() > SEATS.size()) {
			Util.debugMessage("플레이어 수보다 자리가 적습니다. 임의로 자리를 늘리고 싶으면 /au locate를 사용해주세요.");
		}

		user_sit(true);
		armorstandList = new ArrayList<ArmorStand>();

		for (int idx = 0; idx < DATALIST.size(); idx++) {
			PlayerData pd = DATALIST.get(idx);
			Player currentPlayer = Bukkit.getPlayer(pd.getName());

			// currentPlayer.getInventory().clear();

			if (currentPlayer != null) {
				currentPlayer.setAllowFlight(true);
				if (!pd.isAlive())
					armorstandList.add(Util.spawnArmorStand(pd, SEATS.get(idx), false));
				else
					currentPlayer.getInventory().addItem(ItemList.VOTE_PAPER.clone());
			}
		}
	}

	/*
	 * 투표 진행 관련
	 */
	private void putVoter(Player p, ItemStack is) {

		String voter = p.getName();
		String voted = is.getItemMeta().getDisplayName();
		voted = ChatColor.stripColor(is.getItemMeta().getDisplayName());

		if (voteTimer > SETTING.VOTE_MAIN_SEC.getAsInteger() * 20) {
			p.sendMessage(Main.PREFIX + "§c아직 투표 시간이 아닙니다.");
			return;
		}

		if (voteTimer <= 0)
			return;
		if (VOTERS.contains(voter)) {
			p.sendMessage(Main.PREFIX + "§c투표를 번복할 수 없습니다.");
			return;
		}

		if (!PlayerData.getPlayerData(voter).isAlive()) {
			p.sendMessage(Main.PREFIX + "§c죽은 사람은 투표를 할 수 없습니다.");
			Util.debugMessage("죽은사람이 투표를 시도했습니다.");
			return;
		}

		/*
		 * if (voted.equalsIgnoreCase("SKIP")) { Bukkit.broadcastMessage("SKIP voted");
		 * }
		 */
		voteMap.get(voted).add(voter);
		remainedVoter--;
		VOTERS.add(voter);
		Bukkit.broadcastMessage(Main.PREFIX + PlayerData.getPlayerData(voter).getColor().getChatColor() + voter
				+ "§f님이 투표하셨습니다. §e(남은 인원 : " + remainedVoter + "명)");

		if (!voted.equalsIgnoreCase("SKIP"))
			p.sendMessage(
					Main.PREFIX + PlayerData.getPlayerData(voted).getColor().getChatColor() + voted + "§f에게 투표했습니다.");
		else
			p.sendMessage(Main.PREFIX + "스킵에 투표했습니다.");
		Util.enchantItem(is);
		updateGUI(gui_list.get(p.getName()));
		if (remainedVoter == 0) {
			voteover();
		}
	}

	/*
	 * 투표 종료 관련
	 */
	private void timeover() { // 시간 오버
		voteover();
	}

	public static String top = null;

	/*
	 * 시간관련
	 */
	public int getRemainedTick() {
		return voteTimer;
	}

	private void startNotice() {
		VoteResultTimer vrt = new VoteResultTimer();
		vrt.runTaskTimer(Main.getInstance(), 0L, 1L);
		VoteSystem.vrt = vrt;
	}

	@Override
	public void run() {
		voteTimer--;

		if (VoteSystem.vrt != null) {
			this.cancel();
			return;
		}

		switch (voteTimer) {// 투표 종료 후 이벤트 관련
		case 0:
			timeover();
			this.cancel();
			return;
		}

		if (voteTimer >= -1 && voteTimer % 20 == 0) {
			if (voteTimer >= SETTING.VOTE_MAIN_SEC.getAsInteger() * 20)
				Util.getEmergArmorStand().setCustomName(
						"§f투표 시작까지 : §a" + ((voteTimer - SETTING.VOTE_MAIN_SEC.getAsInteger() * 20) / 20 + 1) + "§f초");
			else
				Util.getEmergArmorStand().setCustomName("§f투표 종료까지 : §a" + (voteTimer / 20 + 1) + "§f초");
			user_sit(false);
			for (Inventory gui : gui_list.values()) {
				Util.Stack(gui, guiSize - 2, Material.CLOCK, voteTimer / 20 + 1,
						"§f남은시간 : §a" + (voteTimer / 20 + 1) + "§f초");

				for (HumanEntity he : gui.getViewers()) {
					((Player) he).updateInventory();
				}
			}
		}
	}

	public static resultType getVoteResult() {
		return voteResult;
	}

	public static void setVoteResult(resultType voteResult) {
		VoteSystem.voteResult = voteResult;
	}

	public class VoteResultTimer extends BukkitRunnable {

		private HashMap<String, Location> prev_locs = new HashMap<String, Location>();
		private Location tploc;

		private List<Location> locs;
		private List<Location> changed_locs;

		private final int add_x_value;
		private final int add_z_value;

		private BlockFace bf;

		public VoteResultTimer() {

			tploc = LocManager.getLoc("VoteNotice").get(0);
			locs = LocManager.getLoc("VoteNoticeArmorStand");
			changed_locs = new ArrayList<Location>();
			Location dirloc = LocManager.getLoc("VoteNoticeArmorStand_DIR").get(0);
			Location arloc = locs.get(0);
			add_x_value = dirloc.getBlockX() == arloc.getBlockX() ? 0
					: (dirloc.getBlockX() > arloc.getBlockX() ? 1 : -1);
			add_z_value = dirloc.getBlockZ() == arloc.getBlockZ() ? 0
					: (dirloc.getBlockZ() > arloc.getBlockZ() ? 1 : -1);

			float f = tploc.getYaw();

			if (f >= 45 && f < 135)
				bf = BlockFace.WEST;
			else if (f >= 135 && f < 225)
				bf = BlockFace.NORTH;
			else if (f >= 225 && f >= 315)
				bf = BlockFace.EAST;
			else
				bf = BlockFace.SOUTH;

			for (Player ap : Bukkit.getOnlinePlayers()) {
				PlayerUtil.setInvisible(ap, true);
				ap.getInventory().remove(ItemList.VOTE_PAPER.getType());
				ap.setAllowFlight(true);
				ap.setFlying(true);
				ap.teleport(tploc);
				ap.setGameMode(GameMode.SPECTATOR);
				prev_locs.put(ap.getName(), ap.getLocation());
			}

			for (int temp = 0; temp < DATALIST.size(); temp++) {
				PlayerData pd = DATALIST.get(temp);
				armorstandList.add(Util.spawnArmorStand(pd, locs.get(temp), pd.isAlive()));
			}

			Location loc1 = locs.get(12).clone();
			Location loc2 = locs.get(12).clone().add(0, 1, 0);

			changed_locs.add(loc1);
			changed_locs.add(loc2);
		}

		private String getGeneralTitle() {
			if (SETTING.NOTICE_IMPOSTER.getAsBoolean())
				return "남은 임포스터 : §c" + getRemainImposters() + "명";
			else
				return "";
		}

		private int getRemainImposters() {
			int r = GameTimer.ALIVE_IMPOSTERS.size();
			if (top != null && GameTimer.ALIVE_IMPOSTERS.contains(top))
				return r - 1;
			else
				return r;
		}

		private void resetField() {
			for (Location loc : changed_locs) {
				loc.getBlock().setType(Material.AIR);
			}
			for (ArmorStand as : armorstandList) {
				if (as != null)
					as.remove();
			}
			armorstandList.clear();
		}

		private boolean placeVote(String name, Location loc, int temp) {

			if (!voteMap.containsKey(name) || voteMap.get(name).size() <= temp)
				return false;

			loc.add(add_x_value * (temp + 1), 0, add_z_value * (temp + 1));
			Block wool = loc.getBlock();
			Block head = loc.add(0, 1, 0).getBlock();
			List<String> templist = new ArrayList<>(voteMap.get(name));

			PlayerData pd = PlayerData.getPlayerData(templist.get(temp));
			wool.setType(Material.getMaterial(pd.getColor().getDyeColor().toString() + "_WOOL"));

			head.setType(Material.PLAYER_HEAD);
			Skull skull = (Skull) head.getState();
			skull.setOwningPlayer(Bukkit.getOfflinePlayer(pd.getUUID()));

			Rotatable bd = (Rotatable) skull.getBlockData();
			bd.setRotation(bf);
			skull.setBlockData(bd);

			skull.update();

			changed_locs.add(wool.getLocation());
			changed_locs.add(head.getLocation());

			return true;
		}

		private void end() {
			for (ArmorStand as : armorstandList)
				if (as != null)
					as.remove();
			armorstandList.clear();
			this.cancel();
			VoteSystem.vrt = null;
			AdminMap.ROOMS.resetAllplayer();
			end_check();

		}

		private final int LETTER_DELAY = 2;

		private int timer = 0;
		private boolean all_noticed = false;
		private int reset_time = -1;

		private String title;
		private String subtitle;

		private ArmorStand corpse;

		private int end_time = -1;

		public void run() {

			if (Main.gt == null || VoteSystem.PROGRESSED_VOTE == null || VoteSystem.vrt == null) {
				resetField();
				VoteSystem.vrt = null;
				this.cancel();
				return;
			}

			timer++;

			for (Player ap : Bukkit.getOnlinePlayers())
				ap.teleport(tploc);

			if (end_time > 0) {
				if (end_time == timer)
					end();
			}

			// 추방
			if (reset_time > 0 && reset_time <= timer) {
				if (timer == reset_time) {
					resetField();
				} else if (timer == reset_time + 40) {
					title = getGeneralTitle();
					switch (voteResult) {
					case TIE:
						subtitle = "아무도 추방되지 않았습니다 (동점)";
						break;
					case SKIP:
						subtitle = "아무도 추방되지 않았습니다 (투표 건너 뜀)";
						break;
					case CHOOSED:
						if(Main.SETTING.NOTICE_IMPOSTER.getAsBoolean())
							subtitle = top + "님은 임포스터" + (GameTimer.ALIVE_IMPOSTERS.contains(top) ? "였습니다." : "가 아니었습니다.");
						else
							subtitle = top + "님이 추방되셨습니다.";
						break;
					}
				} else if (timer > reset_time + 40) {
					int tick = timer - reset_time - 40;

					if (voteResult == resultType.CHOOSED) {

						if (tick == 1) {
							corpse = Util.spawnArmorStand(PlayerData.getPlayerData(top),
									LocManager.getLoc("ImposterNoticeArmorStand").get(0), true);
							armorstandList.add(corpse);
						}

						Location tp = corpse.getLocation().clone();
						tp.add(((double) add_x_value) * 0.23D, 0, ((double) add_z_value) * 0.23D);
						tp.setYaw(tp.getYaw() + 10F);
						corpse.teleport(tp);
					}

					if (timer % LETTER_DELAY == 0) {
						int endindex = tick / LETTER_DELAY;
						if (subtitle.length() > endindex) {
							for (Player ap : Bukkit.getOnlinePlayers()) {
								ap.sendTitle("", subtitle.substring(0, endindex), 0, 10, 0);
								ap.playSound(ap.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.5F, 2.0F);
							}

						} else {

							for (Player ap : Bukkit.getOnlinePlayers()) {
								ap.sendTitle(title, subtitle, 0, 10, 0);
							}
							if (end_time == -1)
								end_time = timer + 80;
						}
					}

				}
			}

			// 투표 결과 알림
			if (timer % 20 == 0) {
				if (timer >= 60) {

					if (all_noticed && reset_time < 0) {
						reset_time = timer + 100;
						return;
					}

					int temp = (timer / 20) - 3;
					all_noticed = true;
					for (int i = 0; i < DATALIST.size(); i++) {
						PlayerData pd = DATALIST.get(i);
						Location loc = locs.get(i).clone().add(add_x_value, 0, add_z_value);
						if (placeVote(pd.getName(), loc, temp))
							all_noticed = false;
					}

					if (placeVote("SKIP", locs.get(12).clone().add(add_x_value, 0, add_z_value), temp))
						all_noticed = false;
				}

			}

		}

	}
}