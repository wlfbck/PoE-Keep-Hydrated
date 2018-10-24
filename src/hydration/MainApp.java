package hydration;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class MainApp implements NativeKeyListener {

	private boolean hotkeyActive = false;
	//43 Back Slash    circumflex ^
	private int ignoreHotkeyCode = 43;
	//33 F
	private int hotkeyCode = 33;
	private String[] keysToPress = {"2","3","4","5"};;
	
	Robot robot;
	private TrayIcon trayIcon;

	public static void main(String[] args) {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
		try {
			MainApp app = new MainApp();
		} catch (AWTException ex) {
			System.err.println("Failed to initialize Robot.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		try {
			while(true) {Thread.sleep(128);}
		} catch (InterruptedException e) {
		}
	}
	
	public MainApp() throws AWTException {
		GlobalScreen.addNativeKeyListener(this);
		robot = new Robot();
		
		createTrayIcon();
	}

	
	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {
		//System.out.println("pressed"+NativeKeyEvent.getKeyText(event.getKeyCode()));
		//System.out.println(event.getKeyCode());
		
		if(event.getKeyCode() == ignoreHotkeyCode) {
			//toggle
			hotkeyActive = !hotkeyActive;
			return;
		}
		if(event.getKeyCode() == hotkeyCode && !hotkeyActive) {
			for(String s : keysToPress) {
				waitRandomTimeInMS(10, 40);
				robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(s.charAt(0)));
				waitRandomTimeInMS(5, 10);
				robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(s.charAt(0)));
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
		//System.out.println("released"+NativeKeyEvent.getKeyText(event.getKeyCode()));
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent event) {
	}
	
	private void waitRandomTimeInMS(int lowerBound, int upperBound) {
		if(upperBound < lowerBound) {
			System.err.println("waiting limits are wrong!");
			System.exit(1);
		}
		Random rand = new Random();
		double randomIntervall = (upperBound - lowerBound) * rand.nextDouble() + lowerBound;
		try {
			TimeUnit.MILLISECONDS.sleep((long)randomIntervall);
		} catch (InterruptedException ex) {
			System.err.println("waiting interrupted??");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	private void createTrayIcon() {
        PopupMenu trayMenu = new PopupMenu();
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> {
            System.exit(0);
        });
        trayMenu.add(exit);

        BufferedImage icon = null;
        try {
        	System.out.println(getClass().getClassLoader());
            icon = ImageIO.read(getClass().getClassLoader().getResource("blue_potion.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.trayIcon = new TrayIcon(icon, "MercuryTrade", trayMenu);
        this.trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(this.trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
	}
}
