package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import bepo.au.Main;
import bepo.au.Main.SETTING;
import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.base.VisualTask;
import bepo.au.utils.Util;

public class H_Scanning extends Mission {

	public H_Scanning(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}

	public void onAssigned(Player p) {
		status = false;
		assign(p);
		uploadInventory(p, 9, "Scanning");
	}

	public void onStart(Player p, int code) {

		Block b = p.getLocation().getBlock();
		Location loc = b.getLocation();
		loc.setY(locs.get(0).getY());

		if (locs.contains(loc))
			scanning(p);
		else
			p.sendMessage(Main.PREFIX + "§c발판 위에 서서 수행해주세요.");
	}

	public void onClear(Player p, int code) {
		generalClear(p, code);
	}

	public void onStop(Player p, int code) {
		/*
		 * if(timer != null && timer.GetTimerRunning()) timer.EndTimer(); timer = null;
		 */

	}

	public class Timer extends TimerBase {
		double radius = 0.8;

		private VisualTask vt;

		@Override
		public void EventStartTimer() { // 타이머
			Util.debugMessage(" 타이머 시작됨");
			Player p = getPlayer();
			// Location location = p.getLocation().add(0, 1, 0);
			if (SETTING.VISUAL_TASK.getAsBoolean()) {
				VisualTask vt = new VisualTask() {

					@Override
					public void onStart() {
					}

					@Override
					public void onTicking(int count) {// 파티클 코드 수정 필요 너무 계산낭비임
						for (double x = 0 - radius - 0.2; x <= (radius + 0.2); x += 0.05)
							for (double z = 0 - radius - 0.2; z <= (radius + 0.2); z += 0.05) {
								if ((x * x + z * z) <= radius && (x * x + z * z) >= (radius - 0.1)) {
									double y = (count >= 100 ? 200 - count : count); // count는 200 -> 0 ||| y는 0->-100 후
																						// 100->0감
									// Util.debugMessage("count : "+count);
									y = ((y - 50) * 0.01);
									// Util.debugMessage("y : "+y);
									Location location = p.getLocation();
									location.add(x, 1 + y, z);
									p.getWorld().spawnParticle(Particle.COMPOSTER, location, 1, 0.0, 0.0, 0.0, 1.0,
											null, true);
								}

							}
						// p.getWorld().spawnParticle(Particle.COMPOSTER, location, 15, 0.7, 1.0, 0.7,
						// 1.0,null,true);
					}

					@Override
					public void onFinished() {
					}

					@Override
					public void Reset() {
					}

				};
				vt.StartTimer(p, true);
				this.vt = vt;
			}
		}

		@Override
		public void EventRunningTimer(int count) {
			int ccount = count / 20;
			int slot = 9 - ccount;
			Player p = getPlayer();

			if (p.getOpenInventory().getTitle().contains("Scanning")) { // gui 열고있는지 확인
				if (count % 2 == 0) {
					Util.debugMessage("gui 인식됨");
					ItemStack[] temp = gui.get(0).getContents();
					for (int i = slot; i >= 0;i--) {
						if (0 <= slot && slot <= 8) {
							Util.Stack(gui.get(0), i, Material.GREEN_STAINED_GLASS_PANE, 1,
									"§f" + (10 - ccount) + "0%", "§4클릭불가");
						}
					}
					if (count % 20 == 0) {
						gui.set(0, Bukkit.createInventory(p, 9, "Scanning " + ccount));
						gui.get(0).setContents(temp);
						p.openInventory(gui.get(0));
					}
				}
			} else { // 아니면 캔슬
				Util.debugMessage("인벤토리 닫음 확인");
				stopScan();
			}
			if (p.getOpenInventory().getTitle().contains("Scanning") && count == 0) { // 0초 되면 클리어
				ItemStack[] temp = gui.get(0).getContents();
				gui.set(0, Bukkit.createInventory(p, 9, "Scanning"));
				gui.get(0).setContents(temp);
				p.openInventory(gui.get(0));

				if (p.getOpenInventory().getTitle().equals("Scanning")) {
					status = false;
					onClear(p, 0);
					p.closeInventory();
				}
			}
		}

		@Override
		public void EventEndTimer() {
			Util.debugMessage("타이머 종료됨");
			if(vt != null) vt.Finish(false);
		}

	}

	Timer timer;
	static boolean status = false;// 한 번에 한 플레이어만 가능,

	///////////////////////////////////////////////////
	public void scanning(Player p) {
		if (!status) {
			p.openInventory(gui.get(0));
			status = true;
			timer = new Timer();
			timer.StartTimer(200, true, 1);
		} else {
			p.sendMessage(Main.PREFIX + "§c이미 다른 플레이어가 사용중입니다.");
		}
	}

	public void stopScan() {
		status = false;
		gui.get(0).clear();
		if (timer != null && timer.GetTimerRunning())
			timer.EndTimer();

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		if (!checkPlayer(e))
			return;

		// Inventory inv = e.getClickedInventory();
		// Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() != null) {
			e.setCancelled(true);
		}

	}

}
