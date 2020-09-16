package bepo.au.function;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import bepo.au.utils.Util;

public class ItemList {
	
	public final static ItemStack I_SWORD;
	public final static ItemStack I_SABOTAGE_CRIT;
	public final static ItemStack I_SABOTAGE_DOOR;
	
	public final static ItemStack I_VENT_NEXT;
	public final static ItemStack I_VENT_CONFIRM;
	public final static ItemStack I_VENT_PREV;
	
	public final static ItemStack VOTE_PAPER;
	
	public final static HashMap<Integer, ItemStack> I_SET;
	
	static {
		I_SWORD = Util.createItem(Material.DIAMOND_SWORD, 1, "��c���������� ��", Arrays.asList("��7���ٴϴ� �� ��Ű�� �ʵ��� �����Ͻʽÿ�."));
		Damageable im = (Damageable) I_SWORD.getItemMeta();
		im.setDamage(I_SWORD.getType().getMaxDurability());
		I_SWORD.setItemMeta((ItemMeta) im);
		
		I_SABOTAGE_CRIT = Util.createItem(Material.IRON_PICKAXE, 1, "��cġ���� �纸Ÿ�� ����", Arrays.asList("��7�տ� ��� ���� ������ ġ������ �纸Ÿ���� ������ �ٲ� �� �ֽ��ϴ�.", "��7���ٳ൵ ��Ű�� �ʽ��ϴ�."));
		Damageable im2 = (Damageable) I_SABOTAGE_CRIT.getItemMeta();
		im2.setDamage(I_SABOTAGE_CRIT.getType().getMaxDurability());
		I_SABOTAGE_CRIT.setItemMeta((ItemMeta) im2);
		
		I_SABOTAGE_DOOR = Util.createItem(Material.IRON_HOE, 1, "��c�� �纸Ÿ�� ����", Arrays.asList("��7�տ� ��� ���� ������ �� �纸Ÿ���� �ߵ� ��ġ�� �ٲ� �� �ֽ��ϴ�.", "��7���ٳ൵ ��Ű�� �ʽ��ϴ�."));
		Damageable im3 = (Damageable) I_SABOTAGE_DOOR.getItemMeta();
		im3.setDamage(I_SABOTAGE_DOOR.getType().getMaxDurability());
		I_SABOTAGE_DOOR.setItemMeta((ItemMeta) im3);
		
		VOTE_PAPER = Util.createItem(Material.PAPER, 1, "��a��ǥâ ���� (��Ŭ��)", Arrays.asList("��7�տ� ��� ��Ŭ���ϸ� ��ǥâ�� �����ϴ�."));
		
		I_SET = new HashMap<Integer, ItemStack>();
		I_SET.put(1, I_SWORD.clone());
		I_SET.put(2, I_SABOTAGE_CRIT.clone());
		I_SET.put(3, I_SABOTAGE_DOOR.clone());
		
		
		I_VENT_NEXT = Util.createItem(Material.RED_STAINED_GLASS, 1, "��c���� ��ġ", Arrays.asList(""));
		I_VENT_CONFIRM = Util.createItem(Material.YELLOW_STAINED_GLASS, 1, "��e�� ������ �̵�", Arrays.asList("��7��� ��Ŭ�� �� �� ��Ʈ�� ���ɴϴ�."));
		I_VENT_PREV = Util.createItem(Material.BLUE_STAINED_GLASS, 1, "��b���� ��ġ", Arrays.asList(""));
		
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, ItemStack> getImposterSet(){
		return (HashMap<Integer, ItemStack>) I_SET.clone();
		
	}

}
