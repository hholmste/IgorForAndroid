package com.capgemini;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.content.ContentProviderOperation.Builder;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
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
				.withValue(StructuredName.DISPLAY_NAME, "gaven famility")
				.withValue(StructuredName.FAMILY_NAME, "famility")
				.withValue(StructuredName.GIVEN_NAME, "gaven")
				.withValue(StructuredName.PREFIX, "Mr.");

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		operations.add(accountBuilder.build());
		operations.add(rawContactBuilder.build());

		ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);

		for (ContentProviderResult result : results) {

			if (result.uri.getPathSegments().contains("raw_contacts")) {

				Cursor resultCursor = getContentResolver().query(result.uri,
						new String[] { RawContacts.ACCOUNT_NAME,
								RawContacts.SOURCE_ID,
								RawContacts.CONTACT_ID,
								RawContacts._ID },
						null,
						null,
						null);

				Log.d(logTag, "names of columns: ");
				String[] columns = resultCursor.getColumnNames();

				while (resultCursor.moveToNext()) {
					for (int i = 0; i < columns.length; i++) {

						Log.d(logTag, columns[i] + " : " + resultCursor.getString(i));
					}
					Log.d(logTag, "---");
				}
			}
		}

	}
}