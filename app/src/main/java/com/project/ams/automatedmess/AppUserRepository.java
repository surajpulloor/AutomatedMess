package com.project.ams.automatedmess;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class AppUserRepository {
    private AppUserDao mAppUserDao;
    private LiveData<List<AppUser>> mAppUsers;

    AppUserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mAppUserDao = db.appUserDao();
        mAppUsers = mAppUserDao.getAll();
    }

    LiveData<List<AppUser>> getAllItems() {
        return mAppUsers;
    }


    public void insert (AppUser appUser) {
        new AppUserRepository.insertAppUserAsyncTask(mAppUserDao).execute(appUser);
    }

    private static class insertAppUserAsyncTask extends AsyncTask<AppUser, Void, Void> {

        private AppUserDao mAsyncTaskDao;

        insertAppUserAsyncTask(AppUserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AppUser... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    public void deleteAll () {
        new AppUserRepository.deleteAppUserAllAsyncTask(mAppUserDao).execute();
    }

    private static class deleteAppUserAllAsyncTask extends AsyncTask<AppUser, Void, Void> {

        private AppUserDao mAsyncTaskDao;

        deleteAppUserAllAsyncTask(AppUserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AppUser... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
}
