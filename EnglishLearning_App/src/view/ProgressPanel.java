package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import helper.DatabaseConnection;
import dao.WordDAO;

public class ProgressPanel extends JPanel {
    private JProgressBar practiceBar, quizBar;
    private JLabel lblPractice, lblQuiz;
    private DefaultTableModel practiceModel, quizModel;

    public ProgressPanel() {
        setLayout(new BorderLayout(0, 15)); setBorder(new EmptyBorder(25, 30, 25, 30)); setBackground(new Color(241, 245, 249));

        JTabbedPane tabPane = new JTabbedPane(); tabPane.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JPanel pPractice = new JPanel(new BorderLayout(15, 15)); pPractice.setBackground(Color.WHITE); pPractice.setBorder(new EmptyBorder(20,20,20,20));
        lblPractice = new JLabel("Từ vựng đã thuộc: 0/0"); lblPractice.setFont(new Font("Segoe UI", Font.BOLD, 20));
        practiceBar = new JProgressBar(0, 100); practiceBar.setStringPainted(true); practiceBar.setPreferredSize(new Dimension(0, 30)); practiceBar.setForeground(new Color(16, 185, 129));
        JPanel hPractice = new JPanel(new BorderLayout()); hPractice.setBackground(Color.WHITE); hPractice.add(lblPractice, BorderLayout.NORTH); hPractice.add(practiceBar, BorderLayout.CENTER); pPractice.add(hPractice, BorderLayout.NORTH);
        practiceModel = new DefaultTableModel(new String[]{"Từ Vựng", "Ý Nghĩa", "Trạng Thái"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable tPractice = new JTable(practiceModel); tPractice.setRowHeight(35); tPractice.setFont(new Font("Segoe UI", Font.PLAIN, 16)); pPractice.add(new JScrollPane(tPractice), BorderLayout.CENTER);

        JPanel pQuiz = new JPanel(new BorderLayout(15, 15)); pQuiz.setBackground(Color.WHITE); pQuiz.setBorder(new EmptyBorder(20,20,20,20));
        lblQuiz = new JLabel("Điểm cao nhất: 0/200"); lblQuiz.setFont(new Font("Segoe UI", Font.BOLD, 20));
        quizBar = new JProgressBar(0, 100); quizBar.setStringPainted(true); quizBar.setPreferredSize(new Dimension(0, 30)); quizBar.setForeground(new Color(37, 99, 235));
        JPanel hQuiz = new JPanel(new BorderLayout()); hQuiz.setBackground(Color.WHITE); hQuiz.add(lblQuiz, BorderLayout.NORTH); hQuiz.add(quizBar, BorderLayout.CENTER); pQuiz.add(hQuiz, BorderLayout.NORTH);
        quizModel = new DefaultTableModel(new String[]{"Mã Đề", "Điểm", "Ngày Thi"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable tQuiz = new JTable(quizModel); tQuiz.setRowHeight(35); tQuiz.setFont(new Font("Segoe UI", Font.PLAIN, 16)); pQuiz.add(new JScrollPane(tQuiz), BorderLayout.CENTER);

        tabPane.addTab(" 📈 THỐNG KÊ ", pPractice); tabPane.addTab(" 🏆 LỊCH SỬ THI ", pQuiz);
        add(tabPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        int learned = 0, maxS = 0; practiceModel.setRowCount(0); quizModel.setRowCount(0);
        int totalWords = new WordDAO().getAllWords().size();
        if(totalWords == 0) totalWords = 1; // Tránh chia cho 0

        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rs1 = c.createStatement().executeQuery("SELECT v.word, v.meaning, p.is_mastered FROM UserProgress p JOIN Vocabularies v ON p.vocabulary_id = v.id WHERE p.user_id = 1");
            while (rs1.next()) { 
                String status = rs1.getInt("is_mastered") == 1 ? "ĐÃ THUỘC" : "ĐANG HỌC";
                if(status.equals("ĐÃ THUỘC")) learned++; 
                practiceModel.addRow(new Object[]{rs1.getString("word"), rs1.getString("meaning"), status}); 
            }
            practiceBar.setValue(learned*100/totalWords); lblPractice.setText("Từ vựng đã thuộc: " + learned + "/" + totalWords);

            ResultSet rs2 = c.createStatement().executeQuery("SELECT id, score, date_taken FROM QuizHistory ORDER BY id DESC");
            while (rs2.next()) { int s = rs2.getInt("score"); if(s>maxS) maxS=s; quizModel.addRow(new Object[]{"Đề #" + rs2.getInt("id"), s + "/200", rs2.getString("date_taken")}); }
            quizBar.setValue(maxS*100/200); lblQuiz.setText("Điểm cao nhất: " + maxS + "/200");
        } catch (Exception e) {}
    }
}