package bepo.au.function;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import bepo.au.base.PlayerData;
import bepo.au.base.TimerBase;
import bepo.au.utils.Util;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Cctv implements Listener {
	
	ArrayList<Cctv> cctvList;
	
	CCTVTimer Timer = new CCTVTimer();
	Player P;
	boolean A;
	Location L0;
	Location L1;
	Location L2;
	Location L3;
	Location LS;
	int slot;
	ArmorStand arm;
	
	public void cctv(Player p) {
		
		P = p;
		A = false;
		p.sendMessage("CCTV ON");
		L0 = new Location(P.getLocation().getWorld(), 17.5, 6, -3.5, -110, 10); //X , Y, Z, YAW, PITCH 값
		L1 = new Location(P.getLocation().getWorld(), 49.5, 6, -11.5, -170, 10);
		L2 = new Location(P.getLocation().getWorld(), -6.5, 6, -30.5, -100, 20);
		L3 = new Location(P.getLocation().getWorld(), -50.5, 6, -25.5, -85, 25);
		LS = p.getLocation();
		if(A == false) {
			p.sendMessage("" + LS.getX() + LS.getZ()); //debug
			A = true;
			p.setFlying(true);
			p.addPotionEffect (new PotionEffect (PotionEffectType.INVISIBILITY, 9999, 1, true));
			
			//아머스탠드 생성
			arm = (ArmorStand) P.getWorld().spawnEntity(LS, EntityType.ARMOR_STAND);
			arm.setInvulnerable(true);
			arm.setCustomName("§7"+P.getName());
			EntityEquipment armeq = arm.getEquipment();
			
			ItemStack HEAD=Util.getSkull(P.getName());
			ItemMeta Smeta=HEAD.getItemMeta();
			
			armeq.setHelmet(HEAD);
			
			for(int idx1 = 0 ; idx1 < 3 ; idx1++)
			{
				Material[] clothes = {Material.LEATHER_BOOTS,Material.LEATHER_LEGGINGS,Material.LEATHER_CHESTPLATE};
				ItemStack stack = new ItemStack(clothes[idx1]);
				LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
				meta.setColor(PlayerData.getPlayerData(p.getName()).getColor().getDyeColor().getColor());
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
			//타이머 시작
			Timer.StartTimer(-1, true, 1);
		}
	}
	
	@EventHandler
	public void onPlayerItemHoldEvent(PlayerItemHeldEvent e) {
		slot = e.getNewSlot();
		if(A) {
			P.playSound(P.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
		}
	}
	
	public final class CCTVTimer extends TimerBase {

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void EventRunningTimer(int count) {
			// TODO Auto-generated method stub
			//핫바 슬롯 위치에 따라 플레이어를 이동시킴
			if (slot == 0) {
				P.setFlying(true);
				P.teleport(L0);
			}
			if (slot == 1) {
				P.setFlying(true);
				P.teleport(L1);
			}
			if (slot == 2) {
				P.setFlying(true);
				P.teleport(L2);
			}
			if (slot == 3) {
				P.setFlying(true);
				P.teleport(L3);
			}
			if (slot > 3) {
				Timer.EndTimer();
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			A = false;
			P.teleport(LS);
			P.setFlying(false);
			P.removePotionEffect(PotionEffectType.INVISIBILITY);
			arm.remove();
		}
		
	}
	
	
	
}
