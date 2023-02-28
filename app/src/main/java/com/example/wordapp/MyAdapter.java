package com.example.wordapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends ListAdapter<Word, MyAdapter.MyViewHolder> {     //要重写三个函数 和 新建一个自己的viewholder类 （第二步）

    boolean useCardView;
    WordViewModel wordViewModel;

    public MyAdapter(boolean useCardView, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {

                return (oldItem.getWord().equals(newItem.getWord())
                        && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                        && oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  //将 View holder 设置为 自己创建的 MyViewHolder 类
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView) { //设置视图单位
            itemView = layoutInflater.inflate(R.layout.cell_card_2, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal_2, parent, false);
        }

        final MyViewHolder holder = new MyViewHolder(itemView);
        holder.itemView.setOnClickListener(view -> {
            Uri uri = Uri.parse("https://fanyi.baidu.com/#en/zh/" + holder.textViewEnglish.getText());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            holder.itemView.getContext().startActivity(intent);
        });

        holder.aSwitchChineseInvisible.setOnCheckedChangeListener((compoundButton, b) -> {
            Word word = (Word) holder.itemView.getTag(R.id.word_for_view_holder);
            if (b) {
                //holder.textViewChinese.setVisibility(View.GONE);//视图更新
                holder.textViewChinese.setVisibility(View.VISIBLE);
                word.setChineseInvisible(true);  //底层数据更新   （但由于Livedata的观察者也会实时刷新数据，所以要在activity里判断)
                wordViewModel.updateWords(word);   //修改数据库
            } else {
                //holder.textViewChinese.setVisibility(View.VISIBLE);
                holder.textViewChinese.setVisibility(View.GONE);
                word.setChineseInvisible(false);
                wordViewModel.updateWords(word);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {   //将holder里的item元素 绑定 值
        Word word = getItem(position);
        holder.itemView.setTag(R.id.word_for_view_holder, word);//将word传给上面onCreateViewHolder方法里

        holder.textViewNumber.setText(String.valueOf(position + 1));  //position是从0开始
        holder.textViewChinese.setText(String.valueOf(word.getChineseMeaning()));
        holder.textViewEnglish.setText(String.valueOf(word.getWord()));
        if (word.isChineseInvisible()) {
            //holder.textViewChinese.setVisibility(View.GONE);  //GONE会连位置都让出来
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseInvisible.setChecked(true);
        } else {
           // holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitchChineseInvisible.setChecked(false);
        }


        /*
         * 将之前在这里的监听器放到上面onCreateViewHolder方法里。这样避免多次创建
         */

    }


    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.textViewNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));

    }

    //内部类加static防止内存泄漏
    static class MyViewHolder extends RecyclerView.ViewHolder {   //  自定义创建自己的 view holder     （第一步）
        TextView textViewNumber, textViewEnglish, textViewChinese;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch aSwitchChineseInvisible;

        public MyViewHolder(@NonNull View itemView) {    //绑定视图
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            aSwitchChineseInvisible = itemView.findViewById(R.id.switch2);
        }
    }
}
