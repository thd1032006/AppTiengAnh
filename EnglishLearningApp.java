import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLearningApp extends JFrame {
    private final Color sidebarColor = new Color(16, 30, 51);
    private final Color activeColor = new Color(20, 184, 166);
    private final Color backgroundColor = new Color(244, 247, 251);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, JButton> menuButtons = new LinkedHashMap<String, JButton>();

    public EnglishLearningApp() {
        setTitle("English Basic - Khung giao diện");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createMainLayout());
        showPage("home");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EnglishLearningApp app = new EnglishLearningApp();
                app.setVisible(true);
            }
        });
    }

    private JPanel createMainLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createSidebar(), BorderLayout.WEST);
        mainPanel.add(createContentArea(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel appName = new JLabel("English Basic");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(LEFT_ALIGNMENT);

        JLabel slogan = new JLabel("Learn smarter");
        slogan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        slogan.setForeground(new Color(203, 213, 225));
        slogan.setAlignmentX(LEFT_ALIGNMENT);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(slogan);
        sidebar.add(Box.createVerticalStrut(40));

        sidebar.add(createMenuButton("Trang chủ", "home"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Từ vựng", "vocabulary"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Luyện tập", "practice"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Ngữ pháp", "grammar"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Tiến độ", "progress"));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createGoalBox());

        return sidebar;
    }

    private JButton createMenuButton(String text, final String pageName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 45));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(sidebarColor);
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPage(pageName);
            }
        });

        menuButtons.put(pageName, button);
        return button;
    }

    private JPanel createGoalBox() {
        JPanel goalBox = new JPanel();
        goalBox.setMaximumSize(new Dimension(200, 80));
        goalBox.setBackground(new Color(36, 52, 78));
        goalBox.setLayout(new BorderLayout());
        goalBox.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Mục tiêu hôm nay");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel value = new JLabel("15 phút học");
        value.setForeground(new Color(204, 251, 241));
        value.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        goalBox.add(title, BorderLayout.NORTH);
        goalBox.add(value, BorderLayout.CENTER);
        return goalBox;
    }

    private JPanel createContentArea() {
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(backgroundColor);
        contentArea.setBorder(new EmptyBorder(30, 30, 30, 30));

        contentPanel.setBackground(backgroundColor);
        contentPanel.add(createPage("Trang chủ", "Đây là trang tổng quan của ứng dụng."), "home");
        contentPanel.add(createPage("Từ vựng", "Trang này dùng để hiển thị danh sách từ vựng."), "vocabulary");
        contentPanel.add(createPage("Luyện tập", "Trang này dùng để làm bài tập và kiểm tra đáp án."), "practice");
        contentPanel.add(createPage("Ngữ pháp", "Trang này dùng để học công thức ngữ pháp."), "grammar");
        contentPanel.add(createPage("Tiến độ", "Trang này dùng để xem quá trình học tập."), "progress");

        contentArea.add(contentPanel, BorderLayout.CENTER);
        return contentArea;
    }

    private JPanel createPage(String title, String description) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(backgroundColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descriptionLabel.setForeground(new Color(100, 116, 139));

        JPanel header = new JPanel();
        header.setBackground(backgroundColor);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(8));
        header.add(descriptionLabel);

        JPanel emptyBox = new JPanel(new BorderLayout());
        emptyBox.setBackground(Color.WHITE);
        emptyBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel placeholder = new JLabel("Nội dung của trang " + title, SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.BOLD, 22));
        placeholder.setForeground(new Color(15, 23, 42));
        emptyBox.add(placeholder, BorderLayout.CENTER);

        page.add(header, BorderLayout.NORTH);
        page.add(emptyBox, BorderLayout.CENTER);

        return page;
    }

    private void showPage(String pageName) {
        cardLayout.show(contentPanel, pageName);

        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            JButton button = entry.getValue();
            boolean isSelected = entry.getKey().equals(pageName);

            if (isSelected) {
                button.setBackground(activeColor);
            } else {
                button.setBackground(sidebarColor);
            }
        }
    }
}
