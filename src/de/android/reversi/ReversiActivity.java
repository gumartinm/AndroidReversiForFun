package de.android.reversi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ReversiActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reversi);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_reversi, menu);
        return true;
    }
}
