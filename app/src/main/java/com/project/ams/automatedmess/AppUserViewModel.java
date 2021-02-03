package com.project.ams.automatedmess;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class AppUserViewModel extends AndroidViewModel {

    private AppUserRepository mRepository;

    private LiveData<List<AppUser>> mAllItems;

    public AppUserViewModel(Application application) {
        super(application);
        mRepository = new AppUserRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    LiveData<List<AppUser>> getAllItems() { return mAllItems; }

    public void insert(AppUser user) { mRepository.insert(user); }

    public void deleteAll() { mRepository.deleteAll(); }
}
