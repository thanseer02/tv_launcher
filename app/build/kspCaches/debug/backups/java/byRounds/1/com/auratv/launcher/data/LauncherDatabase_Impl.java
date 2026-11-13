package com.auratv.launcher.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LauncherDatabase_Impl extends LauncherDatabase {
  private volatile AppDao _appDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `app_preferences` (`packageName` TEXT NOT NULL, `activityName` TEXT NOT NULL, `label` TEXT NOT NULL, `customLabel` TEXT, `isLeanback` INTEGER NOT NULL, `bannerPath` TEXT, `iconPath` TEXT, `customIconPath` TEXT, `dominantColor` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL, `favoriteOrder` INTEGER NOT NULL, `isHidden` INTEGER NOT NULL, `lastLaunchTime` INTEGER NOT NULL, `launchCount` INTEGER NOT NULL, `installTime` INTEGER NOT NULL, `category` TEXT NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '807a99eabb4b9493a1906f91a1d8a55d')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `app_preferences`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAppPreferences = new HashMap<String, TableInfo.Column>(16);
        _columnsAppPreferences.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("activityName", new TableInfo.Column("activityName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("customLabel", new TableInfo.Column("customLabel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("isLeanback", new TableInfo.Column("isLeanback", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("bannerPath", new TableInfo.Column("bannerPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("iconPath", new TableInfo.Column("iconPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("customIconPath", new TableInfo.Column("customIconPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("dominantColor", new TableInfo.Column("dominantColor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("favoriteOrder", new TableInfo.Column("favoriteOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("isHidden", new TableInfo.Column("isHidden", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("lastLaunchTime", new TableInfo.Column("lastLaunchTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("launchCount", new TableInfo.Column("launchCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("installTime", new TableInfo.Column("installTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAppPreferences = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAppPreferences = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAppPreferences = new TableInfo("app_preferences", _columnsAppPreferences, _foreignKeysAppPreferences, _indicesAppPreferences);
        final TableInfo _existingAppPreferences = TableInfo.read(db, "app_preferences");
        if (!_infoAppPreferences.equals(_existingAppPreferences)) {
          return new RoomOpenHelper.ValidationResult(false, "app_preferences(com.auratv.launcher.model.AppItem).\n"
                  + " Expected:\n" + _infoAppPreferences + "\n"
                  + " Found:\n" + _existingAppPreferences);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "807a99eabb4b9493a1906f91a1d8a55d", "6490e647c11f4d139e24650865df0b69");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "app_preferences");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `app_preferences`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AppDao.class, AppDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AppDao appDao() {
    if (_appDao != null) {
      return _appDao;
    } else {
      synchronized(this) {
        if(_appDao == null) {
          _appDao = new AppDao_Impl(this);
        }
        return _appDao;
      }
    }
  }
}
