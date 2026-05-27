package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import dao.WordDAO;
import dao.LessonDAO;
import dao.ProgressDAO;
import model.Word;
import model.Lesson;
import helper.UIHelper;

public class LearningPanel extends JPanel {
    private List<Word> words = new ArrayList<>();
    private int currentIndex = 0;
    private JLabel lblWord, lblPronounce, lblMeaning;
    private JComboBox<Object> cbLessons;
    
    private WordDAO wordDAO = new WordDAO();
    private ProgressDAO progressDAO = new ProgressDAO();

    public LearningPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); header.setBackground(getBackground());
        JLabel lblTitle = new JLabel("📌 CHỌN CHỦ ĐỀ: "); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cbLessons = new JComboBox<>(); cbLessons.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        cbLessons.addItem("-- Chọn chủ đề học --");
        for (Lesson l : new LessonDAO().getAllLessons()) cbLessons.addItem(l);
        
        header.add(lblTitle); header.add(cbLessons); add(header, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridLayout(3, 1)); card.setBackground(Color.WHITE); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2), new EmptyBorder(40, 10, 40, 10)));
        lblWord = new JLabel("Vui lòng chọn chủ đề!", JLabel.CENTER); lblWord.setFont(new Font("Segoe UI", Font.BOLD, 55)); lblWord.setForeground(new Color(37, 99, 235));
        lblPronounce = new JLabel(" ", JLabel.CENTER); lblPronounce.setFont(new Font("Segoe UI", Font.ITALIC, 24));
        lblMeaning = new JLabel(" ", JLabel.CENTER); lblMeaning.setFont(new Font("Segoe UI", Font.BOLD, 30)); lblMeaning.setForeground(new Color(220, 38, 38));
        card.add(lblWord); card.add(lblPronounce); card.add(lblMeaning); add(card, BorderLayout.CENTER);

        JPanel btnWrapper = new JPanel(new BorderLayout()); btnWrapper.setBackground(getBackground()); btnWrapper.setBorder(new EmptyBorder(10, 40, 10, 40));
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 15, 0)); btnPanel.setBackground(getBackground());
        
        JButton btnPrev = UIHelper.createButton(this, "Lùi Lại", "/resources/back.png", new Color(226, 232, 240));
        JButton btnSound = UIHelper.createButton(this, "Nghe", "/resources/loa.png", new Color(253, 230, 138));
        JButton btnTrans = UIHelper.createButton(this, "Dịch", "/resources/dich.png", new Color(191, 219, 254));
        JButton btnNext = UIHelper.createButton(this, "Tiếp Theo", "/resources/next.png", new Color(167, 243, 208));

        btnPanel.add(btnPrev); btnPanel.add(btnSound); btnPanel.add(btnTrans); btnPanel.add(btnNext);
        btnWrapper.add(btnPanel, BorderLayout.CENTER); add(btnWrapper, BorderLayout.SOUTH);

        cbLessons.addActionListener(e -> {
            Object sel = cbLessons.getSelectedItem();
            if(sel instanceof Lesson) {
                words = wordDAO.getWordsByLesson(((Lesson)sel).getId());
                if (!words.isEmpty()) { currentIndex = 0; updateUI(); }
                else { lblWord.setText("Trống"); lblPronounce.setText("Chưa có từ"); lblMeaning.setText(""); words.clear(); }
            } else { words.clear(); lblWord.setText("Vui lòng chọn chủ đề!"); lblPronounce.setText(""); lblMeaning.setText(""); }
        });

        btnNext.addActionListener(e -> { if(!words.isEmpty()) { currentIndex = (currentIndex + 1) % words.size(); updateUI(); }});
        btnPrev.addActionListener(e -> { if(!words.isEmpty()) { currentIndex = (currentIndex - 1 < 0) ? words.size()-1 : currentIndex - 1; updateUI(); }});
        btnTrans.addActionListener(e -> { if(!words.isEmpty()) { Word w = words.get(currentIndex); lblMeaning.setText(w.getMeaning() + (w.getExample().isEmpty() ? "" : " - VD: " + w.getExample())); } });
        btnSound.addActionListener(e -> { if(!words.isEmpty()) JOptionPane.showMessageDialog(this, "Phát âm: " + words.get(currentIndex).getWord()); });
    }

    private void updateUI() {
        if(!words.isEmpty()) {
            Word w = words.get(currentIndex);
            lblWord.setText(w.getWord().isEmpty() ? "Lỗi từ trống" : w.getWord());
            lblPronounce.setText(w.getPronunciation());
            lblMeaning.setText("(? Bấm Dịch)");
            progressDAO.saveLearningProgress(w.getId());
            revalidate(); repaint();
        }
    }
}