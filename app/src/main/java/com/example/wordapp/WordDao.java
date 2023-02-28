package com.example.wordapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao                            //Dao database access object
public interface WordDao {
    @Insert
    void insertWord(Word... words);

    @Update
    void updateWord(Word... words);

    @Delete
    void deleteWords(Word... words);

    @Query("DELETE FROM Word")
    void deleteAllWords();

    @Query("SELECT * FROM WORD ORDER BY ID DESC")
    LiveData<List<Word>> getAllLiveWord();      //因为livedata原因，该操作是在副线程上执行，所以不用Async

    @Query("SELECT * FROM WORD WHERE english_word LIKE :patten ORDER BY ID DESC")
    LiveData<List<Word>> findWordWithPatten(String patten);
}
