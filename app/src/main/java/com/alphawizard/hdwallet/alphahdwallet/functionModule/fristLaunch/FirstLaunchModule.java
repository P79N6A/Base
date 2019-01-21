package com.alphawizard.hdwallet.alphahdwallet.functionModule.fristLaunch;

import com.alphawizard.hdwallet.alphahdwallet.di.Scope.ActivityScoped;


import dagger.Binds;
import dagger.Module;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link }.
 */
@Module
public abstract class FirstLaunchModule {

    @ActivityScoped
    @Binds
    abstract FirstLaunchContract.Presenter taskPresenter(FirstLaunchPresenter presenter);

//    请注意  和activity 关联的注解 要加上static

}

