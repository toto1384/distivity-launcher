package com.distivity.productivitylauncher.Database;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.distivity.productivitylauncher.Pojos.Todo;
import com.distivity.productivitylauncher.Pojos.TreeNode;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Dao_Impl extends Dao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfTodo;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfTodo;

  public Dao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTodo = new EntityInsertionAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `todos`(`id`,`name`,`checked`,`parrentId`,`isForToday`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        final int _tmp;
        _tmp = value.isChecked() ? 1 : 0;
        stmt.bindLong(3, _tmp);
        stmt.bindLong(4, value.getParrentId());
        final int _tmp_1;
        _tmp_1 = value.isForToday() ? 1 : 0;
        stmt.bindLong(5, _tmp_1);
      }
    };
    this.__deletionAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `todos` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.getId());
      }
    };
  }

  @Override
  void insertTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTodo.insert(todo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  void deleteTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfTodo.handle(todo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  List<TreeNode> loadSortedTodos(final boolean forToday) {
    __db.beginTransaction();
    try {
      List<TreeNode> _result = Dao_Impl.super.loadSortedTodos(forToday);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  void updateDatabase(final List<Todo> todoToAddForLater, final List<Todo> todosToUpdate,
      final List<Todo> todosToDelete, final Runnable toRunOnFinish, final Runnable toRunOnFinish1) {
    __db.beginTransaction();
    try {
      Dao_Impl.super.updateDatabase(todoToAddForLater, todosToUpdate, todosToDelete, toRunOnFinish, toRunOnFinish1);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  List<Todo> getTodosWithParrentId(final int parrentId, final boolean forToday) {
    final String _sql = "SELECT * FROM todos WHERE parrentId=? AND isForToday=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, parrentId);
    _argIndex = 2;
    final int _tmp;
    _tmp = forToday ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "checked");
      final int _cursorIndexOfParrentId = CursorUtil.getColumnIndexOrThrow(_cursor, "parrentId");
      final int _cursorIndexOfIsForToday = CursorUtil.getColumnIndexOrThrow(_cursor, "isForToday");
      final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Todo _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final boolean _tmpChecked;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfChecked);
        _tmpChecked = _tmp_1 != 0;
        final int _tmpParrentId;
        _tmpParrentId = _cursor.getInt(_cursorIndexOfParrentId);
        final boolean _tmpIsForToday;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsForToday);
        _tmpIsForToday = _tmp_2 != 0;
        _item = new Todo(_tmpId,_tmpName,_tmpChecked,_tmpParrentId,_tmpIsForToday);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
