package bepo.au;

import bepo.au.Main.SETTING;
import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.function.*;
import bepo.au.games.AGameTicker;
import bepo.au.manager.BossBarManager;
import bepo.au.manager.BossBarManager.BossBarList;
import bepo.au.manager.LocManager;
import bepo.au.manager.ScoreboardManager;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameTimer extends BukkitRunnable {

	public enum Status {
		READY, GAME_SETTING, WORKING, VOTING, END;
	}

	public enum WinReason {
		IMPO_OXYG, IMPO_NUCL, IMPO_KILLALL, CREW_MISSION, CREW_KILLALL;
	}

	public enum GameType {
		NORMAL("일반", 137), CHASETAG("술래잡기", 113); //HIDENSEEK("역술래잡기", 373);

		private String name;
		private int preview_map_id;
		private AGameTicker gameticker;
		public static ArrayList<String> TYPES = new ArrayList<String>();

		GameType(String name, int preview_map_id) {
			this.name = name;
			this.preview_map_id = preview_map_id;
		}

		public String getName() {
			return this.name;
		}
		
		public int getPreviewMapId() {
			return this.preview_map_id;
		}

		public void setGameTicker(AGameTicker gt) {
			this.gameticker = gt;
		}

		public AGameTicker getGameTicker() {
			return this.gameticker;
		}

		static {
			for (GameType type : GameType.values()){
					TYPES.add(type.toString());
			}
		}
	}

	public static final ColorUtil[] COLORLIST = { ColorUtil.CYAN, ColorUtil.BLUE, ColorUtil.GRAY, ColorUtil.GREEN,
			ColorUtil.ORANGE, ColorUtil.PURPLE, ColorUtil.RED, ColorUtil.WHITE, ColorUtil.YELLOW, ColorUtil.PINK,
			ColorUtil.BLACK, ColorUtil.LIGHT_GRAY };
	public static final List<ColorUtil> COLORS = new ArrayList<ColorUtil>();

	private int timer = 0;
	private int win_timer = 0;

	private Main main;

	private Status status = Status.READY;
	private boolean pause = false;

	public static GameType gamemode;
	private AGameTicker game_ticker;

	public static List<String> OBSERVER = new ArrayList<String>();
	public static List<String> PLAYERS = new ArrayList<String>();
	public static List<String> IMPOSTER = new ArrayList<String>();

	public static List<Player> ALIVE_PLAYERS = new ArrayList<Player>();
	public static List<String> ALIVE_IMPOSTERS = new ArrayList<String>();

	public static int REQUIRED_MISSION = 0;
	public static int CLEARED_MISSION = 0;

	public static int EMERG_REMAIN_TICK = 0;

	public static WinReason WIN_REASON = null;

	private World world;
	public static Assemble assemble;

	public GameTimer(World world, Main main) {
		this.main = main;
		assemble = new Assemble(main, new ScoreboardManager());
		assemble.setAssembleStyle(AssembleStyle.MODERN);
		
		this.world = world;

	}

	public static final int getRemainImposter() {
		int i = 0;
		for (String name : IMPOSTER) {
			if (PlayerData.getPlayerData(name) != null && PlayerData.getPlayerData(name).isAlive())
				i++;
		}
		return i;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status s) {
		this.status = s;
	}

	public void start(Player p) {
		Bukkit.broadcastMessage(Main.PREFIX + "§e" + p.getName() + "§f님께서 게임을 시작하셨습니다!");
		gamemode = SETTING.GAMEMODE.getAsGameType();
		game_ticker = gamemode.getGameTicker();
		game_ticker.setting(p.getWorld());
		status = Status.GAME_SETTING;
		this.runTaskTimer(main, 0L, 1L);
	}

	public void stop() {
		if (!this.isCancelled())
			this.cancel();
		for (Player ap : Bukkit.getOnlinePlayers()) {
			ap.removePotionEffect(PotionEffectType.BLINDNESS);
			if (ap.getGameMode() == GameMode.ADVENTURE || ap.getGameMode() == GameMode.SURVIVAL) {
				ap.setAllowFlight(false);
				ap.setFlying(false);
			}
			PlayerUtil.toggleRedEffect(ap, false);
			PlayerUtil.resetHidden(ap);
			PlayerUtil.resetGlowingBlock(ap);
			ap.setPlayerListName(ap.getName());
			ap.setWalkSpeed(0.2F);
			if (PLAYERS.contains(ap.getName())) {
				ap.setGameMode(GameMode.ADVENTURE);
				ap.getInventory().clear();
			}
		}
		stop_reset(world);

	}
	
	public static void module_reset() {
		PlayerUtil.resetChairs();
		Util.despawnEmergArmorStand();
		Util.resetCorpse();
		
		BossBarManager.resetAllBossBar();
		if(assemble != null) assemble.cleanup();
	}

	private void stop_reset(World w) {
		
		Main.gt = null;
		Commons = new Mission[2];
		PLAYERS.clear();
		IMPOSTER.clear();
		ALIVE_PLAYERS.clear();
		ALIVE_IMPOSTERS.clear();
		CLEARED_MISSION = 0;
		WIN_REASON = null;
		setStatus(Status.GAME_SETTING);
		// VisualTask.resetAllTasks();
		SightTimer.stop();
		PlayerData.resetPlayerDataList();
		Sabotage.saboStopAll();
		SabotageGUI.stopTimer();
		Util.toggleDescriptionArmorStand(w, true);
		CCTV.deactivateCCTV(world);
		CCTV.watchingCCTVset.clear();
		HandlerList.unregisterAll(Main.getEventManager());
		if (VoteSystem.PROGRESSED_VOTE != null)
			VoteSystem.PROGRESSED_VOTE.cancel();
		VoteSystem.PROGRESSED_VOTE = null;
		VoteSystem.resetArmorStand();
		VoteSystem.vrt = null;
		Mission.deactivateMission();
		module_reset();
		/*
		 * List<PlayerData> lists = new
		 * ArrayList<PlayerData>(PlayerData.getPlayerDataList()); for(PlayerData pd :
		 * lists) { PlayerData.getPlayerDataList().remove(pd); }
		 */
	}

	public static Mission[] Commons = new Mission[2];

	public void run() {

		if (WIN_REASON != null) {
			switch (win_timer) {
			case 0:
				String imposters = "";
				for (String imp : GameTimer.IMPOSTER)
					imposters += PlayerData.getPlayerData(imp).getColor().getChatColor() + imp + " ";

				for (Player ap : Bukkit.getOnlinePlayers()) {
					if (WIN_REASON.toString().contains("CREW"))
						ap.sendTitle("§f크루원 승리", "§c임포스터 : " + imposters, 20, 100, 20);
					else
						ap.sendTitle("§c임포스터 승리", "§c임포스터 : " + imposters, 20, 100, 20);
				}
				setStatus(Status.END);
				break;

			case 40:
				for (Player ap : Bukkit.getOnlinePlayers())
					ap.teleport(LocManager.getLoc("Lobby").get(0));
				stop();

				break;
			}
			win_timer++;
			return;
		}

		switch (timer) {
		case 60:
			Bukkit.broadcastMessage(Main.PREFIX + "§f=====================");
			Bukkit.broadcastMessage(Main.PREFIX + "§e게임 참가자 목록");
			for (int i = 0; i < PLAYERS.size(); i++) {
				Bukkit.broadcastMessage(Main.PREFIX + "§f" + (i + 1) + ". §a" + PLAYERS.get(i));
				Player ap = Bukkit.getPlayer(PLAYERS.get(i));
				if(ap != null){
					ap.setGameMode(GameMode.ADVENTURE);
					ap.getInventory().clear();
				}
			}
			Bukkit.broadcastMessage(Main.PREFIX + "§f=====================");
			break;
		case 160:
			Bukkit.broadcastMessage(Main.PREFIX + "§f직업을 분배합니다...");
			break;
		case 200:
			game_ticker.team_split();
			break;
		case 300:
			Bukkit.broadcastMessage(Main.PREFIX + "§f직업 분배가 완료되었습니다. 5초 뒤 게임을 시작합니다.");
			break;
		case 320:
		case 340:
		case 360:
		case 380:
		case 400:
			Bukkit.broadcastMessage(Main.PREFIX + "§c" + ((420 - timer) / 20) + "초 전");
			break;
		case 420:
			Bukkit.broadcastMessage(Main.PREFIX + "§a게임 시작!");
			if (SETTING.CREW_SIGHT_BLOCK.getAsInteger() >= 0 && SETTING.IMPOSTER_SIGHT_BLOCK.getAsInteger() >= 0)
				SightTimer.start();
			
			status = Status.WORKING;
			PlayerUtil.setSeats(true);
			world.setTime(18000L);
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			Bukkit.getPluginManager().registerEvents(Main.getEventManager(), main);
			Vent.closeAll();
			Sabotage.saboResetAll(true);
			SabotageGUI.startTimer();
			CCTV.activateCCTV(world);
			AdminMap.gui = AdminMap.initializeGUI(true);
			BossBarManager.registerBossBar(BossBarList.TASKS);
			game_ticker.onGameStart(world);
			for (int i = 1; i < 8; i++)
				Sabotage.openDoor(i);
			break;
		}

		// 소리 방해
		if (SETTING.BLOCK_PLAYER_SOUND.getAsBoolean() && !Main.isProtocolHooked) {
			for (Player p : GameTimer.ALIVE_PLAYERS) {
				float v = (float) timer / 200F;
				p.playSound(p.getLocation(), Sound.ENTITY_GHAST_SCREAM, SoundCategory.PLAYERS, v, 1.0F);
				p.playSound(p.getLocation().add(0, 0, 1.0), Sound.ENTITY_CAT_BEG_FOR_FOOD, SoundCategory.PLAYERS, v, 1.0F);
				p.playSound(p.getLocation().add(1.0, 0, 0), Sound.ENTITY_WOLF_HOWL, SoundCategory.PLAYERS, v, 1.0F);
			}
		}

		game_ticker.tick(status, timer);

		if (!pause)
			timer++;
	}

}
