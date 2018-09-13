// Generated by Dagger (https://google.github.io/dagger).
package com.movie.maotrailer.ui.favorite;

import android.arch.lifecycle.ViewModelProvider;
import android.support.v4.app.Fragment;
import com.movie.maotrailer.utils.ColumnUtils;
import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity_MembersInjector;
import javax.inject.Provider;

public final class FavoriteActivity_MembersInjector implements MembersInjector<FavoriteActivity> {
  private final Provider<DispatchingAndroidInjector<Fragment>> supportFragmentInjectorProvider;

  private final Provider<DispatchingAndroidInjector<android.app.Fragment>>
      frameworkFragmentInjectorProvider;

  private final Provider<ViewModelProvider.Factory> viewModelFactoryProvider;

  private final Provider<ColumnUtils> columnUtilsProvider;

  public FavoriteActivity_MembersInjector(
      Provider<DispatchingAndroidInjector<Fragment>> supportFragmentInjectorProvider,
      Provider<DispatchingAndroidInjector<android.app.Fragment>> frameworkFragmentInjectorProvider,
      Provider<ViewModelProvider.Factory> viewModelFactoryProvider,
      Provider<ColumnUtils> columnUtilsProvider) {
    this.supportFragmentInjectorProvider = supportFragmentInjectorProvider;
    this.frameworkFragmentInjectorProvider = frameworkFragmentInjectorProvider;
    this.viewModelFactoryProvider = viewModelFactoryProvider;
    this.columnUtilsProvider = columnUtilsProvider;
  }

  public static MembersInjector<FavoriteActivity> create(
      Provider<DispatchingAndroidInjector<Fragment>> supportFragmentInjectorProvider,
      Provider<DispatchingAndroidInjector<android.app.Fragment>> frameworkFragmentInjectorProvider,
      Provider<ViewModelProvider.Factory> viewModelFactoryProvider,
      Provider<ColumnUtils> columnUtilsProvider) {
    return new FavoriteActivity_MembersInjector(
        supportFragmentInjectorProvider,
        frameworkFragmentInjectorProvider,
        viewModelFactoryProvider,
        columnUtilsProvider);
  }

  @Override
  public void injectMembers(FavoriteActivity instance) {
    DaggerAppCompatActivity_MembersInjector.injectSupportFragmentInjector(
        instance, supportFragmentInjectorProvider.get());
    DaggerAppCompatActivity_MembersInjector.injectFrameworkFragmentInjector(
        instance, frameworkFragmentInjectorProvider.get());
    injectViewModelFactory(instance, viewModelFactoryProvider.get());
    injectColumnUtils(instance, columnUtilsProvider.get());
  }

  public static void injectViewModelFactory(
      FavoriteActivity instance, ViewModelProvider.Factory viewModelFactory) {
    instance.viewModelFactory = viewModelFactory;
  }

  public static void injectColumnUtils(FavoriteActivity instance, ColumnUtils columnUtils) {
    instance.columnUtils = columnUtils;
  }
}