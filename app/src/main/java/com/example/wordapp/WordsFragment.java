package com.example.wordapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;


public class WordsFragment extends Fragment {

    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter1, myAdapter2;

    private LiveData<List<Word>> filterWords;

    private List<Word> allwords;

    private DividerItemDecoration dividerItemDecoration;

    private static final String VIEW_TYPE = "view_type";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";

    public WordsFragment() {
        setHasOptionsMenu(true);  //激活右上角菜单
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();  //设置搜索
        int width = getResources().getDisplayMetrics().widthPixels;   //找到屏幕宽度
        searchView.setMaxWidth((int) (0.6 * width));   //设置搜索框展开宽度
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {  //搜索时改变
                String patten = s.trim();
                filterWords.removeObservers(getViewLifecycleOwner());
                filterWords = wordViewModel.findWordWithPatten(patten);
                filterWords.observe(getViewLifecycleOwner(), words -> {
                    int temp = myAdapter1.getItemCount();
                    allwords=words;
                    if (temp != words.size()) {
                        myAdapter1.submitList(words);
                        myAdapter2.submitList(words);
                    }
                });
                return true;
            }
        });
    }//创建右上角菜单，实现搜索功能

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_data:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", (dialogInterface, i) -> wordViewModel.clearWords());
                builder.setNegativeButton("取消", (dialogInterface, i) -> {
                    //什么都不做
                });
                builder.create().show();
                break;
            case R.id.switch_view:
                SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
                SharedPreferences.Editor editor = shp.edit();
                if (viewType) {
                    recyclerView.setAdapter(myAdapter1);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, false);
                } else {
                    recyclerView.setAdapter(myAdapter2);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, true);
                }
                editor.apply();
                break;


        }
        return super.onOptionsItemSelected(item);
    }//设置实现右上角菜单选项选中时的功能

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 选择创建布局
        return inflater.inflate(R.layout.fragment_words, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        wordViewModel = new ViewModelProvider(requireActivity()).get(WordViewModel.class);   //创建view model 获取数据库数据
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));    //布置recycle view
        myAdapter1 = new MyAdapter(false, wordViewModel);                   //创建 适配器 并将 view model的数据传入
        myAdapter2 = new MyAdapter(true, wordViewModel);

        SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
        dividerItemDecoration=new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        if (viewType) {
            recyclerView.setAdapter(myAdapter2);
        } else {
            recyclerView.setAdapter(myAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }


        recyclerView.setItemAnimator(new DefaultItemAnimator() {  //手动设置，序号改变
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {//动画结束之后，判断，并刷新显示在界面的item的holder的序号
                super.onAnimationFinished(viewHolder);                                                               //（可节省资源）
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastposition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition; i <= lastposition; i++) {
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.textViewNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }
            }
        });


        filterWords = wordViewModel.getAllWordsLive();//一开始将过滤单词设为全体单词
        //设置观察者，观察数据变化
        filterWords.observe(getViewLifecycleOwner(), words -> {
            int temp = myAdapter1.getItemCount();
            allwords=words;
            if (temp != words.size()) {
                recyclerView.smoothScrollBy(0, -200);
                myAdapter1.submitList(words);
                myAdapter2.submitList(words);
            }
        }); //设置观察者，观察数据变化

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Word wordToDelete =allwords.get(viewHolder.getAdapterPosition()); //Livedata的get会告警,所以用allwords存储
                wordViewModel.deleteWords(wordToDelete);
                Snackbar.make(requireActivity().findViewById(R.id.wordfragmentview),"删除了一个单词",Snackbar.LENGTH_SHORT)
                        .setAction("撤销", view1 -> wordViewModel.insertWords(wordToDelete)).show();

            }
        }).attachToRecyclerView(recyclerView);//左右滑动删除单词

        FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);  //左下角悬浮按钮创建和设置监听
        //切换界面
        floatingActionButton.setOnClickListener(view12 -> {
            NavController controller = Navigation.findNavController(view12);
            controller.navigate(R.id.action_wordsFragment_to_addFragment);
        });

    }

    @Override
    public void onResume() {  //每次切换到word界面就会隐藏键盘

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Objects.requireNonNull(getView()).getWindowToken(), 0);
        //getView()返回fragment容器
        super.onResume();
    }//每次切换到word界面就会隐藏键盘


}