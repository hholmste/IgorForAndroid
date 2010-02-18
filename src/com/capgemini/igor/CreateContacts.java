package com.capgemini.igor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.content.ContentProviderOperation.Builder;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.util.Log;

public class CreateContacts extends Activity {
	private static final String logTag = "CreateContacts";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			deleteAllContacts();

			createContacts();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}

		this.finish();
	}

	private void deleteAllContacts() {
		Log.d(logTag, "Marked " + getContentResolver().delete(RawContacts.CONTENT_URI, null, null)
				+ " raw contacts for deletion");
	}

	private void createContacts() throws RemoteException, OperationApplicationException {
		createRawContact();
	}

	private void createRawContact() throws RemoteException, OperationApplicationException {
		Builder accountBuilder = getAccountBuilder();

		Builder rawContactBuilder = getStructuredNameBuilder();

		Builder phoneBuilder = getPhoneBuilder();

		Builder nicknameBuilder = getNicknameBuilder();

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		operations.add(accountBuilder.build());
		operations.add(rawContactBuilder.build());
		operations.add(phoneBuilder.build());
		operations.add(nicknameBuilder.build());

		getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
	}

	private Builder getAccountBuilder() {
		Builder accountBuilder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_NAME, null)
				.withValue(RawContacts.ACCOUNT_TYPE, null);
		return accountBuilder;
	}

	private Builder getNicknameBuilder() {
		Builder nicknameBuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE)
				.withValue(Nickname.NAME, "Mr. Incredible")
				.withValue(Nickname.TYPE, Nickname.TYPE_CUSTOM)
				.withValue(Nickname.LABEL, "Superhero");
		return nicknameBuilder;
	}

	private Builder getPhoneBuilder() {
		Builder phoneBuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, "96964926")
				.withValue(Phone.TYPE, Phone.TYPE_HOME)
				.withValue(Phone.LABEL, "some label");
		return phoneBuilder;
	}

	private Builder getStructuredNameBuilder() {
		Builder rawContactBuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.MIMETYPE)
				.withValue(StructuredName.DISPLAY_NAME, "Regular Guy")
				.withValue(StructuredName.FAMILY_NAME, "Guy")
				.withValue(StructuredName.GIVEN_NAME, "Regular")
				.withValue(StructuredName.PREFIX, "Mr.");
		return rawContactBuilder;
	}
}