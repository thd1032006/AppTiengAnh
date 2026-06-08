package model;

public class Word {
    private int id;
    private String word;
    private String type;
    private String meaning;
    private String pronunciation;
    private String example;
    private int lessonId;

    public Word() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getWord()                     { return word; }
    public void setWord(String word)            { this.word = word; }
    public String getType()                     { return type; }
    public void setType(String type)            { this.type = type; }
    public String getMeaning()                  { return meaning; }
    public void setMeaning(String meaning)      { this.meaning = meaning; }
    public String getPronunciation()            { return pronunciation; }
    public void setPronunciation(String p)      { this.pronunciation = p; }
    public String getExample()                  { return example; }
    public void setExample(String example)      { this.example = example; }
    public int getLessonId()                    { return lessonId; }
    public void setLessonId(int lessonId)       { this.lessonId = lessonId; }
}
