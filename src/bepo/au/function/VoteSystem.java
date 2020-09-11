package bepo.au.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
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

import bepo.au.GameTimer;
import bepo.au.GameTimer.Status;
import bepo.au.Main;
import bepo.au.base.PlayerData;
import bepo.au.manager.LocManager;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;

public class VoteSystem extends BukkitRunnable implements Listener {
	
	public static VoteSystem PROGRESSED_VOTE = null;
	
	public static void start(String name, boolean reported) {
		PROGRESSED_VOTE = new VoteSystem();
		Main.gt.setStatus(Status.VOTING);
		Bukkit.getPluginManager().registerEvents(PROGRESSED_VOTE, Main.getInstance());
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(reported) ap.sendTitle("��c��ü �߰�", PlayerData.getPlayerData(name).getColor().getChatColor() + name + "��f�Բ��� ��ü�� �߰��ϼ̽��ϴ�.", 0, 80, 20);
			else ap.sendTitle("��a��� ����", PlayerData.getPlayerData(name).getColor().getChatColor() + name + "��f�Բ��� ��� ���� ��ư�� �������ϴ�.", 0, 80, 20);
			
			if(PlayerData.getPlayerData(ap.getName()) != null && PlayerData.getPlayerData(ap.getName()).isAlive()) {
				contents.put(ap.getName(), ap.getInventory().getContents().clone());
			}
			
		}
		
