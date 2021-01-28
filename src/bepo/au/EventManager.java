package bepo.au;

import bepo.au.manager.ConfigScreenManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import bepo.au.manager.CommandManager;
import bepo.au.manager.LocManager;
import bepo.au.utils.SettingUtil;
import io.github.thatkawaiisam.assemble.AssembleBoard;
import io.github.thatkawaiisam.assemble.events.AssembleBoardCreatedEvent;

public class EventManager implements Listener{
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if(!p.isOp()) return;
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if(LocManager.getLoc("StartButton").contains(b.getLocation())) {
				CommandManager.start(p);
			}
		}
	}
	
	@EventHandler
	public void onInteractAt(PlayerInteractAtEntityEvent event) {
		
		Player p = event.getPlayer();

		if(!p.isOp()) return;
		
		if(event.getRightClicked() instanceof ArmorStand) {
			if(Main.gt == null) {
				ArmorStand as = (ArmorStand) event.getRightClicked();
				if(as.getScoreboardTags().contains("clickable")) {
					if(as.getScoreboardTags().contains("prev")) {
						SettingUtil.setSetting(as.getWorld(), false);
					} else if(as.getScoreboardTags().contains("next")) {
						SettingUtil.setSetting(as.getWorld(), true);
					}
				}
			}
			
			if(p.getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player && Main.gt == null) event.setCancelled(true);
		else if(event.getEntity() instanceof ItemFrame) event.setCancelled(true);
	}
	
	@EventHandler
	public void onHangingBreak(HangingBreakEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onAssembleCreate(AssembleBoardCreatedEvent event) {
		
		
		AssembleBoard ab = event.getBoard();
		String name = "nt_" + ab.getUuid().toString().substring(0, 6);
		Team t = ab.getScoreboard().getTeam(name);
		if(ab.getScoreboard().getTeam(name) == null) {
			t = ab.getScoreboard().registerNewTeam(name);
		}
		t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		for(Player ap : Bukkit.getOnlinePlayers()) {
			t.addEntry(ap.getName());
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		ConfigScreenManager.onClick(event);
	}


}
