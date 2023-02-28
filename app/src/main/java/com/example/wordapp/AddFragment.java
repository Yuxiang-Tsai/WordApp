package com.example.wordapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class AddFragment extends Fragment {

    private Button buttonSubmit;
    private EditText editTextChinese, editTextEnglish;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WordViewModel wordViewModel;

        FragmentActivity activity = requireActivity();
        wordViewModel = new ViewModelProvider(activity).get(WordViewModel.class);
        editTextChinese = activity.findViewById(R.id.editTextChinese);
        editTextEnglish = activity.findViewById(R.id.editTextEnglish);
        buttonSubmit = activity.findViewById(R.id.buttonSubmit);
        buttonSubmit.setEnabled(false);
        editTextEnglish.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextEnglish, 0);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                buttonSubmit.setEnabled(!english.isEmpty() && !chinese.isEmpty());  //只有全填了之后，才能提交
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        editTextEnglish.addTextChangedListener(textWatcher);
        editTextChinese.addTextChangedListener(textWatcher);

        buttonSubmit.setOnClickListener(view1 -> {
            String english = editTextEnglish.getText().toString().trim();
            String chinese = editTextChinese.getText().toString().trim();
            Word word = new Word(english, chinese);
            wordViewModel.insertWords(word);
            NavController controller = Navigation.findNavController(view1);//提交后返回上一个界面
            controller.navigateUp();
        });


    }
}