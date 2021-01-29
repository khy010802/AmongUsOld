package bepo.au.manager;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigScreenManager {
    public static String guiName = "Config";

    private final static Inventory gui = Bukkit.createInventory(null, ((Main.SETTING.values().length+1)/9+1)*9, guiName);;

    private final static Main.SETTING[] arr = Main.SETTING.values();


    private static void stack(int slot, Main.SETTING setting){
        if (setting==Main.SETTING.GAMEMODE)
            Util.Stack(gui,slot, Material.PAPER,1,arr[0].getName(), Arrays.asList(arr[0].get().toString(), ChatColor.RED + "�κ� ���� �����ϴ� ���� �����մϴ�."));
        else {

            if(setting.getType() == Double.class)
                Util.Stack(gui,slot, Material.PAPER,1,arr[slot].getName(),"��e" + Math.round(arr[slot].getAsDouble()*10)/10.0+"", "��7��o��Ŭ��/��Ŭ�� | 1 ����/����");
            else if(setting.getType() == Integer.class)
                Util.Stack(gui, slot, Material.PAPER, 1, arr[slot].getName(), Arrays.asList("��e" + arr[slot].getAsInteger() + "", "��7��o��Ŭ��/��Ŭ�� | 1 ����/����", "��7��oShift+��Ŭ��/��Ŭ�� | 10 ����/����"));
            else
                Util.Stack(gui,slot, Material.PAPER,1,arr[slot].getName(), arr[slot].get().toString());
        }
    }

    public static int reset(){
        int i;
        for (i = 0 ; i<Main.SETTING.values().length;i++){
            stack(i,arr[i]);
        }
        return i;
    }

    public static void openGUI(Player p){
        int slot = reset();
        Util.Stack(gui, slot, Material.RED_WOOL, 1, "��c��ü �ʱ�ȭ", "��7Ŭ�� �� ��� ������ �ʱ�ȭ");
        p.openInventory(gui);
    }

    private static void  modifyConfig(Main.SETTING setting, boolean increase, boolean increaseX10){
        if(setting.getType().isAssignableFrom(GameTimer.GameType.class)) setting.setSetting(setting.getAsGameType().getNextType());
        else if (setting.getType().isAssignableFrom(Boolean.class)) setting.setSetting(!setting.getAsBoolean());
        else if (setting.getType().isAssignableFrom(Double.class)) setting.setSetting(setting.getAsDouble()+ (increase ? 0.1 : -0.1));
        else if (setting.getType().isAssignableFrom(Integer.class)) {
            int constant = 1;
            if (increaseX10) constant*=10;
            setting.setSetting(setting.getAsInteger()+ (increase ? constant : -constant));
        }
    }

    public static void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().contains(guiName))
            return;
        if(e.getCurrentItem() != null && e.getClick() != ClickType.DOUBLE_CLICK){
            switch(e.getCurrentItem().getType()){
                case PAPER:
                    Main.SETTING setting;
                    int i = e.getRawSlot();
                    try {
                        setting = arr[i];
                    } catch (Exception exception) {
                        e.setCancelled(true);
                        return;
                    }
                    if(e.getClick() != ClickType.MIDDLE) {
                        modifyConfig(setting, e.getClick().isRightClick(), e.isShiftClick());
                    }
                    stack(i, arr[i]);
                case RED_WOOL:
                    Main.SETTING.GAMEMODE.getAsGameType().getGameTicker().config(1);
                    reset();
                    break;
            }
        }
        e.setCancelled(true);
    }
}
