package com.alphawizard.hdwallet.alphahdwallet.di.dagger.interact.diModule;


import com.alphawizard.hdwallet.alphahdwallet.di.dagger.support.Repositor.PreferenceRepositoryType;

import com.alphawizard.hdwallet.alphahdwallet.di.dagger.support.Repositor.RealmDBOperatorType;
import com.alphawizard.hdwallet.alphahdwallet.di.dagger.interact.CreateWalletInteract;
import com.alphawizard.hdwallet.alphahdwallet.di.dagger.interact.DefaultWalletInteract;
import com.alphawizard.hdwallet.alphahdwallet.di.dagger.interact.RealmTestDBInteract;
import com.alphawizard.hdwallet.alphahdwallet.di.dagger.support.service.DemoServiceInterface;

import dagger.Module;
import dagger.Provides;

/**
 * provides  interact and     router
 *   功能模块的提供
 *
 */
@Module
public class InteractModule {

    @Provides
    static CreateWalletInteract createWalletInteract(DemoServiceInterface demoService){
        return new CreateWalletInteract(demoService);
    }

    @Provides
    static DefaultWalletInteract defaultWalletInteract( PreferenceRepositoryType  preferenceRepositoryType){
        return new DefaultWalletInteract(preferenceRepositoryType);
    }

    @Provides
    static RealmTestDBInteract realmTestDBInteract(RealmDBOperatorType realmDBOperatorType){
        return new RealmTestDBInteract(realmDBOperatorType);
    }



}
