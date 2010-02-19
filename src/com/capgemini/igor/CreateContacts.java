package com.capgemini.igor;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.capgemini.igor.pusher.ContactPusher;

public class CreateContacts extends Activity {
	private static final String logTag = "Igor:CreateContacts";

	private Thread createContactsThread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		deleteAllContacts();

		createContacts();

		try {
			createContactsThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.finish();
	}

	private void createContacts() {
		if (createContactsThread != null && createContactsThread.isAlive()) {
			createContactsThread.interrupt();
		}
		createContactsThread = new Thread(new ContactPusher(getContentResolver()));
		createContactsThread.start();
	}

	private void deleteAllContacts() {
		Log.d(logTag, "Marked " + getContentResolver().delete(RawContacts.CONTENT_URI, null, null)
				+ " raw contacts for deletion");
	}

}