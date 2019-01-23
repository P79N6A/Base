package com.alphawizard.hdwallet.alphahdwallet.db.Repositor;


import io.reactivex.Single;

public interface PreferenceRepositoryType {
	Single<String> getCurrentWalletAddress();
	void setCurrentWalletAddress(String address);

	String getDefaultNetwork();
	void setDefaultNetwork(String netName);


}
