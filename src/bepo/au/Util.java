package bepo.au;

import java.util.ArrayList;								
import java.util.Arrays;								
import java.util.Collections;								
import java.util.List;								
import java.util.Random;								
								
import org.bukkit.Bukkit;								
import org.bukkit.Color;								
import org.bukkit.Material;								
import org.bukkit.entity.Player;								
import org.bukkit.event.Listener;								
import org.bukkit.inventory.Inventory;								
import org.bukkit.inventory.InventoryView;								
import org.bukkit.inventory.ItemFlag;								
import org.bukkit.inventory.ItemStack;								
import org.bukkit.inventory.meta.ItemMeta;								
import org.bukkit.inventory.meta.PotionMeta;								
								
								
								
								
public class Util implements Listener{								
	static boolean debug = true;							
	static Random ran = new Random();							
	public static int random(int min, int max) { //min ~ max 의 정수를 출력							
								
		return min+ran.nextInt(max-min+1);						
	}							
	public static int[] difrandom(int min, int max, int length) {							
		if (length>max-min+1) return null;						
		ArrayList<Integer> numbers = new ArrayList<Integer>();						
		for (int num = min; num<=max ; num++) numbers.add(num);						
		Collections.shuffle(numbers);						
		return Arrays.stream(numbers.toArray(new Integer[numbers.size()])).mapToInt(Integer::intValue).toArray();						
								
	}							
								
	public static void fillAround(Inventory inv, int slot, Material item){//gui 관련							
		int Slot;						
		for (int i=-1;i<2;i++) {						
			for(int j=-1;j<2;j++) {					
			if (i==0&&j==0) continue;					
			Slot=getCoordinate(slot,i,j);					
			if(Slot==-1) continue;					
			inv.setItem(Slot, new ItemStack(item));					
				}				
			}					
		//						
	}							
								
	public static int getCoordinate(int slot, int x, int y) {							
		int idx=slot+x+9*y;						
		if ((slot+x)/9!=slot/9) return -1;						
		if (idx >53 || idx<0) return -1;						
		return idx;						
	}							
								
	public static void debugMessage(String message) {  //디버그메세지							
		if (debug == true) Bukkit.broadcastMessage("[Debug]"+message);						
	}							
	public static boolean isDebug() {							
		return debug;						
	}							
	public static void setDebug(boolean debug) {							
		Util.debug = debug;						
	}							
								
	public static ItemStack createItem(Material mat, int amount, String name, List<String> lore) {							
		ItemStack is = new ItemStack(mat, amount);						
		ItemMeta ism = is.getItemMeta();						
		ism.setDisplayName(name);						
		ism.setLore(lore);						
		is.setItemMeta(ism);						
		return is;						
	}							
	public static ItemStack createPotion(int amount,Color color, String name, List<String> lore) {							
		ItemStack is = createItem(Material.POTION,amount,name,lore);						
								
		PotionMeta isp= (PotionMeta) is.getItemMeta();						
		isp.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);						
		isp.setColor(color);						
		is.setItemMeta(isp);						
		return is;						
	}							
								
////////////////////////////								
	public static void giveItem(Player p, Material mat, int amount, String name, List<String> lore) {							
		p.getInventory().addItem(createItem(mat, amount, name, lore));						
		p.updateInventory();						
	}							
	public static void StackPotion(Inventory inv, int slot, Color color, int amount, String name, List<String> lore) {							
		inv.setItem(slot, createPotion(amount,color, name, lore));						
	}							
	public static void StackPotion(Inventory inv, int slot, Color color, int amount, String name) {							
		inv.setItem(slot, createPotion(amount, color, name, null));						
	}							
								
	public static void Stack(Inventory inv, int slot, Material mat, int amount, String name, List<String> lore) {							
		inv.setItem(slot, createItem(mat, amount, name, lore));						
	}
	public static void Stack(Inventory inv, int slot, Material mat, int amount, String name, String lore) {							
		inv.setItem(slot, createItem(mat, amount, name, Arrays.asList(lore)));						
	}
	public static void Stack(Inventory inv, int slot, Material mat, int amount, String name) {							
		inv.setItem(slot, createItem(mat, amount, name, null));						
	}
								
//////////////////////////////								
								
	public static Inventory setTitle(Player p,Inventory gui, String title) {							
		ItemStack[] temp =	gui.getContents();					
		gui = Bukkit.createInventory(p, 54, title);						
		gui.setContents(temp);						
		InventoryView guiView = (InventoryView) gui;						
		if(p.getOpenInventory().getTitle().equals(guiView.getTitle())) p.openInventory(gui);						
		return gui;						
	}							
}								
								