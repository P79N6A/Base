package com.alphawizard.hdwallet.alphahdwallet.functionModule.send;

import android.arch.lifecycle.ViewModelProviders;
import android.widget.Button;
import android.widget.EditText;

import com.alphawizard.hdwallet.alphahdwallet.R;
import com.alphawizard.hdwallet.alphahdwallet.di.ViewModule.SendViewModuleFactory;
import com.alphawizard.hdwallet.common.presenter.BasePresenterToolbarActivity;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class SendActivity extends BasePresenterToolbarActivity<SendContract.Presenter,SendViewModule> implements SendContract.View{

    @Inject
    SendContract.Presenter mPresenter;

    @Inject
    SendViewModuleFactory viewModuleFactory;
    SendViewModule viewModel;


    @BindView(R.id.ed_eth_address)
    EditText  mAddresss;

    @BindView(R.id.ed_eth_amounts)
    EditText  mAmount;

    @BindView(R.id.btn_send)
    Button mSend;

    @Override
    public SendContract.Presenter initPresenter() {
        return mPresenter;
    }

    @Override
    public SendViewModule initViewModule() {
        return viewModel;
    }

    @Override
    public int getContentLayoutID() {
        return R.layout.activity_send;
    }

    @Override
    public void initData() {
        super.initData();
        viewModel = ViewModelProviders.of(this, viewModuleFactory)
                .get(SendViewModule.class);
        getmPresenter().takeView(this,viewModel);
    }

    @OnClick(R.id.btn_send)
    void onClickSend(){
        String address = mAddresss.getText().toString();
        String amounts = mAmount.getText().toString();
        mPresenter.sendTransaction("aa",address,amounts);
    }
}

