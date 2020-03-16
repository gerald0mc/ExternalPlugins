package net.runelite.client.plugins.cagoscouter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(value = "ScouterConfig")
public interface ScouterConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName =  "raidType",
            name = "Pick the Rooms to look for",
            description = "This will determine what rooms to look for."
    )
    default String roomsWanted() { return ""; }

    @ConfigItem(
            position = 1,
            keyName =  "raidCode",
            name = "Pick the layout code to look for",
            description = "This will determine what code to look for."
    )
    default String codeWanted() { return ""; }

    @ConfigItem(
            keyName = "randLow",
            name = "Minimum Delay",
            description = "For MouseEvents only.",
            position = 2,
            titleSection = "helperConfig"
    )
    default int randLow()
    {
        return 70;
    }

    @ConfigItem(
            keyName = "randLower",
            name = "Maximum Delay",
            description = "For MouseEvents only.",
            position = 3,
            titleSection = "helperConfig"
    )
    default int randHigh()
    {
        return 80;
    }

    @ConfigItem(
            keyName = "flash",
            name = "Flash on finding Raid",
            description = "Your Screen flashes when you find a raid",
            position = 4
    )
    default boolean flash()
    {
        return false;
    }
}
