// Generated by Dagger (https://google.github.io/dagger).
package com.movie.maotrailer.helper;

import dagger.internal.Factory;

public final class AppsExecutor_Factory implements Factory<AppsExecutor> {
  private static final AppsExecutor_Factory INSTANCE = new AppsExecutor_Factory();

  @Override
  public AppsExecutor get() {
    return provideInstance();
  }

  public static AppsExecutor provideInstance() {
    return new AppsExecutor();
  }

  public static AppsExecutor_Factory create() {
    return INSTANCE;
  }

  public static AppsExecutor newAppsExecutor() {
    return new AppsExecutor();
  }
}