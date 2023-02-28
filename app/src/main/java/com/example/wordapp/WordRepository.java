package com.example.wordapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
    private final LiveData<List<Word>> allWordsLive;
    private final WordDao wordDao;

    public LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }

    public LiveData<List<Word>> findWordsWithPatten(String patten) {
        return wordDao.findWordWithPatten("%" + patten + "%");  //模糊查询
    }

    public WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());  //单例模式获取database
        wordDao = wordDatabase.getWordDao();//获取dao，用来操作数据库    在副线程下操作数据库的Async方法在下面
        allWordsLive = wordDao.getAllLiveWord(); //因为livedata原因，该操作是在副线程上执行，所以不用Async   //通过dao将数据全部传到 livedata 里*/
    }


    //下面四个接口是用来在view model里操作用的
    void insertWords(Word... words) {
        new InsertAsyncTask(wordDao).execute(words);  //通过Async来保证数据库的增删改查是在副进程中
    }

    void deleteWords(Word... words) {
        new DeletetAsyncTask(wordDao).execute(words);
    }

    void clearWords() {
        new DeleteAllAsyncTask(wordDao).execute();
    }

    void updateWords(Word... words) {
        new UpdateAsyncTask(wordDao).execute(words);

    }

    //这底下的方法 用于在副线程下执行数据库操作，从而避免
    static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {
        private final WordDao wordDao;

        public InsertAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWord(words);
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<Word, Void, Void> {
        private final WordDao wordDao;

        public UpdateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWord(words);
            return null;
        }
    }

    static class DeletetAsyncTask extends AsyncTask<Word, Void, Void> {
        private final WordDao wordDao;

        public DeletetAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WordDao wordDao;

        public DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}
