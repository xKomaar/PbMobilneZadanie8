package com.example.pbmobilnezadanie8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pbmobilnezadanie8.R;

public class EditBookActivity extends AppCompatActivity {

    private EditText editTitleEditText;
    private EditText editAuthorEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        editTitleEditText = findViewById(R.id.edit_book_title);
        editAuthorEditText = findViewById(R.id.edit_book_author);

        if (getIntent().hasExtra("EDIT_BOOK_TITLE"))
            editTitleEditText.setText(getIntent().getStringExtra("EDIT_BOOK_TITLE"));
        if (getIntent().hasExtra("EDIT_BOOK_AUTHOR"))
            editAuthorEditText.setText(getIntent().getStringExtra("EDIT_BOOK_AUTHOR"));

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent intent = new Intent();
            if (TextUtils.isEmpty(editTitleEditText.getText())
                    || TextUtils.isEmpty(editAuthorEditText.getText()))
                setResult(RESULT_CANCELED, intent);
            else {
                String title = editTitleEditText.getText().toString();
                intent.putExtra("EDIT_BOOK_TITLE", title);
                String author = editAuthorEditText.getText().toString();
                intent.putExtra("EDIT_BOOK_AUTHOR", author);
                setResult(RESULT_OK, intent);
            }
            finish();
        });
    }
}