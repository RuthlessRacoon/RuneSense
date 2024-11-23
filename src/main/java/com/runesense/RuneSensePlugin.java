package com.runesense;

import com.google.gson.Gson;
import com.google.inject.Provides;
import javax.inject.Inject;

import com.runesense.api.Command;
import com.runesense.ui.RuneSensePanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import okhttp3.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
	name = "RuneSense"
)
public class RuneSensePlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private RuneSenseConfig config;

	private NavigationButton navButton;

	private boolean initializing;
	private Map<Skill, Integer> skillXp;
	private OkHttpClient httpClient;

	public ArrayList<Command> commands;

	@Inject
	public RuneLiteConfig runeLiteConfig;
	public RuneLiteConfig getRuneLiteConfig() { return runeLiteConfig; }

	public BufferedImage icon;
	public BufferedImage getIcon() { return icon; }

	private final String COMMAND_DATA_DIR_NAME = "runesense";
	private final String COMMAND_DATA_FILE_NAME = "RuneSenseData.json";
	private File commandDataDir;
	@Inject
	private Gson injectedGson;
	private Gson gson;
	@Inject
	private ScheduledExecutorService executor;

	@Override
	protected void startUp() throws Exception {
		log.debug("Starting RuneSense");

		RuneSensePanel panel = new RuneSensePanel(this);
		icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		navButton = NavigationButton.builder()
				.tooltip("RuneSense")
				.icon(icon)
				.panel(panel)
				.priority(10)
				.build();
		clientToolbar.addNavigation(navButton);

		initializing = true;
		skillXp = new HashMap<>();
		httpClient = new OkHttpClient();
		commands = new ArrayList<>();

		commandDataDir = new File(RuneLite.RUNELITE_DIR, COMMAND_DATA_DIR_NAME);
		boolean ignored = commandDataDir.mkdirs();
		gson = injectedGson.newBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create();

		loadCommandData();
		panel.refreshCommandPanel();
	}

	@Override
	protected void shutDown() throws Exception {
		saveCommandData();
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onClientShutdown(ClientShutdown event) {
		event.waitFor(executor.submit(this::saveCommandData));
	}

	private Command findFirstCommand(Skill skill, int xpDiff) {
		for (Command command : commands) {
			switch (command.getTrigger().type()) {
				case XP_GAIN:
					if (command.getTrigger().threshold() <= xpDiff)
						return command;
					break;
				//case DROP_GP:
				//	break;
			}
		}
		return null;
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged) {
		if (initializing) return;

		final Skill skill = statChanged.getSkill();
		final int currentXp = statChanged.getXp();
		final int currentLevel = statChanged.getLevel();

		final int diff = currentXp - skillXp.get(skill);
		skillXp.replace(skill, currentXp);
		log.debug(skill.name() + "(" + currentLevel + "): " + diff);

		Command c = findFirstCommand(skill, diff);

		if (c != null) {
			log.debug("Duration: " + c.getDuration());
			try {
				Request request = new Request.Builder()
						.url("http://" + config.local_ip() + ":" + config.port() + "/command")
						.post(c.getRequestBody())
						.build();
				Response response = httpClient.newCall(request).execute();
				response.close();
			} catch (Exception e) {
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", e.getMessage(), "RuneSense");
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick ignoredEvent) {
		if (initializing) {
			initializing = false;
			// Grab initial XP values
			for (Skill skill : Skill.values()) {
				skillXp.put(skill, client.getSkillExperience(skill));
			}
		}
	}

	@Provides
	RuneSenseConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(RuneSenseConfig.class);
	}

	private void saveCommandData() {
		try {
			File commandFile = new File(commandDataDir, COMMAND_DATA_FILE_NAME);
			Writer writer = new FileWriter(commandFile);
			gson.toJson(commands, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			log.warn("Error while savings command data " + e.getMessage());
		}
	}

	private void loadCommandData() {
		try {
			File commandFile = new File(commandDataDir, COMMAND_DATA_FILE_NAME);
			if (commandFile.exists()) {
				commands.addAll(Arrays.asList(gson.fromJson(new FileReader(commandFile), Command[].class)));
			}
		} catch (Exception e) {
			log.warn("Error while loading command data " + e.getMessage());
		}
	}
}