		new BukkitRunnable() {
			public void run() {
				
				if(Main.gt == null || Main.gt.getStatus() != Status.VOTING) return;
				
				int temp = 0;
				List<Location> locs = LocManager.getLoc("SEATS");
				
				for(Player ap : Bukkit.getOnlinePlayers()) {
					if(PlayerData.getPlayerData(ap.getName()) != null) {
						PlayerData pd = PlayerData.getPlayerData(ap.getName());
						
						if(pd.isAlive()) {
							if(locs.size() <= temp) temp = 0;
							ap.teleport(locs.get(temp));
							PlayerUtil.sitChair(ap, locs.get(temp));
							temp++;
						} else {
							ap.teleport(locs.get(0));
						}
					} else {
						ap.teleport(locs.get(0));
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 100L);
	}
	
	
	
	String guiName = "��ǥ";
	Inventory gui;
	private Main main;
	private boolean isImposter;
	
	private static HashMap<String, ItemStack[]> contents = new HashMap<String, ItemStack[]>();
	
	private static HashMap<String, ArrayList<String>> voteMap;
	private static ArrayList<Location> SEATS;
	private static List<PlayerData> DATALIST;
	private static ArrayList<String> SURVIVERS;
	private static ArrayList<String> VOTERS;
	private static int remainedVoter;
	private static int guiSize;
	private static ArrayList<ArmorStand> armorstandList;

	public static enum resultType {

		TIE, SKIP, CHOOSED

	}

	public static int voteTimer;

	public VoteSystem() {
		this.main = Main.getInstance();
		voteTimer = Main.VOTE_SEC * 20;
	}

	private static resultType voteResult;

	/*
	 * ��ǥ ���� ����
	 */
	public void onAssigned(Player p) {// p�� ��ü
		DATALIST = PlayerData.getPlayerDataList();
		for (PlayerData pd : DATALIST) {
			if (pd.isAlive()) {
				SURVIVERS.add(pd.getName());
			}
		}
		remainedVoter = SURVIVERS.size();
		VOTERS.clear();
		setGUI(p);
		setSeats();
		this.runTaskTimer(main, 0L, 1L);
	}

	public void setGUI(Player p) { // ��ǥ�Ҷ� GUI��
		guiSize = ((DATALIST.size() + 3) / 9 + 1) * 9; // �÷��̾� ���� ���� ���������� gui ũ�� ����
		int idx = 0;
		this.gui = Bukkit.createInventory(p, guiSize, guiName);
		voteMap = new HashMap<String, ArrayList<String>>();
		for (PlayerData pd : DATALIST) {
			String name = pd.getName();
			ItemStack item = Util.getSkull(name);
			ItemMeta meta = item.getItemMeta();
			voteMap.put(pd.getName(), new ArrayList<String>());

			isImposter = GameTimer.IMPOSTER.contains(p.getName());// ���������Ͻ� ���� ������
			if (isImposter && GameTimer.IMPOSTER.contains(p.getName())) {
				meta.setDisplayName("��4" + name);
				meta.addEnchant(Enchantment.MENDING, idx, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			} else
				meta.setDisplayName("��f" + name);

			if (!pd.isAlive()) {
				meta.setLore(Arrays.asList("��7���"));
			} // ���Ȯ��
			else {
				meta.setLore(Arrays.asList("��7��ǥ"));
			}
			item.setItemMeta(meta);
			gui.setItem(idx, item);
			idx++;
		}
		Util.Stack(gui, guiSize - 2, Material.CLOCK, voteTimer / 20, "�����ð� : " + voteTimer / 20);
		Util.Stack(gui, guiSize - 1, Material.MAGENTA_GLAZED_TERRACOTTA, 1, "��fSKIP", "��7��ǥ");
	}

	public void openGUI(Player p) {
		p.openInventory(this.gui);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		ItemStack its = e.getCurrentItem();
		if (guiName == e.getView().getTitle() && its != null) {
			if (its.getItemMeta().hasLore() && its.getItemMeta().getLore().get(0).equals("��7��ǥ")) {
				putVoter(e.getWhoClicked().getName(), its.getItemMeta().getDisplayName());
			}
			e.setCancelled(true);
		}

	}

	Material[] clothes = { Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE };

	public void setSeats() { // ��ǥ�� ���۵� �� �ڸ� ����
		SEATS = LocManager.getLoc("SEATS");
		if (DATALIST.size() > SEATS.size()) {
			Util.debugMessage("�÷��̾� ������ �ڸ��� �����ϴ�.");
		}
		for (int idx = 0; idx < DATALIST.size(); idx++) {
			Player currentPlayer = Bukkit.getPlayer(DATALIST.get(idx).getName());
			currentPlayer.teleport(SEATS.get(idx));// �÷��̾ �� �ڸ��� �̵�
			Vector dir = LocManager.getLoc("Center").get(0).clone().subtract(currentPlayer.getEyeLocation()).toVector();
			Location loc = currentPlayer.getLocation().setDirection(dir);
			currentPlayer.teleport(loc);// �÷��̾���� �ٲٱ�

			if (!DATALIST.get(idx).isAlive()) {// �÷��̾ ���ɻ����϶�
				ArmorStand arm = (ArmorStand) currentPlayer.getWorld().spawnEntity(SEATS.get(idx),
						EntityType.ARMOR_STAND);
				arm.setInvulnerable(true);
				arm.setCustomName("��7" + currentPlayer.getName());
				arm.addScoreboardTag("forVote");
				armorstandList.add(arm);
				EntityEquipment armeq = arm.getEquipment();

				armeq.setHelmet(new ItemStack(Material.SKELETON_SKULL));
				for (int idx1 = 0; idx1 < 3; idx1++) // ��������
				{
					ItemStack stack = new ItemStack(clothes[idx1]);
					LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
					meta.setColor(DATALIST.get(idx).getColor().getDyeColor().getColor());
					stack.setItemMeta(meta);
					switch (idx1) {
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
	 * ��ǥ ���� ����
	 */
	private void putVoter(String voter, String voted) {
		if (voteTimer <= 0)
			return;
		if (VOTERS.contains(voter))
			return;

		if (voted == "��fSKIP") {
			voteMap.get(voted).add(voter);
			remainedVoter--;
			VOTERS.add(voter);
		} else if (PlayerData.getPlayerData(voter).isAlive()) {
			voteMap.get(voted).add(voter);
			remainedVoter--;
			VOTERS.add(voter);
		} else
			Util.debugMessage("��������� ��ǥ�� �õ��߽��ϴ�.");
		if (remainedVoter == SURVIVERS.size()) {
			voteover();
		}
	}

	/*
	 * ��ǥ ���� ����
	 */
	private void timeover() { // �ð� ����
		voteover();
	}

	public static String top = null;

	private void voteover() {// ���� ������
		int maximum = -1;
		int current;
		boolean tied = false;

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

	}

	/*
	 * �ð�����
	 */
	public int getRemainedTick() {
		return voteTimer;
	}

	@Override
	public void run() {
		voteTimer--;
		Util.Stack(gui, guiSize - 2, Material.CLOCK, voteTimer / 20, "�����ð� : " + voteTimer / 20);
		switch (voteTimer) {// ��ǥ ���� �� �̺�Ʈ ����
		case 0:
			timeover();
		case -25:// ��ǥ ���� ����.
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