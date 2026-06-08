package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import model.User;
import helper.UIHelper;

public class MainDashboard extends JFrame {

    private final JPanel centerPanel;
    private final CardLayout cardLayout;
    private final ProgressPanel progressPanel;
    private LearningPanel learningPanel;
    private PracticePanel practicePanel;
    private QuizPanel quizPanel;

    private static final Color SIDEBAR_BG     = UIHelper.BG_SIDEBAR;
    private static final Color SIDEBAR_ACTIVE = UIHelper.ACCENT_BLUE;
    private static final Color SIDEBAR_HOVER  = new Color(37, 47, 70);

    public MainDashboard(User user) {
        setTitle("English Mastery Pro - " + user.getUsername());
        setSize(1200, 780);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIHelper.BG_PAGE);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR_BG);

        JPanel logoBlock = new JPanel(new GridLayout(3, 1, 0, 4));
        logoBlock.setBackground(new Color(18, 24, 40));
        logoBlock.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel logoText = new JLabel("English Mastery", JLabel.CENTER);
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoText.setForeground(Color.WHITE);

        JLabel logoSub = new JLabel("by KMA", JLabel.CENTER);
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoSub.setForeground(new Color(148, 163, 184));

        JLabel lblUser = new JLabel(user.getUsername(), JLabel.CENTER);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(96, 165, 250));

        logoBlock.add(logoText);
        logoBlock.add(lblUser);
        logoBlock.add(logoSub);
        sidebar.add(logoBlock, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(SIDEBAR_BG);
        menu.setBorder(new EmptyBorder(16, 12, 16, 12));

        JButton btnHoc    = createMenuBtn("\uD83D\uDCD6", "Học từ vựng");
        JButton btnLuyen  = createMenuBtn("\u270D\uFE0F", "Luyện tập");
        JButton btnThi    = createMenuBtn("\uD83D\uDCDD", "Kiểm tra");
        JButton btnTienDo = createMenuBtn("\uD83D\uDCCA", "Tiến độ");
        JButton btnQuanLy = createMenuBtn("\u2699\uFE0F",  "Quản lý từ");

        menu.add(btnHoc);    menu.add(Box.createVerticalStrut(10));
        menu.add(btnLuyen);  menu.add(Box.createVerticalStrut(10));
        menu.add(btnThi);    menu.add(Box.createVerticalStrut(10));
        menu.add(btnTienDo); menu.add(Box.createVerticalStrut(10));
        menu.add(btnQuanLy);
        sidebar.add(menu, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SIDEBAR_BG);
        bottomPanel.setBorder(new EmptyBorder(0, 12, 16, 12));

        JButton btnLogout = new JButton("Đăng xuất") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setBackground(new Color(55, 30, 30));
        btnLogout.setForeground(new Color(252, 165, 165));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(false);
        btnLogout.setBorder(new EmptyBorder(10, 16, 10, 16));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        bottomPanel.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        cardLayout    = new CardLayout();
        centerPanel   = new JPanel(cardLayout);
        centerPanel.setBackground(UIHelper.BG_PAGE);

        int uid = user.getId();
        progressPanel = new ProgressPanel(uid);
        VocabularyManagerPanel vocabManager = new VocabularyManagerPanel();
        learningPanel = new LearningPanel(uid);
        practicePanel = new PracticePanel(uid);
        quizPanel     = new QuizPanel(uid);

        centerPanel.add(learningPanel, "Hoc");
        centerPanel.add(practicePanel, "Luyen");
        centerPanel.add(quizPanel,     "Thi");
        centerPanel.add(progressPanel, "TienDo");
        centerPanel.add(vocabManager,  "QuanLy");

        vocabManager.setOnDataChanged(() -> {
            learningPanel.reloadLessons();
            practicePanel.reloadLessons();
            quizPanel.reloadWords();
            progressPanel.refreshData();
        });

        btnHoc.addActionListener(e -> { resetMenu(menu); setActive(btnHoc);    cardLayout.show(centerPanel, "Hoc"); });
        btnLuyen.addActionListener(e -> { resetMenu(menu); setActive(btnLuyen); cardLayout.show(centerPanel, "Luyen"); });
        btnThi.addActionListener(e -> { resetMenu(menu); setActive(btnThi);    cardLayout.show(centerPanel, "Thi"); });
        btnTienDo.addActionListener(e -> {
            resetMenu(menu); setActive(btnTienDo);
            progressPanel.refreshData();
            cardLayout.show(centerPanel, "TienDo");
        });
        btnQuanLy.addActionListener(e -> {
            resetMenu(menu); setActive(btnQuanLy);
            vocabManager.refreshTable();
            cardLayout.show(centerPanel, "QuanLy");
        });
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
            }
        });

        add(sidebar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        btnHoc.doClick();
    }

    private JButton createMenuBtn(String icon, String label) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        JLabel lblIcon = new JLabel(icon, JLabel.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setForeground(new Color(203, 213, 225));

        JLabel lblText = new JLabel(label, JLabel.CENTER);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblText.setForeground(new Color(203, 213, 225));

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 2));
        inner.setOpaque(false);
        inner.add(lblIcon);
        inner.add(lblText);

        b.setLayout(new BorderLayout());
        b.add(inner, BorderLayout.CENTER);
        b.setText(null);

        b.putClientProperty("lblIcon", lblIcon);
        b.putClientProperty("lblText", lblText);

        b.setBackground(SIDEBAR_BG);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(10, 6, 10, 6));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        b.setPreferredSize(new Dimension(0, 68));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!b.getBackground().equals(SIDEBAR_ACTIVE)) {
                    b.setBackground(SIDEBAR_HOVER); b.repaint();
                }
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!b.getBackground().equals(SIDEBAR_ACTIVE)) {
                    b.setBackground(SIDEBAR_BG); b.repaint();
                }
            }
        });
        return b;
    }

    private void updateMenuBtnColor(JButton b, Color fg) {
        JLabel lblIcon = (JLabel) b.getClientProperty("lblIcon");
        JLabel lblText = (JLabel) b.getClientProperty("lblText");
        if (lblIcon != null) lblIcon.setForeground(fg);
        if (lblText != null) lblText.setForeground(fg);
    }

    private void setActive(JButton btn) {
        btn.setBackground(SIDEBAR_ACTIVE);
        updateMenuBtnColor(btn, Color.WHITE);
        btn.repaint();
    }

    private void resetMenu(JPanel p) {
        Color defaultFg = new Color(203, 213, 225);
        for (Component c : p.getComponents()) {
            if (c instanceof JButton b) {
                b.setBackground(SIDEBAR_BG);
                updateMenuBtnColor(b, defaultFg);
                b.repaint();
            }
        }
    }
}
