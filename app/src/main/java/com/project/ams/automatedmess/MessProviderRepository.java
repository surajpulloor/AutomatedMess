package com.project.ams.automatedmess;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MessProviderRepository {

    private MessProviderDao mMessProviderDao;
    private LiveData<List<MessProviderProfile>> mAllItems;

    MessProviderRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mMessProviderDao = db.messProviderDao();
        mAllItems = mMessProviderDao.getAll();
    }

    LiveData<List<MessProviderProfile>> getAllItems() {
        return mAllItems;
    }


    public void insert (MessProviderProfile messProvider) {
        new MessProviderRepository.insertAsyncTask_MessProvider(mMessProviderDao).execute(messProvider);
    }

    public void deleteAll () {
        new MessProviderRepository.deleteAllAsyncTask_MessProvider(mMessProviderDao).execute();
    }

    private static class insertAsyncTask_MessProvider extends AsyncTask<MessProviderProfile, Void, Void> {

        private MessProviderDao mAsyncTaskDao;

        insertAsyncTask_MessProvider(MessProviderDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MessProviderProfile... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask_MessProvider extends AsyncTask<MessProviderProfile, Void, Void> {

        private MessProviderDao mAsyncTaskDao;

        deleteAllAsyncTask_MessProvider(MessProviderDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MessProviderProfile... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
}
