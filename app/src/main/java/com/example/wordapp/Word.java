package com.example.wordapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "english_word")
    private String word;
    @ColumnInfo(name="chinese_word")
    private String chineseMeaning;

    @ColumnInfo(name = "chinese_invisible")
    private boolean ChineseInvisible;

    public boolean isChineseInvisible() {
        return ChineseInvisible;
    }

    public void setChineseInvisible(boolean chineseInvisible) {
        ChineseInvisible = chineseInvisible;
    }
    /*    @ColumnInfo(name = "foo_data")
    private boolean foo;

    public boolean isFoo() {
        return foo;
    }

    public void setFoo(boolean foo) {
        this.foo = foo;
    }*/

    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}
