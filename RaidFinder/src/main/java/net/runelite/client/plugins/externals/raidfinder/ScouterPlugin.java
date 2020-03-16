package net.runelite.client.plugins.cagoscouter;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.cagoscouter.utils.ExtUtils;
import net.runelite.client.plugins.cagoscouter.utils.InputHandler;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.Point;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Extension
@PluginDescriptor(
        name = "Raid Finder",
        enabledByDefault = false,
        description = "Scouts a raid layout as input.",
        type = PluginType.EXTERNAL
)
@Slf4j
public class ScouterPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private ScouterConfig config;
    @Inject
    private ExtUtils extUtils;
    @Inject
    private Notifier notifier;

    private ExecutorService executorService;
    private int sign = 29776;
    private int leaveCave = 29778;
    private int enterCave = 29777;
    private String[] layout;
    private String raidCode;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean flash, run, hasPressed, foundRaid;

    @Provides
    ScouterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ScouterConfig.class);
    }

    private String[] getWantedCode() {
        return config.codeWanted().split(",");
    }

    @Override
    protected void startUp() {
        executorService = Executors.newSingleThreadExecutor();
        run = true;
        foundRaid = false;
        hasPressed = false;
    }

    @Override
    protected void shutDown() {
        System.out.println("Shutting down");
        run = false;
        executorService.shutdown();
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGGED_IN) {
            return;
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {

        executorService.submit(() -> {
            if (client.getVar(Varbits.IN_RAID) == 1 && !executorService.isShutdown() && run) {
                if (hasPressed == false) {
                    boolean perfectRaid = findRaid();
                    if (perfectRaid && config.flash()) {
                        setFlash(true);
                        foundRaid = true;
                        System.out.println("RaidFinder: Found Raid.");
                        notifier.notify("Raid has been found.");
                    }
                }
                if (hasPressed == false && foundRaid == false) {
                    try {
                        Thread.sleep(getMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    clickObject(leaveCave, 125);
                    hasPressed = true;
                }
            }

            if ((client.getLocalPlayer().getWorldLocation().getX() == 1246 && client.getLocalPlayer().getWorldLocation().getY() == 3558) ||
                    (client.getLocalPlayer().getWorldLocation().getX() == 1246 && client.getLocalPlayer().getWorldLocation().getY() == 3562)
                            && client.getLocalPlayer().getAnimation() == AnimationID.IDLE && client.getVar(VarPlayer.IN_RAID_PARTY) == -1) {
                hasPressed = false;
                try {
                    Thread.sleep(getMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clickObject(sign, 175);
            }
        });
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (client.getVar(Varbits.IN_RAID) == 1 && !executorService.isShutdown()) {
            String message = Text.removeTags(event.getMessage());
            if (event.getMessage().contains("Layout:")) {
                raidCode = message.substring(message.indexOf("[") + 1, message.lastIndexOf(']'));
                layout = message.substring(message.lastIndexOf(":") + 1).split(",");
            }
        }
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded event) {
        executorService.submit(() ->
        {
            if (event.getGroupId() == 499 && !executorService.isShutdown()) {
                extUtils.click(new Rectangle(new Point(extUtils.getRandomIntBetweenRange(303, 400), extUtils.getRandomIntBetweenRange(298, 318))));
                try {
                    Thread.sleep(getMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                InputHandler.pressKey(this.client.getCanvas(), KeyEvent.VK_ESCAPE);
                try {
                    Thread.sleep(getMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clickObject(enterCave, 45);
            }

            if (event.getGroupId() == WidgetID.DIALOG_OPTION_GROUP_ID) {
                extUtils.pressKey('1');
            }
        });
    }

    private void clickObject(int obj, int zOffset) {
        LocalPoint objPoint = LocalPoint.fromWorld(client, extUtils.findNearestObject(obj).getWorldLocation());
        net.runelite.api.Point p = Perspective.localToCanvas(client, objPoint, client.getPlane(), zOffset);
        InputHandler.leftClick(client, p);
    }

    public boolean findRaid() {
        int tick = 0;
        if (!executorService.isShutdown() && run) {

            for (String code : getWantedCode()) {
                if (code.trim().equals(raidCode.trim())) {
                    for (String[] raid : getWantedRooms()) {
                        for (String room : raid) {
                            for (int i = 0; i < layout.length; i++) {
                                if (room.equalsIgnoreCase(layout[i].trim())) {
                                    tick++;
                                }
                            }
                        }
                        if (tick == raid.length) {
                            run = false;
                            return true;
                        } else {
                            tick = 0;
                        }
                    }
                }
            }
        }

        return false;
    }

    public List<String[]> getWantedRooms() {
        String[] collectionRaid = config.roomsWanted().split(":");
        List<String[]> wantedRooms = new ArrayList<>();

        for (String s : collectionRaid) {
            s = s.replace('[', ' ');
            s = s.replace(']', ' ');
            s = s.trim();
            wantedRooms.add(s.split(","));
        }

        return wantedRooms;
    }

    private long getMillis() {
        return (long) (Math.random() * config.randLow() + config.randHigh());
    }
}
