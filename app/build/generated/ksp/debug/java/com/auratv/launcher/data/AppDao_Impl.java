package com.auratv.launcher.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.auratv.launcher.model.AppItem;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDao_Impl implements AppDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppItem> __insertionAdapterOfAppItem;

  private final EntityDeletionOrUpdateAdapter<AppItem> __updateAdapterOfAppItem;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByPackage;

  private final SharedSQLiteStatement __preparedStmtOfSetFavorite;

  private final SharedSQLiteStatement __preparedStmtOfSetHidden;

  private final SharedSQLiteStatement __preparedStmtOfSetCustomLabel;

  private final SharedSQLiteStatement __preparedStmtOfSetCustomIcon;

  private final SharedSQLiteStatement __preparedStmtOfRecordLaunch;

  private final SharedSQLiteStatement __preparedStmtOfResetCustomizations;

  public AppDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppItem = new EntityInsertionAdapter<AppItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `app_preferences` (`packageName`,`activityName`,`label`,`customLabel`,`isLeanback`,`bannerPath`,`iconPath`,`customIconPath`,`dominantColor`,`isFavorite`,`favoriteOrder`,`isHidden`,`lastLaunchTime`,`launchCount`,`installTime`,`category`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppItem entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getActivityName());
        statement.bindString(3, entity.getLabel());
        if (entity.getCustomLabel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCustomLabel());
        }
        final int _tmp = entity.isLeanback() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getBannerPath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getBannerPath());
        }
        if (entity.getIconPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getIconPath());
        }
        if (entity.getCustomIconPath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCustomIconPath());
        }
        statement.bindLong(9, entity.getDominantColor());
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getFavoriteOrder());
        final int _tmp_2 = entity.isHidden() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        statement.bindLong(13, entity.getLastLaunchTime());
        statement.bindLong(14, entity.getLaunchCount());
        statement.bindLong(15, entity.getInstallTime());
        statement.bindString(16, entity.getCategory());
      }
    };
    this.__updateAdapterOfAppItem = new EntityDeletionOrUpdateAdapter<AppItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `app_preferences` SET `packageName` = ?,`activityName` = ?,`label` = ?,`customLabel` = ?,`isLeanback` = ?,`bannerPath` = ?,`iconPath` = ?,`customIconPath` = ?,`dominantColor` = ?,`isFavorite` = ?,`favoriteOrder` = ?,`isHidden` = ?,`lastLaunchTime` = ?,`launchCount` = ?,`installTime` = ?,`category` = ? WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppItem entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getActivityName());
        statement.bindString(3, entity.getLabel());
        if (entity.getCustomLabel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCustomLabel());
        }
        final int _tmp = entity.isLeanback() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getBannerPath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getBannerPath());
        }
        if (entity.getIconPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getIconPath());
        }
        if (entity.getCustomIconPath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCustomIconPath());
        }
        statement.bindLong(9, entity.getDominantColor());
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getFavoriteOrder());
        final int _tmp_2 = entity.isHidden() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        statement.bindLong(13, entity.getLastLaunchTime());
        statement.bindLong(14, entity.getLaunchCount());
        statement.bindLong(15, entity.getInstallTime());
        statement.bindString(16, entity.getCategory());
        statement.bindString(17, entity.getPackageName());
      }
    };
    this.__preparedStmtOfDeleteByPackage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM app_preferences WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetFavorite = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_preferences SET isFavorite = ?, favoriteOrder = ? WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetHidden = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_preferences SET isHidden = ? WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetCustomLabel = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_preferences SET customLabel = ? WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetCustomIcon = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_preferences SET customIconPath = ? WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRecordLaunch = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_preferences SET lastLaunchTime = ?, launchCount = launchCount + 1 WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfResetCustomizations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_preferences SET isFavorite = 0, isHidden = 0, favoriteOrder = 2147483647, customLabel = NULL, customIconPath = NULL";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdateAll(final List<AppItem> apps,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppItem.insert(apps);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOrUpdate(final AppItem app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppItem.insert(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AppItem app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAppItem.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByPackage(final String packageName,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByPackage.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByPackage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setFavorite(final String packageName, final boolean isFavorite, final int order,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetFavorite.acquire();
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, order);
        _argIndex = 3;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetFavorite.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setHidden(final String packageName, final boolean isHidden,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetHidden.acquire();
        int _argIndex = 1;
        final int _tmp = isHidden ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetHidden.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setCustomLabel(final String packageName, final String label,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetCustomLabel.acquire();
        int _argIndex = 1;
        if (label == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, label);
        }
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetCustomLabel.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setCustomIcon(final String packageName, final String iconPath,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetCustomIcon.acquire();
        int _argIndex = 1;
        if (iconPath == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, iconPath);
        }
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetCustomIcon.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object recordLaunch(final String packageName, final long launchTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRecordLaunch.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, launchTime);
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRecordLaunch.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resetCustomizations(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResetCustomizations.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfResetCustomizations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AppItem>> getAllVisibleApps() {
    final String _sql = "SELECT * FROM app_preferences WHERE isHidden = 0 ORDER BY label COLLATE NOCASE ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_preferences"}, new Callable<List<AppItem>>() {
      @Override
      @NonNull
      public List<AppItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCustomLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "customLabel");
          final int _cursorIndexOfIsLeanback = CursorUtil.getColumnIndexOrThrow(_cursor, "isLeanback");
          final int _cursorIndexOfBannerPath = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerPath");
          final int _cursorIndexOfIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "iconPath");
          final int _cursorIndexOfCustomIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "customIconPath");
          final int _cursorIndexOfDominantColor = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantColor");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfFavoriteOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "favoriteOrder");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfLastLaunchTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLaunchTime");
          final int _cursorIndexOfLaunchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "launchCount");
          final int _cursorIndexOfInstallTime = CursorUtil.getColumnIndexOrThrow(_cursor, "installTime");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final List<AppItem> _result = new ArrayList<AppItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppItem _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpCustomLabel;
            if (_cursor.isNull(_cursorIndexOfCustomLabel)) {
              _tmpCustomLabel = null;
            } else {
              _tmpCustomLabel = _cursor.getString(_cursorIndexOfCustomLabel);
            }
            final boolean _tmpIsLeanback;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLeanback);
            _tmpIsLeanback = _tmp != 0;
            final String _tmpBannerPath;
            if (_cursor.isNull(_cursorIndexOfBannerPath)) {
              _tmpBannerPath = null;
            } else {
              _tmpBannerPath = _cursor.getString(_cursorIndexOfBannerPath);
            }
            final String _tmpIconPath;
            if (_cursor.isNull(_cursorIndexOfIconPath)) {
              _tmpIconPath = null;
            } else {
              _tmpIconPath = _cursor.getString(_cursorIndexOfIconPath);
            }
            final String _tmpCustomIconPath;
            if (_cursor.isNull(_cursorIndexOfCustomIconPath)) {
              _tmpCustomIconPath = null;
            } else {
              _tmpCustomIconPath = _cursor.getString(_cursorIndexOfCustomIconPath);
            }
            final int _tmpDominantColor;
            _tmpDominantColor = _cursor.getInt(_cursorIndexOfDominantColor);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final int _tmpFavoriteOrder;
            _tmpFavoriteOrder = _cursor.getInt(_cursorIndexOfFavoriteOrder);
            final boolean _tmpIsHidden;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_2 != 0;
            final long _tmpLastLaunchTime;
            _tmpLastLaunchTime = _cursor.getLong(_cursorIndexOfLastLaunchTime);
            final int _tmpLaunchCount;
            _tmpLaunchCount = _cursor.getInt(_cursorIndexOfLaunchCount);
            final long _tmpInstallTime;
            _tmpInstallTime = _cursor.getLong(_cursorIndexOfInstallTime);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item = new AppItem(_tmpPackageName,_tmpActivityName,_tmpLabel,_tmpCustomLabel,_tmpIsLeanback,_tmpBannerPath,_tmpIconPath,_tmpCustomIconPath,_tmpDominantColor,_tmpIsFavorite,_tmpFavoriteOrder,_tmpIsHidden,_tmpLastLaunchTime,_tmpLaunchCount,_tmpInstallTime,_tmpCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AppItem>> getAllApps() {
    final String _sql = "SELECT * FROM app_preferences ORDER BY label COLLATE NOCASE ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_preferences"}, new Callable<List<AppItem>>() {
      @Override
      @NonNull
      public List<AppItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCustomLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "customLabel");
          final int _cursorIndexOfIsLeanback = CursorUtil.getColumnIndexOrThrow(_cursor, "isLeanback");
          final int _cursorIndexOfBannerPath = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerPath");
          final int _cursorIndexOfIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "iconPath");
          final int _cursorIndexOfCustomIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "customIconPath");
          final int _cursorIndexOfDominantColor = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantColor");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfFavoriteOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "favoriteOrder");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfLastLaunchTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLaunchTime");
          final int _cursorIndexOfLaunchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "launchCount");
          final int _cursorIndexOfInstallTime = CursorUtil.getColumnIndexOrThrow(_cursor, "installTime");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final List<AppItem> _result = new ArrayList<AppItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppItem _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpCustomLabel;
            if (_cursor.isNull(_cursorIndexOfCustomLabel)) {
              _tmpCustomLabel = null;
            } else {
              _tmpCustomLabel = _cursor.getString(_cursorIndexOfCustomLabel);
            }
            final boolean _tmpIsLeanback;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLeanback);
            _tmpIsLeanback = _tmp != 0;
            final String _tmpBannerPath;
            if (_cursor.isNull(_cursorIndexOfBannerPath)) {
              _tmpBannerPath = null;
            } else {
              _tmpBannerPath = _cursor.getString(_cursorIndexOfBannerPath);
            }
            final String _tmpIconPath;
            if (_cursor.isNull(_cursorIndexOfIconPath)) {
              _tmpIconPath = null;
            } else {
              _tmpIconPath = _cursor.getString(_cursorIndexOfIconPath);
            }
            final String _tmpCustomIconPath;
            if (_cursor.isNull(_cursorIndexOfCustomIconPath)) {
              _tmpCustomIconPath = null;
            } else {
              _tmpCustomIconPath = _cursor.getString(_cursorIndexOfCustomIconPath);
            }
            final int _tmpDominantColor;
            _tmpDominantColor = _cursor.getInt(_cursorIndexOfDominantColor);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final int _tmpFavoriteOrder;
            _tmpFavoriteOrder = _cursor.getInt(_cursorIndexOfFavoriteOrder);
            final boolean _tmpIsHidden;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_2 != 0;
            final long _tmpLastLaunchTime;
            _tmpLastLaunchTime = _cursor.getLong(_cursorIndexOfLastLaunchTime);
            final int _tmpLaunchCount;
            _tmpLaunchCount = _cursor.getInt(_cursorIndexOfLaunchCount);
            final long _tmpInstallTime;
            _tmpInstallTime = _cursor.getLong(_cursorIndexOfInstallTime);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item = new AppItem(_tmpPackageName,_tmpActivityName,_tmpLabel,_tmpCustomLabel,_tmpIsLeanback,_tmpBannerPath,_tmpIconPath,_tmpCustomIconPath,_tmpDominantColor,_tmpIsFavorite,_tmpFavoriteOrder,_tmpIsHidden,_tmpLastLaunchTime,_tmpLaunchCount,_tmpInstallTime,_tmpCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AppItem>> getFavoriteApps() {
    final String _sql = "SELECT * FROM app_preferences WHERE isFavorite = 1 AND isHidden = 0 ORDER BY favoriteOrder ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_preferences"}, new Callable<List<AppItem>>() {
      @Override
      @NonNull
      public List<AppItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCustomLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "customLabel");
          final int _cursorIndexOfIsLeanback = CursorUtil.getColumnIndexOrThrow(_cursor, "isLeanback");
          final int _cursorIndexOfBannerPath = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerPath");
          final int _cursorIndexOfIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "iconPath");
          final int _cursorIndexOfCustomIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "customIconPath");
          final int _cursorIndexOfDominantColor = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantColor");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfFavoriteOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "favoriteOrder");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfLastLaunchTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLaunchTime");
          final int _cursorIndexOfLaunchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "launchCount");
          final int _cursorIndexOfInstallTime = CursorUtil.getColumnIndexOrThrow(_cursor, "installTime");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final List<AppItem> _result = new ArrayList<AppItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppItem _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpCustomLabel;
            if (_cursor.isNull(_cursorIndexOfCustomLabel)) {
              _tmpCustomLabel = null;
            } else {
              _tmpCustomLabel = _cursor.getString(_cursorIndexOfCustomLabel);
            }
            final boolean _tmpIsLeanback;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLeanback);
            _tmpIsLeanback = _tmp != 0;
            final String _tmpBannerPath;
            if (_cursor.isNull(_cursorIndexOfBannerPath)) {
              _tmpBannerPath = null;
            } else {
              _tmpBannerPath = _cursor.getString(_cursorIndexOfBannerPath);
            }
            final String _tmpIconPath;
            if (_cursor.isNull(_cursorIndexOfIconPath)) {
              _tmpIconPath = null;
            } else {
              _tmpIconPath = _cursor.getString(_cursorIndexOfIconPath);
            }
            final String _tmpCustomIconPath;
            if (_cursor.isNull(_cursorIndexOfCustomIconPath)) {
              _tmpCustomIconPath = null;
            } else {
              _tmpCustomIconPath = _cursor.getString(_cursorIndexOfCustomIconPath);
            }
            final int _tmpDominantColor;
            _tmpDominantColor = _cursor.getInt(_cursorIndexOfDominantColor);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final int _tmpFavoriteOrder;
            _tmpFavoriteOrder = _cursor.getInt(_cursorIndexOfFavoriteOrder);
            final boolean _tmpIsHidden;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_2 != 0;
            final long _tmpLastLaunchTime;
            _tmpLastLaunchTime = _cursor.getLong(_cursorIndexOfLastLaunchTime);
            final int _tmpLaunchCount;
            _tmpLaunchCount = _cursor.getInt(_cursorIndexOfLaunchCount);
            final long _tmpInstallTime;
            _tmpInstallTime = _cursor.getLong(_cursorIndexOfInstallTime);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item = new AppItem(_tmpPackageName,_tmpActivityName,_tmpLabel,_tmpCustomLabel,_tmpIsLeanback,_tmpBannerPath,_tmpIconPath,_tmpCustomIconPath,_tmpDominantColor,_tmpIsFavorite,_tmpFavoriteOrder,_tmpIsHidden,_tmpLastLaunchTime,_tmpLaunchCount,_tmpInstallTime,_tmpCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AppItem>> getRecentApps() {
    final String _sql = "SELECT * FROM app_preferences WHERE lastLaunchTime > 0 AND isHidden = 0 ORDER BY lastLaunchTime DESC LIMIT 15";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_preferences"}, new Callable<List<AppItem>>() {
      @Override
      @NonNull
      public List<AppItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCustomLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "customLabel");
          final int _cursorIndexOfIsLeanback = CursorUtil.getColumnIndexOrThrow(_cursor, "isLeanback");
          final int _cursorIndexOfBannerPath = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerPath");
          final int _cursorIndexOfIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "iconPath");
          final int _cursorIndexOfCustomIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "customIconPath");
          final int _cursorIndexOfDominantColor = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantColor");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfFavoriteOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "favoriteOrder");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfLastLaunchTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLaunchTime");
          final int _cursorIndexOfLaunchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "launchCount");
          final int _cursorIndexOfInstallTime = CursorUtil.getColumnIndexOrThrow(_cursor, "installTime");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final List<AppItem> _result = new ArrayList<AppItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppItem _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpCustomLabel;
            if (_cursor.isNull(_cursorIndexOfCustomLabel)) {
              _tmpCustomLabel = null;
            } else {
              _tmpCustomLabel = _cursor.getString(_cursorIndexOfCustomLabel);
            }
            final boolean _tmpIsLeanback;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLeanback);
            _tmpIsLeanback = _tmp != 0;
            final String _tmpBannerPath;
            if (_cursor.isNull(_cursorIndexOfBannerPath)) {
              _tmpBannerPath = null;
            } else {
              _tmpBannerPath = _cursor.getString(_cursorIndexOfBannerPath);
            }
            final String _tmpIconPath;
            if (_cursor.isNull(_cursorIndexOfIconPath)) {
              _tmpIconPath = null;
            } else {
              _tmpIconPath = _cursor.getString(_cursorIndexOfIconPath);
            }
            final String _tmpCustomIconPath;
            if (_cursor.isNull(_cursorIndexOfCustomIconPath)) {
              _tmpCustomIconPath = null;
            } else {
              _tmpCustomIconPath = _cursor.getString(_cursorIndexOfCustomIconPath);
            }
            final int _tmpDominantColor;
            _tmpDominantColor = _cursor.getInt(_cursorIndexOfDominantColor);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final int _tmpFavoriteOrder;
            _tmpFavoriteOrder = _cursor.getInt(_cursorIndexOfFavoriteOrder);
            final boolean _tmpIsHidden;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_2 != 0;
            final long _tmpLastLaunchTime;
            _tmpLastLaunchTime = _cursor.getLong(_cursorIndexOfLastLaunchTime);
            final int _tmpLaunchCount;
            _tmpLaunchCount = _cursor.getInt(_cursorIndexOfLaunchCount);
            final long _tmpInstallTime;
            _tmpInstallTime = _cursor.getLong(_cursorIndexOfInstallTime);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item = new AppItem(_tmpPackageName,_tmpActivityName,_tmpLabel,_tmpCustomLabel,_tmpIsLeanback,_tmpBannerPath,_tmpIconPath,_tmpCustomIconPath,_tmpDominantColor,_tmpIsFavorite,_tmpFavoriteOrder,_tmpIsHidden,_tmpLastLaunchTime,_tmpLaunchCount,_tmpInstallTime,_tmpCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getApp(final String packageName, final Continuation<? super AppItem> $completion) {
    final String _sql = "SELECT * FROM app_preferences WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AppItem>() {
      @Override
      @Nullable
      public AppItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCustomLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "customLabel");
          final int _cursorIndexOfIsLeanback = CursorUtil.getColumnIndexOrThrow(_cursor, "isLeanback");
          final int _cursorIndexOfBannerPath = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerPath");
          final int _cursorIndexOfIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "iconPath");
          final int _cursorIndexOfCustomIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "customIconPath");
          final int _cursorIndexOfDominantColor = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantColor");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfFavoriteOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "favoriteOrder");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfLastLaunchTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLaunchTime");
          final int _cursorIndexOfLaunchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "launchCount");
          final int _cursorIndexOfInstallTime = CursorUtil.getColumnIndexOrThrow(_cursor, "installTime");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final AppItem _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpCustomLabel;
            if (_cursor.isNull(_cursorIndexOfCustomLabel)) {
              _tmpCustomLabel = null;
            } else {
              _tmpCustomLabel = _cursor.getString(_cursorIndexOfCustomLabel);
            }
            final boolean _tmpIsLeanback;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLeanback);
            _tmpIsLeanback = _tmp != 0;
            final String _tmpBannerPath;
            if (_cursor.isNull(_cursorIndexOfBannerPath)) {
              _tmpBannerPath = null;
            } else {
              _tmpBannerPath = _cursor.getString(_cursorIndexOfBannerPath);
            }
            final String _tmpIconPath;
            if (_cursor.isNull(_cursorIndexOfIconPath)) {
              _tmpIconPath = null;
            } else {
              _tmpIconPath = _cursor.getString(_cursorIndexOfIconPath);
            }
            final String _tmpCustomIconPath;
            if (_cursor.isNull(_cursorIndexOfCustomIconPath)) {
              _tmpCustomIconPath = null;
            } else {
              _tmpCustomIconPath = _cursor.getString(_cursorIndexOfCustomIconPath);
            }
            final int _tmpDominantColor;
            _tmpDominantColor = _cursor.getInt(_cursorIndexOfDominantColor);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final int _tmpFavoriteOrder;
            _tmpFavoriteOrder = _cursor.getInt(_cursorIndexOfFavoriteOrder);
            final boolean _tmpIsHidden;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_2 != 0;
            final long _tmpLastLaunchTime;
            _tmpLastLaunchTime = _cursor.getLong(_cursorIndexOfLastLaunchTime);
            final int _tmpLaunchCount;
            _tmpLaunchCount = _cursor.getInt(_cursorIndexOfLaunchCount);
            final long _tmpInstallTime;
            _tmpInstallTime = _cursor.getLong(_cursorIndexOfInstallTime);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _result = new AppItem(_tmpPackageName,_tmpActivityName,_tmpLabel,_tmpCustomLabel,_tmpIsLeanback,_tmpBannerPath,_tmpIconPath,_tmpCustomIconPath,_tmpDominantColor,_tmpIsFavorite,_tmpFavoriteOrder,_tmpIsHidden,_tmpLastLaunchTime,_tmpLaunchCount,_tmpInstallTime,_tmpCategory);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllAppsSnapshot(final Continuation<? super List<AppItem>> $completion) {
    final String _sql = "SELECT * FROM app_preferences";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AppItem>>() {
      @Override
      @NonNull
      public List<AppItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCustomLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "customLabel");
          final int _cursorIndexOfIsLeanback = CursorUtil.getColumnIndexOrThrow(_cursor, "isLeanback");
          final int _cursorIndexOfBannerPath = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerPath");
          final int _cursorIndexOfIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "iconPath");
          final int _cursorIndexOfCustomIconPath = CursorUtil.getColumnIndexOrThrow(_cursor, "customIconPath");
          final int _cursorIndexOfDominantColor = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantColor");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfFavoriteOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "favoriteOrder");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfLastLaunchTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLaunchTime");
          final int _cursorIndexOfLaunchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "launchCount");
          final int _cursorIndexOfInstallTime = CursorUtil.getColumnIndexOrThrow(_cursor, "installTime");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final List<AppItem> _result = new ArrayList<AppItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppItem _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpCustomLabel;
            if (_cursor.isNull(_cursorIndexOfCustomLabel)) {
              _tmpCustomLabel = null;
            } else {
              _tmpCustomLabel = _cursor.getString(_cursorIndexOfCustomLabel);
            }
            final boolean _tmpIsLeanback;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLeanback);
            _tmpIsLeanback = _tmp != 0;
            final String _tmpBannerPath;
            if (_cursor.isNull(_cursorIndexOfBannerPath)) {
              _tmpBannerPath = null;
            } else {
              _tmpBannerPath = _cursor.getString(_cursorIndexOfBannerPath);
            }
            final String _tmpIconPath;
            if (_cursor.isNull(_cursorIndexOfIconPath)) {
              _tmpIconPath = null;
            } else {
              _tmpIconPath = _cursor.getString(_cursorIndexOfIconPath);
            }
            final String _tmpCustomIconPath;
            if (_cursor.isNull(_cursorIndexOfCustomIconPath)) {
              _tmpCustomIconPath = null;
            } else {
              _tmpCustomIconPath = _cursor.getString(_cursorIndexOfCustomIconPath);
            }
            final int _tmpDominantColor;
            _tmpDominantColor = _cursor.getInt(_cursorIndexOfDominantColor);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final int _tmpFavoriteOrder;
            _tmpFavoriteOrder = _cursor.getInt(_cursorIndexOfFavoriteOrder);
            final boolean _tmpIsHidden;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_2 != 0;
            final long _tmpLastLaunchTime;
            _tmpLastLaunchTime = _cursor.getLong(_cursorIndexOfLastLaunchTime);
            final int _tmpLaunchCount;
            _tmpLaunchCount = _cursor.getInt(_cursorIndexOfLaunchCount);
            final long _tmpInstallTime;
            _tmpInstallTime = _cursor.getLong(_cursorIndexOfInstallTime);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item = new AppItem(_tmpPackageName,_tmpActivityName,_tmpLabel,_tmpCustomLabel,_tmpIsLeanback,_tmpBannerPath,_tmpIconPath,_tmpCustomIconPath,_tmpDominantColor,_tmpIsFavorite,_tmpFavoriteOrder,_tmpIsHidden,_tmpLastLaunchTime,_tmpLaunchCount,_tmpInstallTime,_tmpCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
