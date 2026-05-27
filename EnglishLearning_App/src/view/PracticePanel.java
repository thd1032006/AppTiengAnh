package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import dao.WordDAO;
import dao.LessonDAO;
import dao.ProgressDAO;
import model.Word;
import model.Lesson;
import helper.UIHelper;

public class PracticePanel extends JPanel {
    private List<Word> allWords;
    private Word currentWord;
    private Random rnd = new Random();
    private JLabel lblQuestion, lblResult;
    private JTextField txtAnswer;
    private JComboBox<Object> cbTopic;
    private JComboBox<String> cbMode;
    private ProgressDAO progressDAO = new ProgressDAO();

    public PracticePanel() {
        allWords = new WordDAO().getAllWords();
        setLayout(new GridBagLayout()); setBackground(new Color(241, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(20, 20, 20, 20); gbc.gridy = 0; gbc.gridx = 0;

        JPanel filter = new JPanel(new FlowLayout()); filter.setBackground(getBackground());
        cbTopic = new JComboBox<>(); cbTopic.addItem("Tất cả chủ đề");
        for (Lesson l : new LessonDAO().getAllLessons()) cbTopic.addItem(l);
        cbMode = new JComboBox<>(new String[]{"Anh -> Việt", "Việt -> Anh"});
        cbTopic.setFont(new Font("Segoe UI", Font.BOLD, 16)); cbMode.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        filter.add(new JLabel("Chủ đề:")); filter.add(cbTopic); filter.add(new JLabel(" Chế độ:")); filter.add(cbMode);
        add(filter, gbc);

        gbc.gridy++; lblQuestion = new JLabel("Bấm 'Tiếp Theo' để bắt đầu", JLabel.CENTER); lblQuestion.setFont(new Font("Segoe UI", Font.BOLD, 28)); add(lblQuestion, gbc);

        gbc.gridy++; JPanel inputPanel = new JPanel(new FlowLayout()); inputPanel.setBackground(getBackground());
        txtAnswer = new JTextField(20); txtAnswer.setFont(new Font("Segoe UI", Font.BOLD, 22)); txtAnswer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 2), new EmptyBorder(10, 10, 10, 10)));
        JButton btnCheck = UIHelper.createButton(this, "Kiểm Tra", null, new Color(191, 219, 254));
        JButton btnNext = UIHelper.createButton(this, "Tiếp Theo", "/resources/next.png", new Color(167, 243, 208));
        inputPanel.add(txtAnswer); inputPanel.add(btnCheck); inputPanel.add(btnNext); add(inputPanel, gbc);

        gbc.gridy++; lblResult = new JLabel(" "); lblResult.setFont(new Font("Segoe UI", Font.BOLD, 24)); add(lblResult, gbc);

        btnNext.addActionListener(e -> nextQuestion());
        btnCheck.addActionListener(e -> checkAnswer());
        txtAnswer.addActionListener(e -> checkAnswer());
        cbTopic.addActionListener(e -> nextQuestion());
    }

    private void nextQuestion() {
        if(allWords == null || allWords.isEmpty()) return;
        Object sel = cbTopic.getSelectedItem();
        List<Word> filtered;
        if (sel instanceof Lesson) {
            int id = ((Lesson)sel).getId();
            filtered = allWords.stream().filter(w -> w.getLessonId() == id).collect(Collectors.toList());
        } else { filtered = allWords; }

        if(filtered.isEmpty()) { lblQuestion.setText("Chưa có từ vựng!"); return; }
        currentWord = filtered.get(rnd.nextInt(filtered.size()));
        lblQuestion.setText(cbMode.getSelectedIndex() == 0 ? "Dịch: " + currentWord.getWord() : "Tiếng Anh: " + currentWord.getMeaning());
        txtAnswer.setText(""); lblResult.setText(" "); txtAnswer.requestFocus();
    }

    private void checkAnswer() {
        if(currentWord == null || txtAnswer.getText().trim().isEmpty()) return;
        String ans = txtAnswer.getText().trim();
        String cor = (cbMode.getSelectedIndex() == 0) ? currentWord.getMeaning() : currentWord.getWord();
        if(ans.equalsIgnoreCase(cor)) {
            lblResult.setText("✅ CHÍNH XÁC!"); lblResult.setForeground(new Color(16, 185, 129));
            progressDAO.markAsMastered(currentWord.getId());
        } else {
            lblResult.setText("❌ SAI! Đáp án đúng: " + cor); lblResult.setForeground(Color.RED);
        }
    }
}