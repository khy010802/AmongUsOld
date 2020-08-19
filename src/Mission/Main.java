package Mission;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import bepo.au.missions.ActivatingReactor;
import bepo.au.missions.ActivatingShield;
import bepo.au.missions.DistributePower;
import bepo.au.missions.EmptyGarbage;
import bepo.au.missions.Gas;
import bepo.au.missions.OpenManifold;
import bepo.au.missions.Shooting;
import bepo.au.sabo.Sabo_Oxygen;

public class Main extends JavaPlugin implements Listener {
	


	public static Plugin instance;
	ActivatingReactor activateReactor = new ActivatingReactor();
	ActivatingShield activateShield = new ActivatingShield();
	Sabo_Oxygen sabo_Oxygen = new Sabo_Oxygen();
	OpenManifold openManifold = new OpenManifold();
	EmptyGarbage emptyGarbage = new EmptyGarbage();
	Gas gas = new Gas();
	DistributePower distributePower = new DistributePower();
			
	
	
	@Override
	public void onEnable() {
		System.out.println("플러그인 활성화");
		getCommand("test").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(new Shooting(), this);
		Bukkit.getPluginManager().registerEvents(activateShield, this);
		Bukkit.getPluginManager().registerEvents(activateReactor, this);
		Bukkit.getPluginManager().registerEvents(sabo_Oxygen, this);
		Bukkit.getPluginManager().registerEvents(openManifold, this);
		Bukkit.getPluginManager().registerEvents(emptyGarbage, this);
		Bukkit.getPluginManager().registerEvents(gas, this);
		Bukkit.getPluginManager().registerEvents(distributePower, this);
		instance=this;
	}
	
	@Override
	public void onDisable() {
		System.out.println("플러그인 비활성화");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if(command.getName().toLowerCase().equalsIgnoreCase("test")) {
			
			if(args[0].equalsIgnoreCase("Shooting")) {
				Shooting c = new Shooting();
				c.shooting(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Shield")) {
				activateShield.activatingShield(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Oxygen")) {
				sabo_Oxygen.sabo_Oxygen(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Reactor")) {
				activateReactor.activatingReactor(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Manifold")) {
				openManifold.openManifold(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Garbage1")) {
				emptyGarbage.emptyGarbage1(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Garbage2")) {
				emptyGarbage.emptyGarbage2(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Gas1")) {
				gas.gas1(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Gas2")) {
				gas.gas2(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("Power")) {
				distributePower.distributePower(p);
				return true;
			}
		}
		return true;
	} 
	static Main main;
}
