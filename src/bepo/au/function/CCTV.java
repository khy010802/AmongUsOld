package bepo.au.function;

import java.util.HashSet;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import bepo.au.manager.LocManager;
import bepo.au.utils.Util;

public class CCTV {
	
	public static HashSet<Player> watchingCCTVset = new HashSet<Player>();//보고 있는 플레이어들

	public enum E_cctv{
		Security("보안실"),
		MedBay("의무실"),
		Admin("관리실"),
		Navi("항해실");
		
		E_cctv(String name) {
			this.name = name;
		}
		
		private String name;
		private Location loc;
		private LivingEntity entity;
		
		public String getName() {
			return this.name;
		}
		
		public void setEntity(LivingEntity livingentity){
			this.entity=livingentity;
		}

		public LivingEntity getEntity(){
			return this.entity;
		}
		public static LivingEntity getEntity(E_cctv cctv){
			return cctv.getEntity();
		}

		public Location getloc(){
			return this.loc;
		}
		public static Location getloc(E_cctv cctv){
			return cctv.getloc();
		}

		static{
			List<Location> loclist = LocManager.getLoc("CCTV");
			for (E_cctv cctv : E_cctv.values()){
				cctv.loc = loclist.get(cctv.ordinal());
			}
		}
	}
	//private static final String uuid = "f33b49c7-e6c3-4806-ba67-cc662df7f9b0";
	//String id = "[I;-214218297,-423409658,-1167602586,771226032]";
	final static String value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=";
	
	public static final void activateCCTV(World w) {
		ItemStack head = Util.getCustomTextureHead(value);

		for(E_cctv cctv : E_cctv.values()){
			Zombie zombie = (Zombie) w.spawnEntity(cctv.getloc(),EntityType.ZOMBIE);
			zombie.setInvulnerable(true);
			zombie.setGravity(false);
			zombie.setAI(false);
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0, true));
			zombie.setRemoveWhenFarAway(false);
			zombie.setSilent(true);
			zombie.setBaby();
			zombie.addScoreboardTag("au_reset");
			
			zombie.getEquipment().setHelmet(head);
			
			cctv.setEntity((LivingEntity)zombie);
		}
	}
	
	public static final void deactivateCCTV(World w) {
		for(E_cctv cctv : E_cctv.values()) {
			if(cctv.getEntity() != null) {
				Entity e = cctv.getEntity();
				if(e.isValid() && !e.isDead()) e.remove();
				cctv.setEntity(null);
			}
		}
	}
	

}
