package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import helper.UIHelper;

public class MainDashboard extends JFrame {
    private JPanel centerPanel;
    private CardLayout cardLayout;
    private ProgressPanel progressPanel;
    private Color colorSidebar = new Color(30, 41, 59);

    public MainDashboard() {
        setTitle("English Mastery Pro"); setSize(1100, 750); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLayout(new BorderLayout());

        JPanel sidebar = new JPanel(new BorderLayout()); sidebar.setPreferredSize(new Dimension(240, 0)); sidebar.setBackground(colorSidebar);
        JLabel logo = new JLabel("ĐĂNG SMILE", JLabel.CENTER); logo.setFont(new Font("Segoe UI", Font.BOLD, 24)); logo.setForeground(Color.WHITE); logo.setBorder(new EmptyBorder(40,10,40,10)); sidebar.add(logo, BorderLayout.NORTH);
        
        JPanel menu = new JPanel(new GridLayout(6, 1, 0, 15)); menu.setBackground(colorSidebar); menu.setBorder(new EmptyBorder(10, 18, 10, 18));
        JButton btnHoc = createMenuBtn("Học từ vựng", "/resources/hoc.png"); 
        JButton btnLuyen = createMenuBtn("Luyện tập", "/resources/luyen.png"); 
        JButton btnThi = createMenuBtn("Kiểm tra", "/resources/thi.png"); 
        JButton btnTienDo = createMenuBtn("Tiến độ", "/resources/tiendo.png");
        menu.add(btnHoc); menu.add(btnLuyen); menu.add(btnThi); menu.add(btnTienDo); sidebar.add(menu, BorderLayout.CENTER);

        cardLayout = new CardLayout(); centerPanel = new JPanel(cardLayout); progressPanel = new ProgressPanel();
        centerPanel.add(new LearningPanel(), "Hoc"); centerPanel.add(new PracticePanel(), "Luyen"); centerPanel.add(new QuizPanel(), "Thi"); centerPanel.add(progressPanel, "TienDo");

        btnHoc.addActionListener(e -> { resetMenu(menu); btnHoc.setBackground(new Color(37,99,235)); cardLayout.show(centerPanel, "Hoc"); UIHelper.enforceBlackText(centerPanel); });
        btnLuyen.addActionListener(e -> { resetMenu(menu); btnLuyen.setBackground(new Color(37,99,235)); cardLayout.show(centerPanel, "Luyen"); UIHelper.enforceBlackText(centerPanel); });
        btnThi.addActionListener(e -> { resetMenu(menu); btnThi.setBackground(new Color(37,99,235)); cardLayout.show(centerPanel, "Thi"); UIHelper.enforceBlackText(centerPanel); });
        btnTienDo.addActionListener(e -> { resetMenu(menu); btnTienDo.setBackground(new Color(37,99,235)); progressPanel.refreshData(); cardLayout.show(centerPanel, "TienDo"); UIHelper.enforceBlackText(centerPanel); });

        btnHoc.doClick(); add(sidebar, BorderLayout.WEST); add(centerPanel, BorderLayout.CENTER); setLocationRelativeTo(null);
    }

    private JButton createMenuBtn(String txt, String iconPath) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g.create(); g2.setColor(getBackground()); g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose(); super.paintComponent(g); }
        };
        ImageIcon icon = UIHelper.loadIcon(this, iconPath, 24, 24); if (icon != null) { b.setIcon(icon); b.setIconTextGap(15); }
        b.setFont(new Font("Segoe UI", Font.BOLD, 16)); b.setForeground(Color.WHITE); b.setBackground(colorSidebar);
        b.setFocusPainted(false); b.setContentAreaFilled(false); b.setBorder(new EmptyBorder(15, 20, 15, 20)); b.setHorizontalAlignment(SwingConstants.LEFT); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
    private void resetMenu(JPanel p) { for (Component c : p.getComponents()) if (c instanceof JButton) c.setBackground(colorSidebar); }
}