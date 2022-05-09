package net.runelite.client.plugins.keyclick;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("keyclick")
public interface KeyClickConfig extends Config
{

	@ConfigItem(
			keyName = "keyToPress",
			name = "Key To Press",
			description = "The key to press so the plugin will toggle. (Not case sensitive)",
			position = 1
	)
	default String keyToPress() {
		return "R";
	}

	@ConfigItem(
			keyName = "rightClick",
			name = "Right Click",
			description = "Right click instead of left click.",
			position = 2
	)
	default boolean rightClick()
	{
		return false;
	}
}
