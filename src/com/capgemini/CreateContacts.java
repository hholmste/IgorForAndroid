package com.capgemini;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.content.ContentProviderOperation.Builder;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

public class CreateContacts extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		createContacts();

		this.finish();
	}

	private void createContacts() {
		String accountName = getCurrentAccountName();
		String accountType = getCurrentAccountType();

		createContact(accountName, accountType);
	}

	private void createContact(String accountName, String accountType) {
		Builder accountBuilder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_NAME, accountName)
				.withValue(RawContacts.ACCOUNT_TYPE, accountType);

		Builder contactBuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.MIMETYPE)
				.withValue(StructuredName.DISPLAY_NAME, "gaven famility")
				.withValue(StructuredName.FAMILY_NAME, "famility")
				.withValue(StructuredName.GIVEN_NAME, "gaven");

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		operations.add(accountBuilder.build());
		operations.add(contactBuilder.build());

		try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}

	private String getCurrentAccountType() {
		return null;
	}

	private String getCurrentAccountName() {
		AccountManager accountManager = AccountManager.get(getBaseContext());

		Account[] accounts = accountManager.getAccounts();

		for (Account account : accounts) {
			if (ContactsContract.AUTHORITY.equals(account.type)) {
				return account.name;
			}
		}

		return null;
	}

}