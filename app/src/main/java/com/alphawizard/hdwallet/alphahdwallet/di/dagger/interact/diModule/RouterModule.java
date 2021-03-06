package com.alphawizard.hdwallet.alphahdwallet.di.dagger.interact.diModule;


import com.alphawizard.hdwallet.alphahdwallet.functionModule.DemoWallet.WalletRouter;
import com.alphawizard.hdwallet.alphahdwallet.functionModule.DemoFristLaunch.FirstLaunchRouter;

import dagger.Module;
import dagger.Provides;

/**
 * provides      router
 *
 * activity 之间  跳转 模块的提供
 */
@Module
public class RouterModule {



    @Provides
    public static FirstLaunchRouter providesFirstLaunchRouter(){
        return new FirstLaunchRouter();
    }

    @Provides
    public static WalletRouter providesWalletRouter(){
        return new WalletRouter();
    }



}
