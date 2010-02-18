package com.capgemini.igor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
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
		Builder accountBuilder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_NAME, null)
				.withValue(RawContacts.ACCOUNT_TYPE, null);

		Builder rawContactBuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.MIMETYPE)
				.withValue(StructuredName.DISPLAY_NAME, "Regular Guy")
				.withValue(StructuredName.FAMILY_NAME, "Guy")
				.withValue(StructuredName.GIVEN_NAME, "Regular")
				.withValue(StructuredName.PREFIX, "Mr.");

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		operations.add(accountBuilder.build());
		operations.add(rawContactBuilder.build());

		ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);

		for (ContentProviderResult result : results) {

			if (result.uri.getPathSegments().contains("raw_contacts")) {

				String lastPathSegment = result.uri.getLastPathSegment();

				if (lastPathSegment != null) {
					int rawContactId = Integer.parseInt(lastPathSegment);

					addNameDetails(rawContactId);

					addPhoneDetails(rawContactId);

				}
			}
		}
	}

	private void addNameDetails(int rawContactId) throws RemoteException, OperationApplicationException {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValue(Data.RAW_CONTACT_ID, rawContactId)
				.withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE)
				.withValue(Nickname.NAME, "Mr. Incredible")
				.withValue(Nickname.TYPE, Nickname.TYPE_CUSTOM)
				.withValue(Nickname.LABEL, "Superhero")
				.build());

		getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
	}

	private void addPhoneDetails(int rawContactId) throws RemoteException, OperationApplicationException {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValue(Data.RAW_CONTACT_ID, rawContactId)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, "96964926")
				.withValue(Phone.TYPE, Phone.TYPE_HOME)
				.withValue(Phone.LABEL, "some label")
				.build());

		getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
	}
}