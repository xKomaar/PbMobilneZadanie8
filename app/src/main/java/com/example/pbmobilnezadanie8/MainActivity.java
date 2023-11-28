package com.example.pbmobilnezadanie8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pbmobilnezadanie8.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Book editedBook = null;
    private BookViewModel bookViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.findAll().observe(this, adapter::setBooks);

        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK) {
            Book book = new Book(data.getStringExtra("EDIT_BOOK_TITLE"), data.getStringExtra("EDIT_BOOK_AUTHOR"));
            bookViewModel.insert(book);
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_added),
                    Snackbar.LENGTH_LONG).show();
        } else if (requestCode == 2) {
            editedBook.setTitle(data.getStringExtra("EDIT_BOOK_TITLE"));
            editedBook.setAuthor(data.getStringExtra("EDIT_BOOK_AUTHOR"));
            bookViewModel.update(editedBook);
            editedBook = null;
            Snackbar.make(findViewById(R.id.coordinator_layout),
                            getString(R.string.book_edited),
                            Snackbar.LENGTH_LONG)
                    .show();
        } else {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.empty_not_saved),
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private class BookHolder extends RecyclerView.ViewHolder {

        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private Book book;
        boolean swiped = false;
        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));

            itemView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
                @Override
                public void onSwipeRight() {
                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_archived),
                            Snackbar.LENGTH_LONG).show();
                    swiped = true;
                }
            });

            itemView.setOnClickListener(view -> {
                MainActivity.this.editedBook = this.book;
                Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
                intent.putExtra("EDIT_BOOK_TITLE", book.getTitle());
                intent.putExtra("EDIT_BOOK_AUTHOR", book.getAuthor());
                startActivityForResult(intent, 2);
            });

            itemView.setOnLongClickListener(view -> {
                if(!swiped) {
                    MainActivity.this.bookViewModel.delete(this.book);
                } else {
                    swiped = false;
                }
                return true;
            });

            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author);
        }

        public void bind(Book book) {
            this.book = book;
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());
        }

        public class OnSwipeTouchListener implements View.OnTouchListener {

            private final GestureDetector gestureDetector;

            public OnSwipeTouchListener(Context context) {
                gestureDetector = new GestureDetector(context, new GestureListener());
            }

            public void onSwipeLeft() {
            }

            public void onSwipeRight() {
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

            private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

                private static final int SWIPE_DISTANCE_THRESHOLD = 100;
                private static final int SWIPE_VELOCITY_THRESHOLD = 100;

                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float distanceX = e2.getX() - e1.getX();
                    float distanceY = e2.getY() - e1.getY();
                    if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (distanceX > 0)
                            onSwipeRight();
                        else
                            onSwipeLeft();
                        return true;
                    }
                    return false;
                }
            }
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                Book book = books.get(position);
                holder.bind(book);
            } else {
                Log.d("MainActivity", "No books");
            }
        }

        @Override
        public int getItemCount() {
            if(books != null) {
                return books.size();
            } else {
                return 0;
            }
        }

        void setBooks(List<Book> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }
}