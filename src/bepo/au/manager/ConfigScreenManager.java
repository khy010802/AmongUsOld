package bepo.au.manager;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.utils.Util;
import com.mysql.fabric.xmlrpc.base.Array;
import net.minecraft.server.v1_16_R3.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigScreenManager {
    public static String guiName = "Config";

    private static Inventory gui = Bukkit.createInventory(null, (Main.SETTING.values().length/9+1)*9, guiName);;

    private static Main.SETTING[] arr = Main.SETTING.values();;


    private static void stack(int slot,Main.SETTING setting){
        if (setting==Main.SETTING.GAMEMODE) Util.Stack(gui,slot, Material.PAPER,1,arr[0].getName(), Arrays.asList(arr[0].get().toString(), ChatColor.RED + "로비를 통해 변경하는 것을 권장합니다."));
        else {
            if(setting.getType()==Double.class) Util.Stack(gui,slot, Material.PAPER,1,arr[slot].getName(),Math.round(arr[slot].getAsDouble()*10)/10.0+"");
            else Util.Stack(gui,slot, Material.PAPER,1,arr[slot].getName(),arr[slot].get().toString());
        }
    }
    public static void openGUI(Player p){
        for (int i = 0 ; i<Main.SETTING.values().length;i++){
            stack(i,arr[i]);
        }
        p.openInventory(gui);
    }

    private static void  modifyConfig(Main.SETTING setting, boolean increase, boolean increseX10){
        if(setting.getType().isAssignableFrom(GameTimer.GameType.class)) setting.setSetting(setting.getAsGameType().getNextType());
        else if (setting.getType().isAssignableFrom(Boolean.class)) setting.setSetting(!setting.getAsBoolean());
        else if (setting.getType().isAssignableFrom(Double.class)) setting.setSetting(setting.getAsDouble()+ (increase ? 0.1 : -0.1));
        else if (setting.getType().isAssignableFrom(Integer.class)) {
            int constant = 1;
            if (increseX10) constant*=10;
            setting.setSetting(setting.getAsInteger()+ (increase ? constant : -constant));
        }

    }

    public static void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().contains(guiName))
            return;
        if (e.getCurrentItem()==null||!e.getCurrentItem().getType().equals(Material.PAPER)|| e.getClick()==ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
        }
        else{
            Main.SETTING setting;
            int i = e.getRawSlot();
            try{
            setting = arr[e.getRawSlot()];
            }catch (Exception exception){
                e.setCancelled(true);
                return;
            }
            if (e.getClick().isLeftClick()){
                modifyConfig(setting,false,e.isShiftClick());
                stack(i,arr[i]);
            }else if(e.getClick().isRightClick()){
                modifyConfig(setting,true,e.isShiftClick());
                stack(i,arr[i]);
            }
            e.setCancelled(true);
        }
    }
}
