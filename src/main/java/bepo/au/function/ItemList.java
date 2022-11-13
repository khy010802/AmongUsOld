package bepo.au.function;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import bepo.au.utils.Util;

@SuppressWarnings("deprecation")
public class ItemList {
	
	public final static ItemStack I_SWORD;
	public final static ItemStack I_SABOTAGE_CRIT;
	public final static ItemStack I_SABOTAGE_DOOR;

	public final static ItemStack I_VENT_NEXT;
	public final static ItemStack I_VENT_CONFIRM;
	public final static ItemStack I_VENT_PREV;
	
	public final static ItemStack I_SABOTAGE_GUI;
	
	public final static ItemStack VOTE_PAPER;
	
	public final static ItemStack MINIMAP;
	
	public final static ItemStack CCTV_EXIT;
	//public final static ItemStack REPORT_BOOK;
	
	public final static HashMap<Integer, ItemStack> I_SET;
	
	static {
		I_SWORD = Util.createItem(Material.DIAMOND_SWORD, 1, "§c임포스터의 검", Arrays.asList("§7들고다니는 걸 들키지 않도록 주의하십시오."));
		Damageable im = (Damageable) I_SWORD.getItemMeta();
		im.setDamage(I_SWORD.getType().getMaxDurability());
		I_SWORD.setItemMeta((ItemMeta) im);
		
		I_SABOTAGE_CRIT = Util.createItem(Material.IRON_PICKAXE, 1, "§c치명적 사보타지 선택", Arrays.asList("§7손에 들면 현재 선택한 치명적인 사보타지의 종류를 바꿀 수 있습니다.", "§7들고다녀도 들키지 않습니다.", "§7손 바꾸기(F) 키로 선택한 사보타지를 발동시킬 수 있습니다."));
		Damageable im2 = (Damageable) I_SABOTAGE_CRIT.getItemMeta();
		im2.setDamage(I_SABOTAGE_CRIT.getType().getMaxDurability());
		I_SABOTAGE_CRIT.setItemMeta((ItemMeta) im2);
		
		I_SABOTAGE_DOOR = Util.createItem(Material.IRON_HOE, 1, "§c문 사보타지 선택", Arrays.asList("§7손에 들면 현재 선택한 문 사보타지의 발동 위치를 바꿀 수 있습니다.", "§7들고다녀도 들키지 않습니다.", "§7손 바꾸기(F) 키로 선택한 사보타지를 발동시킬 수 있습니다."));
		Damageable im3 = (Damageable) I_SABOTAGE_DOOR.getItemMeta();
		im3.setDamage(I_SABOTAGE_DOOR.getType().getMaxDurability());
		I_SABOTAGE_DOOR.setItemMeta((ItemMeta) im3);
		
		I_SABOTAGE_GUI = Util.createItem(Material.ENDER_EYE, 1, "§c사보타지 선택창 열기", Arrays.asList("§7클릭하면 선택 페이지를 엽니다.", "§c움직이면서 사용할 수 없으므로, 아무도 없는 곳에서 사용하세요."));
		
		VOTE_PAPER = Util.createItem(Material.BOOK, 1, "§a투표창 열기 (우클릭)", Arrays.asList("§7손에 들고 우클릭하면 투표창이 열립니다."));
		
		I_SET = new HashMap<Integer, ItemStack>();
		I_SET.put(1, I_SWORD.clone());
		I_SET.put(3, I_SABOTAGE_DOOR.clone());
		I_SET.put(5, I_SABOTAGE_CRIT.clone());
		I_SET.put(22, I_SABOTAGE_GUI.clone());
		
		I_VENT_NEXT = Util.createItem(Material.RED_STAINED_GLASS, 1, "§c다음 위치", Arrays.asList(""));
		I_VENT_CONFIRM = Util.createItem(Material.YELLOW_STAINED_GLASS, 1, "§e이 곳으로 이동", Arrays.asList("§7들고 우클릭 시 이 벤트로 나옵니다."));
		I_VENT_PREV = Util.createItem(Material.BLUE_STAINED_GLASS, 1, "§b이전 위치", Arrays.asList(""));
		
		CCTV_EXIT = Util.createItem(Material.IRON_DOOR, 1, "§cCCTV 그만 보기", Arrays.asList("§7CCTV를 그만봅니다."));
		
		MINIMAP = Util.createItem(Material.FILLED_MAP, 1, "", Arrays.asList(""));
		MapMeta mm = (MapMeta) MINIMAP.getItemMeta();
		mm.setMapId(0);
		MINIMAP.setItemMeta(mm);
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, ItemStack> getImposterSet(){
		return (HashMap<Integer, ItemStack>) I_SET.clone();
		
	}

}
