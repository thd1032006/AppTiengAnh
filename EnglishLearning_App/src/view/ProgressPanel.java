package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import helper.DatabaseConnection;
import helper.UIHelper;

public class ProgressPanel extends JPanel {

    private int userId;
    private JProgressBar quizBar;
    private JLabel lblQuizBest;
    private DefaultTableModel practiceModel, quizModel;

    public ProgressPanel() {
        this(1);
    }

    public ProgressPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.BG_PAGE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(UIHelper.BG_PAGE);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel lblTitle = new JLabel("Tiến độ học tập");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(UIHelper.TEXT_PRIMARY);
        header.add(lblTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabPane.setBackground(UIHelper.BG_PAGE);
        tabPane.addTab("Từ vựng đã học", buildPracticeTab());
        tabPane.addTab("Lịch sử kiểm tra", buildQuizTab());
        add(tabPane, BorderLayout.CENTER);
    }

    private JPanel buildPracticeTab() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(UIHelper.BG_CARD);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        practiceModel = new DefaultTableModel(new String[]{"Từ vựng", "Nghĩa tiếng Việt", "Trạng thái"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = buildTable(practiceModel);
        tbl.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildQuizTab() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(UIHelper.BG_CARD);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel quizCard = new JPanel(new BorderLayout(0, 10));
        quizCard.setBackground(new Color(240, 253, 244));
        quizCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(167, 243, 208), 1, true),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel lblQuiz = new JLabel("Điểm cao nhất");
        lblQuiz.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblQuiz.setForeground(UIHelper.TEXT_PRIMARY);

        lblQuizBest = new JLabel("0 / 300");
        lblQuizBest.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblQuizBest.setForeground(UIHelper.TEXT_SECOND);

        JPanel labelRow2 = new JPanel(new BorderLayout());
        labelRow2.setOpaque(false);
        labelRow2.add(lblQuiz, BorderLayout.WEST);
        labelRow2.add(lblQuizBest, BorderLayout.EAST);

        quizBar = new JProgressBar(0, 100);
        quizBar.setStringPainted(false);
        quizBar.setPreferredSize(new Dimension(0, 10));
        quizBar.setForeground(UIHelper.ACCENT_BLUE);
        quizBar.setBackground(new Color(219, 234, 254));
        quizBar.setBorder(null);

        quizCard.add(labelRow2, BorderLayout.NORTH);
        quizCard.add(quizBar, BorderLayout.CENTER);
        p.add(quizCard, BorderLayout.NORTH);

        quizModel = new DefaultTableModel(new String[]{"Mã đề", "Điểm", "Ngày thi"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        p.add(new JScrollPane(buildTable(quizModel)), BorderLayout.CENTER);
        return p;
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setRowHeight(38);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tbl.setForeground(UIHelper.TEXT_PRIMARY);
        tbl.setGridColor(UIHelper.BORDER_COLOR);
        tbl.setShowVerticalLines(false);
        tbl.setSelectionBackground(new Color(239, 246, 255));
        tbl.setSelectionForeground(UIHelper.TEXT_PRIMARY);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.getTableHeader().setBackground(new Color(248, 250, 252));
        tbl.getTableHeader().setForeground(UIHelper.TEXT_SECOND);
        tbl.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, UIHelper.BORDER_COLOR));
        return tbl;
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            lbl.setHorizontalAlignment(JLabel.CENTER);
            if ("ĐÃ THUỘC".equals(val)) {
                lbl.setForeground(UIHelper.ACCENT_GREEN);
                lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            } else {
                lbl.setForeground(UIHelper.ACCENT_AMBER);
                lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
            }
            return lbl;
        }
    }

    public void refreshData() {
        practiceModel.setRowCount(0);
        quizModel.setRowCount(0);

        try (Connection c = DatabaseConnection.getConnection()) {
            if (c == null) return;

            String sql1 = "SELECT v.word, v.meaning, p.is_mastered " +
                          "FROM UserProgress p JOIN Vocabularies v ON p.vocabulary_id = v.id " +
                          "WHERE p.user_id = " + userId;
            try (ResultSet rs1 = c.createStatement().executeQuery(sql1)) {
                while (rs1.next()) {
                    String status = rs1.getInt("is_mastered") == 1 ? "ĐÃ THUỘC" : "ĐANG HỌC";
                    practiceModel.addRow(new Object[]{
                        rs1.getString("word"), rs1.getString("meaning"), status
                    });
                }
            }

            int maxS = 0;
            String sql2 = "SELECT quiz_number, score, date_taken FROM QuizHistory " +
                          "WHERE user_id = " + userId + " ORDER BY quiz_number DESC";
            try (ResultSet rs2 = c.createStatement().executeQuery(sql2)) {
                while (rs2.next()) {
                    int s = rs2.getInt("score");
                    if (s > maxS) maxS = s;
                    quizModel.addRow(new Object[]{
                        "Đề #" + rs2.getInt("quiz_number"), s + " / 300", rs2.getString("date_taken")
                    });
                }
            }
            quizBar.setValue(maxS * 100 / 300);
            lblQuizBest.setText(maxS + " / 300");

        } catch (Exception e) {
            System.err.println("Lỗi ProgressPanel: " + e.getMessage());
        }
    }
}
