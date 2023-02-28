package com.example.wordapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


//singleton 单例  只能生成一个对象， 数据库最好在app运行时只生成一个对象，
@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;          //上下文，表示运行环境
    //      不会同时创建
    static synchronized WordDatabase getDatabase(Context context){
        if(INSTANCE==null){                                                                         //数据库名称
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"WordDataBase1")
                    //.fallbackToDestructiveMigration()  数据库迁移时会销毁旧文件，一般不会使用
                    //.addMigrations(MIGRATION_3_4)       //数据库字段修改时使用
                    .build();

        }
        return INSTANCE;
    }

    public abstract WordDao getWordDao();  //从database里获取dao 用来操作数据库

    static final Migration MIGRATION_1_2 =new Migration(1,2) {   //在表里增加一列
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN foo_data INTEGER NOT NULL DEFAULT 1");
        }
    };

    static final Migration MIGRATION_2_3 =new Migration(2,3) {   //表里删除列  ，  比较麻烦，只能创建新表，把旧表所需字段复制过去
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE word_temp(id INTEGER PRIMARY KEY NOT NULL ,english_word TEXT," +
                    "chinese_word TEXT)");   //字符串在sql里边是TEXT文件，不是string
            database.execSQL("insert into word_temp (id,english_word,chinese_word)" +
                    "select id,english_word,chinese_word From word");
            database.execSQL("drop table word");
            database.execSQL("alter table word_temp rename to word");
        }
    };

    static final Migration MIGRATION_3_4 =new Migration(3,4) {   //在表里增加一列
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_invisible INTEGER NOT NULL DEFAULT 0");
        }
    };
}
