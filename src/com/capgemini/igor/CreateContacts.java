package com.capgemini.igor;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.capgemini.igor.pusher.ContactPusher;
import com.capgemini.igor.pusher.ContactRemover;

public class CreateContacts extends Activity {
	private ContactRemover contactRemover;
	private ContactPusher contactPusher;
	private ProgressBar progressBar;

	private Button createButton;

	private Button deleteButton;

	private Button exitButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		setUpButtons();

		setUpOtherWidgets();
	}

	private void setUpOtherWidgets() {
		progressBar = (ProgressBar) findViewById(R.id.ProgressBar01);
	}

	private void setUpButtons() {
		createButton = (Button) findViewById(R.id.CreateButton);
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createContacts();
			}
		});

		deleteButton = (Button) findViewById(R.id.DeleteButton);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteAllContacts();
			}
		});

		exitButton = (Button) findViewById(R.id.ExitButton);
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exit();
			}
		});
	}

	private void createContacts() {
		killWorkers();

		contactPusher = new ContactPusher(this);
		contactPusher.execute();
	}

	private void deleteAllContacts() {
		contactRemover = new ContactRemover(this);
		contactRemover.execute();
	}

	private void killWorkers() {
		killWorker(contactPusher);
		killWorker(contactRemover);
	}

	private void killWorker(AsyncTask<?, ?, ?> worker) {
		if (worker != null && !worker.isCancelled()) {
			worker.cancel(true);
		}
	}

	private void exit() {
		killWorkers();

		this.finish();
	}

	public void createContactsUpdateCallback(Integer progress) {
		progressBar.setProgress(progress);
	}

	public void createContactsStartedCallback() {
		progressBar.setIndeterminate(false);
		progressBar.setMax(20);
		progressBar.setProgress(0);
		progressBar.setVisibility(ProgressBar.VISIBLE);

		disableButtons();
	}

	public void createContactsFinishedCallback() {
		progressBar.setVisibility(ProgressBar.INVISIBLE);

		enableButtons();
	}

	public void deleteContactsStartedCallback() {
		progressBar.setIndeterminate(true);
		progressBar.setVisibility(ProgressBar.VISIBLE);

		disableButtons();
	}

	public void deleteContactsFinishedCallback() {
		progressBar.setVisibility(ProgressBar.INVISIBLE);

		enableButtons();
	}

	private void disableButtons() {
		createButton.setEnabled(false);
		deleteButton.setEnabled(false);
	}

	private void enableButtons() {
		createButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}
}