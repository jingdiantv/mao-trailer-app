// Generated by Dagger (https://google.github.io/dagger).
package com.movie.maotrailer.ui.main.fragments.profile;

import android.support.v4.app.Fragment;
import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerFragment_MembersInjector;
import javax.inject.Provider;

public final class ProfileFragment_MembersInjector implements MembersInjector<ProfileFragment> {
  private final Provider<DispatchingAndroidInjector<Fragment>> childFragmentInjectorProvider;

  public ProfileFragment_MembersInjector(
      Provider<DispatchingAndroidInjector<Fragment>> childFragmentInjectorProvider) {
    this.childFragmentInjectorProvider = childFragmentInjectorProvider;
  }

  public static MembersInjector<ProfileFragment> create(
      Provider<DispatchingAndroidInjector<Fragment>> childFragmentInjectorProvider) {
    return new ProfileFragment_MembersInjector(childFragmentInjectorProvider);
  }

  @Override
  public void injectMembers(ProfileFragment instance) {
    DaggerFragment_MembersInjector.injectChildFragmentInjector(
        instance, childFragmentInjectorProvider.get());
  }
}