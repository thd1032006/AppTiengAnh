package view;

import javax.swing.*;
import javax.swing.border.*;
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
    private int userId;

    private JLabel lblWord, lblPronounce, lblMeaning, lblCounter;
    private JComboBox<Object> cbLessons;

    private final WordDAO wordDAO = new WordDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();

    public LearningPanel() {
        this(1);
    }

    public LearningPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.BG_PAGE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(UIHelper.BG_PAGE);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel lblTitle = new JLabel("Học từ vựng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(UIHelper.TEXT_PRIMARY);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterRow.setBackground(UIHelper.BG_PAGE);

        JLabel lblPick = new JLabel("Chủ đề:");
        lblPick.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPick.setForeground(UIHelper.TEXT_SECOND);

        cbLessons = new JComboBox<>();
        cbLessons.setPreferredSize(new Dimension(240, 38));
        UIHelper.styleComboBox(cbLessons);
        cbLessons.addItem("-- Chọn chủ đề --");
        for (Lesson l : new LessonDAO().getAllLessons()) cbLessons.addItem(l);

        filterRow.add(lblPick);
        filterRow.add(cbLessons);
        header.add(lblTitle, BorderLayout.WEST);
        header.add(filterRow, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel flashCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.setColor(UIHelper.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.dispose();
            }
        };
        flashCard.setOpaque(false);
        flashCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 8, 0);
        lblCounter = new JLabel("0 / 0");
        lblCounter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCounter.setForeground(UIHelper.TEXT_SECOND);
        flashCard.add(lblCounter, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 12, 0);
        lblWord = new JLabel("Chọn chủ đề để bắt đầu", JLabel.CENTER);
        lblWord.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lblWord.setForeground(UIHelper.ACCENT_BLUE);
        flashCard.add(lblWord, gc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 16, 0);
        lblPronounce = new JLabel(" ", JLabel.CENTER);
        lblPronounce.setFont(new Font("Segoe UI", Font.ITALIC, 22));
        lblPronounce.setForeground(UIHelper.TEXT_SECOND);
        flashCard.add(lblPronounce, gc);

        gc.gridy = 3; gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(0, 0, 16, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(UIHelper.BORDER_COLOR);
        sep.setPreferredSize(new Dimension(300, 1));
        flashCard.add(sep, gc);

        gc.gridy = 4; gc.fill = GridBagConstraints.NONE; gc.insets = new Insets(0, 0, 0, 0);
        lblMeaning = new JLabel("Bấm Dịch để xem nghĩa", JLabel.CENTER);
        lblMeaning.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMeaning.setForeground(UIHelper.TEXT_SECOND);
        flashCard.add(lblMeaning, gc);

        add(flashCard, BorderLayout.CENTER);

        JPanel btnArea = new JPanel(new GridLayout(1, 4, 12, 0));
        btnArea.setBackground(UIHelper.BG_PAGE);
        btnArea.setBorder(new EmptyBorder(24, 0, 0, 0));

        JButton btnPrev  = makeBtn("Lùi lại",    new Color(226, 232, 240), UIHelper.TEXT_PRIMARY);
        JButton btnTrans = makeBtn("Dịch nghĩa", new Color(219, 234, 254), new Color(29, 78, 216));
        JButton btnSound = makeBtn("Phát âm",    new Color(237, 233, 254), new Color(91, 33, 182));
        JButton btnNext  = makeBtn("Tiếp theo",  new Color(209, 250, 229), new Color(6, 95, 70));

        btnArea.add(btnPrev);
        btnArea.add(btnTrans);
        btnArea.add(btnSound);
        btnArea.add(btnNext);
        add(btnArea, BorderLayout.SOUTH);

        cbLessons.addActionListener(e -> {
            Object sel = cbLessons.getSelectedItem();
            if (sel instanceof Lesson) {
                words = wordDAO.getWordsByLesson(((Lesson) sel).getId());
                if (!words.isEmpty()) {
                    currentIndex = 0;
                    refreshCard();
                } else {
                    words.clear();
                    lblWord.setText("Chưa có từ vựng");
                    lblPronounce.setText("");
                    lblMeaning.setText("Chủ đề này chưa có dữ liệu");
                    lblCounter.setText("0 / 0");
                }
            } else {
                words.clear();
                lblWord.setText("Chọn chủ đề để bắt đầu");
                lblPronounce.setText(" ");
                lblMeaning.setText("Bấm Dịch để xem nghĩa");
                lblCounter.setText("0 / 0");
            }
        });

        btnNext.addActionListener(e -> {
            if (!words.isEmpty()) {
                currentIndex = (currentIndex + 1) % words.size();
                refreshCard();
            }
        });
        btnPrev.addActionListener(e -> {
            if (!words.isEmpty()) {
                currentIndex = (currentIndex - 1 + words.size()) % words.size();
                refreshCard();
            }
        });
        btnSound.addActionListener(e -> {
            if (!words.isEmpty()) {
                String wordToSpeak = words.get(currentIndex).getWord();
                new Thread(() -> {
                    try {
                        String safeWord = wordToSpeak.replace("'", "''");
                        String[] cmdArr = {
                            "PowerShell", "-Command",
                            "Add-Type -AssemblyName System.Speech; " +
                            "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + safeWord + "');"
                        };
                        new ProcessBuilder(cmdArr).start();
                    } catch (Exception ex) {
                        System.out.println("Lỗi phát âm: " + ex.getMessage());
                    }
                }).start();
            }
        });
        btnTrans.addActionListener(e -> {
            if (!words.isEmpty()) {
                Word w = words.get(currentIndex);
                String meaning = w.getMeaning() != null ? w.getMeaning() : "(không có nghĩa)";
                String ex = (w.getExample() != null && !w.getExample().trim().isEmpty())
                        ? "<br><span style='color:#64748b;font-size:14px'>VD: " + w.getExample() + "</span>"
                        : "";
                lblMeaning.setText("<html><div style='text-align:center'>" + meaning + ex + "</div></html>");
                lblMeaning.setForeground(UIHelper.ACCENT_RED);
            }
        });
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void refreshCard() {
        if (words.isEmpty()) return;
        Word w = words.get(currentIndex);
        lblWord.setText((w.getWord() != null && !w.getWord().trim().isEmpty()) ? w.getWord() : "(lỗi dữ liệu)");
        lblWord.setForeground(UIHelper.ACCENT_BLUE);
        lblPronounce.setText(w.getPronunciation() != null ? w.getPronunciation() : "");
        lblMeaning.setText("Bấm Dịch để xem nghĩa");
        lblMeaning.setForeground(UIHelper.TEXT_SECOND);
        lblCounter.setText((currentIndex + 1) + " / " + words.size());
        try {
            progressDAO.saveLearningProgress(userId, w.getId());
        } catch (Exception ignored) {}
        revalidate();
        repaint();
    }

    public void reloadLessons() {
        Object selected = cbLessons.getSelectedItem();
        cbLessons.removeAllItems();
        cbLessons.addItem("-- Chọn chủ đề --");
        for (Lesson l : new LessonDAO().getAllLessons()) cbLessons.addItem(l);
        if (selected instanceof Lesson selLesson) {
            for (int i = 0; i < cbLessons.getItemCount(); i++) {
                Object item = cbLessons.getItemAt(i);
                if (item instanceof Lesson l && l.getId() == selLesson.getId()) {
                    cbLessons.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}
