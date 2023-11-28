package com.example.pbmobilnezadanie8;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class BookRepository {
    private final BookDAO bookDAO;
    private final LiveData<List<Book>> books;

    BookRepository(Application application) {
        BookDatabase database = BookDatabase.getDatabase(application);
        bookDAO = database.bookDAO();
        books = bookDAO.findAll();
    }

    LiveData<List<Book>> findAllBooks() {
        return books;
    }

    void insert(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDAO.insert(book));
    }

    void update(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDAO.update(book));
    }

    void delete(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDAO.delete(book));
    }
}
