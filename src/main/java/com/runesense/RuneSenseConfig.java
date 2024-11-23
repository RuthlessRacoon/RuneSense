package com.runesense;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("config")
public interface RuneSenseConfig extends Config
{
	@ConfigItem(
		keyName = "local_ip",
		name = "Local IP",
		description = "Local IP for Lovense Remote Game Mode"
	)
	default String local_ip() {
		return "";
	}

	@ConfigItem(
			keyName = "port",
			name = "Port",
			description = "Port for Lovense Remote Game Mode. NOT the SSL Port."
	)
	default String port() {
		return "20010";
	}
}
