package model;

public class UserProgress {
    private int userId;
    private int vocabularyId;
    private int isMastered;
    private int isFavorite;

    public UserProgress() {}
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getVocabularyId() { return vocabularyId; }
    public void setVocabularyId(int vocabularyId) { this.vocabularyId = vocabularyId; }
    public int getIsMastered() { return isMastered; }
    public void setIsMastered(int isMastered) { this.isMastered = isMastered; }
    public int getIsFavorite() { return isFavorite; }
    public void setIsFavorite(int isFavorite) { this.isFavorite = isFavorite; }
}