package com.example.android.stockkeepingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CatalogActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate UI view
		setContentView(R.layout.activity_catalog);

		// Setup FAB to open EditorActivity
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
				startActivity(intent);
			}
		});
	}
}
