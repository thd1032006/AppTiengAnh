package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dao.WordDAO;
import dao.ProgressDAO;
import model.Word;
import helper.UIHelper;

public class QuizPanel extends JPanel {
    private List<Word> allWords;
    private List<Word> currentQuizList;
    private Word currentQuizWord;
    private String correctQuizAnswer;
    private int score = 0, qNum = 1, timeLeft = 120; 
    private Timer timer;
    private JLabel lblScore, lblTimer, lblQuestion, lblResult;
    private JRadioButton[] rbs = new JRadioButton[4];
    private ButtonGroup group = new ButtonGroup();
    private JButton btnStart, btnNext;
    private JComboBox<String> cbMode;
    private ProgressDAO progressDAO = new ProgressDAO();

    public QuizPanel() {
        allWords = new WordDAO().getAllWords();
        setLayout(new BorderLayout(20, 20)); setBackground(new Color(241, 245, 249)); setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(getBackground());
        cbMode = new JComboBox<>(new String[]{"Thi: Anh -> Việt", "Thi: Việt -> Anh"}); cbMode.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblScore = new JLabel("Điểm: 0", JLabel.CENTER); lblScore.setFont(new Font("Segoe UI", Font.BOLD, 24)); lblScore.setForeground(new Color(37, 99, 235));
        lblTimer = new JLabel("⏳ 02:00", JLabel.RIGHT); lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 24)); lblTimer.setForeground(Color.RED);
        header.add(cbMode, BorderLayout.WEST); header.add(lblScore, BorderLayout.CENTER); header.add(lblTimer, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(7, 1, 10, 10)); content.setBackground(getBackground());
        lblQuestion = new JLabel("Bấm Bắt Đầu để thi!", JLabel.CENTER); lblQuestion.setFont(new Font("Segoe UI", Font.BOLD, 26)); content.add(lblQuestion);
        for (int i=0; i<4; i++) { rbs[i] = new JRadioButton("Đáp án"); rbs[i].setFont(new Font("Segoe UI", Font.BOLD, 18)); rbs[i].setEnabled(false); group.add(rbs[i]); content.add(rbs[i]); }
        lblResult = new JLabel(" ", JLabel.CENTER); lblResult.setFont(new Font("Segoe UI", Font.BOLD, 22)); content.add(lblResult); 
        
        JPanel bot = new JPanel(new FlowLayout()); bot.setBackground(getBackground());
        btnStart = UIHelper.createButton(this, "BẮT ĐẦU", null, new Color(191, 219, 254));
        btnNext = UIHelper.createButton(this, "CHỐT ĐÁP ÁN", null, new Color(167, 243, 208)); btnNext.setEnabled(false);
        bot.add(btnStart); bot.add(btnNext); content.add(bot); add(content, BorderLayout.CENTER);

        timer = new Timer(1000, e -> { timeLeft--; lblTimer.setText(String.format("⏳ %02d:%02d", timeLeft/60, timeLeft%60)); if(timeLeft<=0) finish(); });
        
        btnStart.addActionListener(e -> {
            if(allWords.size() < 20) { JOptionPane.showMessageDialog(this, "Thiếu dữ liệu DB (cần 20 từ)!"); return; }
            currentQuizList = new ArrayList<>(allWords); Collections.shuffle(currentQuizList); currentQuizList = currentQuizList.subList(0, 20);
            score = 0; qNum = 1; timeLeft = 120; lblScore.setText("Điểm: 0");
            btnStart.setEnabled(false); btnNext.setEnabled(true); cbMode.setEnabled(false); loadQ(); timer.start();
        });
        
        btnNext.addActionListener(e -> {
            if(btnNext.getText().equals("CHỐT ĐÁP ÁN")) {
                String sel = ""; for(JRadioButton rb : rbs) if(rb.isSelected()) sel = rb.getText().substring(5); 
                if(sel.isEmpty()) return;
                for(JRadioButton rb : rbs) rb.setEnabled(false);
                if(sel.equals(correctQuizAnswer)) { score += 10; lblScore.setText("Điểm: " + score); lblResult.setText("✅ Đúng!"); lblResult.setForeground(new Color(16,185,129)); }
                else { lblResult.setText("❌ Sai. Đáp án: " + correctQuizAnswer); lblResult.setForeground(Color.RED); }
                btnNext.setText("CÂU TIẾP THEO");
            } else { qNum++; loadQ(); }
        });
    }

    private void loadQ() {
        if(qNum > 20) { finish(); return; }
        currentQuizWord = currentQuizList.get(qNum - 1);
        int mode = cbMode.getSelectedIndex();
        List<String> opts = new ArrayList<>();
        List<Word> tmp = new ArrayList<>(allWords); tmp.remove(currentQuizWord); Collections.shuffle(tmp); 
        correctQuizAnswer = (mode == 0) ? currentQuizWord.getMeaning() : currentQuizWord.getWord();
        opts.add(correctQuizAnswer); for(int i=0; i<3; i++) opts.add((mode == 0) ? tmp.get(i).getMeaning() : tmp.get(i).getWord()); Collections.shuffle(opts);
        
        lblQuestion.setText("Câu " + qNum + ": " + ((mode==0) ? currentQuizWord.getWord() : currentQuizWord.getMeaning()));
        for(int i=0; i<4; i++) { rbs[i].setText("  " + (char)('A'+i) + ". " + opts.get(i)); rbs[i].setEnabled(true); }
        group.clearSelection(); lblResult.setText(" "); btnNext.setText("CHỐT ĐÁP ÁN"); 
    }
    private void finish() {
        timer.stop(); btnNext.setEnabled(false); btnStart.setEnabled(true); cbMode.setEnabled(true);
        for (JRadioButton rb : rbs) rb.setEnabled(false); lblQuestion.setText("ĐÃ NỘP BÀI!");
        progressDAO.saveQuizScore(score); JOptionPane.showMessageDialog(this, "Hoàn thành! Bạn được " + score + "/200 điểm.");
    }
}