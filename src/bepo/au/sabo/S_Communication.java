package bepo.au.sabo;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

import bepo.au.base.Sabotage;
import bepo.au.utils.Util;

import java.util.Random;

public class S_Communication extends Sabotage {
	
	Random random = new Random();
	private final int maxslot = 27;
	private final int maxNum = 10; // ���ļ��� �ִ������. 1 : ���� | 2 : �ѹ�Ŭ���ϸ�� | 3 : �ִ� �ι�Ŭ�� | 5~6 : ���� | 10 : �غ��ϱ� �� ���ϱ���
								   // 20+ �����ɸ���
	private int[] answerStatus = new int[5];
	private int[] currentStatus = new int[5];
	private Material[] Color = { Material.LIME_WOOL, Material.ORANGE_WOOL, Material.WHITE_WOOL }; // �� �ε����� ������� �Ÿ���.
																									// �Ÿ��� maxNum�� ������
																									// ����. (����)������ 1�̰�
																									// ���� 2 �̶�� 1��° �ε���.
	
	public static boolean Activated = false;
	
	public S_Communication(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc, SaboType.COMM, 0);
	}
	
	public void onAssigned(Player p) {
		initialize_s_communications(p);
	}
	
	public void onStart(Player p, int i) {
		s_communicationOpen(p);
	}
	
	public void onStop(Player p, int i) {

	}
	
	public void onClear(Player p, int i) {
		Activated = false;
	}
	
	
	/*
	 * ��ɾ� ���� �� �����, GUI ���� �õ�.
	 */
	public void s_communicationOpen(Player p) {
		
		if (Activated) {
			p.openInventory(gui.get(0));
		} else {
			Util.debugMessage("��� �纸Ÿ�ִ� ���� ���۵��� �ʾҽ��ϴ�");
		}
	}

	/*
	 * ��ɾ� ���� �� �����. �纸Ÿ�� ����.
	 */
	public void s_communicationsStart() {
		if (Activated) {
			Util.debugMessage("��� �纸Ÿ�ִ� �̹� ���۵Ǿ����ϴ�.");
		} else {
			
			Util.debugMessage("��� �纸Ÿ�� �����");
		}
	}

	/*
	 * �ʱ�ȭ ; GUI�� ����.
	 */
	private void initialize_s_communications(Player p) {
		while (true) {
			for (int i = 0; i < 5; i++) {
				answerStatus[i] = random.nextInt(maxNum); // ���� ���ļ� �����, ���ļ��� 0~4��.
				currentStatus[i] = random.nextInt(maxNum); // ���� ���ļ� ����
			}
			if (!Arrays.equals(answerStatus, currentStatus))
				break; // ����� ������ ���� ������ �ݺ�.
		}

		Activated = true;
		uploadInventory(p, maxslot, "Communication");
		gui.get(0).setMaxStackSize(1);
		setGUI(); // GUI �����

	}
	/*
	 * GUI�� �����.
	 */

	private void setGUI() {
		List<String> lore = Arrays.asList("��7������ Ŭ���� ��� ������ �ʷϻ����� ���弼��.");
		for (int slot = 0; slot < maxslot; slot++) {
			int x = slot % 9, y = slot / 9;

			if (10 < slot && slot < 16) {
				Util.debugMessage(slot + "�� ���� " + currentStatus[x - 2] + "�� " + answerStatus[x - 2] + "�� ��");
				int dif = difference(currentStatus[x - 2], answerStatus[x - 2], maxNum);
				Util.debugMessage("�Ÿ� : " + dif);
				if (dif >= Color.length)
					dif = Color.length - 1; // ������ ���� ������
				Util.Stack(gui.get(0), slot, Color[dif], 1, "��f" + currentStatus[x - 2],
						Arrays.asList("��7��Ŭ�� : ����", "��7��Ŭ�� : ����"));
			} else if (x == 0 && y == 1) {
				Util.Stack(gui.get(0), slot, Material.BOOK, 1, "��f���� ����", lore);
			} else {
				Util.Stack(gui.get(0), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			}

		}
	}

	/*
	 * �� �������� ��(�Ÿ�) ���ϱ�
	 */
	private int difference(int a, int b, int max) {
		Util.debugMessage(a + "��" + b + "�� ���մϴ�.");
		int temp = Math.abs(a - b);
		Util.debugMessage("�� : " + temp);
		if (temp > max / 2) {
			return max - temp;
		}

		return temp;
	}

	/*
	 * ������ ���
	 */
	private void toggleStatus(Player p, int idx, boolean increase) {
		Util.debugMessage("��-------���-------��");
		Util.debugMessage(idx + "�� ���ļ� ���");
		if (increase) {
			if (++currentStatus[idx] > maxNum - 1)
				currentStatus[idx] = 0;
		} else if (--currentStatus[idx] < 0)
			currentStatus[idx] = maxNum - 1;
		int dif = difference(currentStatus[idx], answerStatus[idx], maxNum);
		if (dif >= Color.length)
			dif = Color.length - 1; // ������ ���� ������ ���� �ڿ� �ִ� ���� ��.
		Util.Stack(gui.get(0), 11 + idx, Color[dif], 1, "��f" + currentStatus[idx], Arrays.asList("��7��Ŭ�� : ����", "��7��Ŭ�� : ����"));
		p.updateInventory();
		if (dif == 0)
			check();
		Util.debugMessage("��------��� ��------��");
	}

	/*
	 * Ŭ���� Ȯ��
	 */
	private void check() {
		for (int i = 0; i < 5; i++) {
			int dif = difference(currentStatus[i], answerStatus[i], maxNum);
			Util.debugMessage("Ŭ���� Ȯ�� " + Integer.toString(dif));
			if (dif != 0)
				return;
		}
		Util.debugMessage("�纸Ÿ�� Ŭ����");
		Activated = false;
		Sabotage.saboClear(0);
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		Player p = (Player) e.getWhoClicked();
			Util.debugMessage("Ŭ�� �νĵ�");
			int slot = e.getRawSlot();
			ItemStack itemstack = e.getCurrentItem();

			// Inventory gui = e.getClickedInventory();
			// Player p = (Player) e.getWhoClicked();

			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // ����Ŭ��,����ƮŬ�� ����
				Util.debugMessage("���� Ŭ�� �Ұ�");
				e.setCancelled(true);
			}
			if (itemstack != null) {
				if (Arrays.asList(Color).contains(itemstack.getType())) {
					if (e.isLeftClick()) {
						Util.debugMessage("��Ŭ�� �νĵ�");
						toggleStatus(p, slot % 9 - 2, true);
						e.setCancelled(true);
					} else {
						Util.debugMessage("��Ŭ�� �νĵ�");
						toggleStatus(p, slot % 9 - 2, false);
						e.setCancelled(true);
					}

				} else {
					Util.debugMessage("Ŭ�� �Ұ�");
					e.setCancelled(true);
				}
			}
	}
	/*
	 * @EventHandler public void onDrag(InventoryEvent e) { if
	 * (e.getView().getTitle().equals("EmptyChute") && !e.getRawSlots().isEmpty()) {
	 * ; }
	 * 
	 * }
	 */

}
