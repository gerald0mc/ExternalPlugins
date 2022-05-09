package net.runelite.client.plugins.keyclick;

import net.runelite.api.Client;
import net.runelite.api.Point;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputUtil {
	public static void leftClick(Client client, int x, int y) {
		assert !client.isClientThread();Point pos = new Point(x, y);
		moveMouse(client, pos);
		double xx = ((double) pos.getX() / (double) client.getMouseCanvasPosition().getX());
		double yy = ((double) pos.getY() / (double) client.getMouseCanvasPosition().getY());
		Point lastPoint = new Point((int) (pos.getX() * xx), (int) (pos.getY() * yy));
		moveMouse(client, lastPoint);
		clickMouse(client, lastPoint, 1);
	}
	
	public static void leftClick(Client client, Point pos) {
		assert !client.isClientThread();
		moveMouse(client, pos);
		double xx = ((double) pos.getX() / (double) client.getMouseCanvasPosition().getX());
		double yy = ((double) pos.getY() / (double) client.getMouseCanvasPosition().getY());
		Point lastPoint = new Point((int) (pos.getX() * xx), (int) (pos.getY() * yy));
		moveMouse(client, lastPoint);
		clickMouse(client, lastPoint, 1);
	}
	
	private static void clickMouse(Client client, Point p, int button) {
		client.getCanvas().dispatchEvent(createEvent(client, p, 501, button));
		client.getCanvas().dispatchEvent(createEvent(client, p, 502, button));
		client.getCanvas().dispatchEvent(createEvent(client, p, 500, button));
	}
}
