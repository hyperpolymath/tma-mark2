// 
// Decompiled by Procyon v0.6.0
// 

package etmaHandler;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.SplashScreen;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import java.awt.Frame;

public class SplashDemo extends Frame implements ActionListener
{
    private static WindowListener closeWindow;
    
    static void renderSplashFrame(final Graphics2D g, final int frame) {
        final String[] comps = { "foo", "bar", "baz" };
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120, 140, 200, 40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading " + comps[frame / 5 % 3] + "...", 120, 150);
    }
    
    public SplashDemo() {
        super("SplashScreen demo");
        this.setSize(300, 200);
        this.setLayout(new BorderLayout());
        final Menu m1 = new Menu("File");
        final MenuItem mi1 = new MenuItem("Exit");
        m1.add(mi1);
        mi1.addActionListener(this);
        this.addWindowListener(SplashDemo.closeWindow);
        final MenuBar mb = new MenuBar();
        this.setMenuBar(mb);
        mb.add(m1);
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }
        final Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
        for (int i = 0; i < 100; ++i) {
            renderSplashFrame(g, i);
            splash.update();
            try {
                Thread.sleep(90L);
            }
            catch (final InterruptedException ex) {}
        }
        splash.close();
        this.setVisible(true);
        this.toFront();
    }
    
    @Override
    public void actionPerformed(final ActionEvent ae) {
        System.exit(0);
    }
    
    public static void main(final String[] args) {
        final SplashDemo test = new SplashDemo();
    }
    
    static {
        SplashDemo.closeWindow = new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                e.getWindow().dispose();
            }
        };
    }
}
