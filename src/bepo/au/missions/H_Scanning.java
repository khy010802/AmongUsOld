package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import bepo.au.Util;
import bepo.au.base.Mission;
import bepo.au.base.TimerBase;

public class H_Scanning extends Mission {
	
	public H_Scanning(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}
	
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 54, "Scanning");
	}
	
	public void onStart(Player p, int code) {
		scanning(p);
	}
	
	public void onClear(Player p, int code) {
		generalClear(p, code);
	}
	
	public void onStop(Player p, int code) {
		
	}
	
	public class Timer extends TimerBase {
		double radius = 0.8;

		@Override
		public void EventStartTimer() { // 타이머
			Util.debugMessage(" 타이머 시작됨");
		}

		@Override
		public void EventRunningTimer(int count) {
			Util.debugMessage(" 0.1초 경과");
			int ccount = count / 20;
			int slot = 9 - ccount;
			Util.debugMessage(slot + "슬롯 설정됨");
			Player p = getPlayer();
			if (p.getOpenInventory().getTitle().split(" ")[0].equals("Scanning")) { // gui 열고있는지 확인

				// Location location = p.getLocation().add(0, 1, 0);
				for (double x = 0 - radius - 0.2; x <= (radius + 0.2); x += 0.05)
					for (double z = 0 - radius - 0.2; z <= (radius + 0.2); z += 0.05) {
						if ((x * x + z * z) <= radius && (x * x + z * z) >= (radius - 0.1)) {
							double y = (count >= 100 ? 200 - count : count); // count는 200 -> 0 ||| y는 0->-100 후 100->0감
							// Util.debugMessage("count : "+count);
							y = ((y - 50) * 0.01);
							// Util.debugMessage("y : "+y);
							Location location = p.getLocation();
							location.add(x, 1 + y, z);
							p.getWorld().spawnParticle(Particle.COMPOSTER, location, 1, 0.0, 0.0, 0.0, 1.0, null, true);
						}

					}
				// p.getWorld().spawnParticle(Particle.COMPOSTER, location, 15, 0.7, 1.0, 0.7,
				// 1.0,null,true);
				if (count % 2 == 0) {
					Util.debugMessage("gui 인식됨");
					ItemStack[] temp = gui.get(0).getContents();
					gui.set(0, Bukkit.createInventory(p, 9, "Scanning " + ccount));
					gui.get(0).setContents(temp);
					if (0 <= slot && slot <= 8)
						Util.Stack(gui.get(0), slot, Material.GREEN_STAINED_GLASS_PANE, 1, "§f" + (10 - ccount) + "0%",
								"§4클릭불가");
					p.openInventory(gui.get(0));
				}
			} else { // 아니면 캔슬
				Util.debugMessage("인벤토리 닫음 확인");
				stopScan();
			}
			if (p.getOpenInventory().getTitle().split(" ")[0].equals("Scanning") && count == 0) { // 0초 되면 클리어
				ItemStack[] temp = gui.get(0).getContents();
				gui.set(0, Bukkit.createInventory(p, 9, "Scanning"));
				p.openInventory(gui.get(0));
				gui.get(0).setContents(temp);

				if (p.getOpenInventory().getTitle().equals("Scanning")) {
					status = false;
					p.closeInventory();
					Util.debugMessage("클리어!");
				}
			}
		}

		@Override
		public void EventEndTimer() {
			Util.debugMessage("타이머 종료됨");
		}

	}

	Timer timer = new Timer();
	static boolean status = false;// 한 번에 한 플레이어만 가능,

	///////////////////////////////////////////////////
	public void scanning(Player p) {
		if (!status) {
			p.openInventory(gui.get(0));
			status = true;
			timer.StartTimer(200, true, 1);
		} else {
			p.sendMessage("이미 다른 플레이어가 사용중입니다.");
		}
	}

	public void stopScan() {
		status = false;
		timer.StopTimer();
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		
		// Inventory inv = e.getClickedInventory();
		// Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() != null) {
			e.setCancelled(true);
		}

	}
}
