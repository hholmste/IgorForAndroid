package com.capgemini.igor.pusher;

import android.os.AsyncTask;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.capgemini.igor.CreateContacts;

public class ContactRemover extends AsyncTask<Void, Void, Void> {

	private static final String logTag = "Igor:ContactRemover";

	private final CreateContacts parent;

	public ContactRemover(CreateContacts parent) {
		super();
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		parent.deleteContactsStartedCallback();
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		parent.deleteContactsFinishedCallback();
	}

	@Override
	protected Void doInBackground(Void... params) {
		int numberOfContactsMarkedForDeletion = parent.getContentResolver().delete(RawContacts.CONTENT_URI, null, null);

		Log.i(logTag, "Marked " + numberOfContactsMarkedForDeletion + " raw contacts for deletion");

		return null;
	}

}
