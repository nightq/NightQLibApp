package freedom.nightq.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import freedom.nightq.libraryapp.puzzlepicturesample.PuzzlePictureSampleActivity;
import freedom.nightq.libraryapp.tagSample.widgets.AddTagViewActivity;
import freedom.nightq.libraryapp.tagSample.widgets.TagSampleMainActivity;

public class MainSampleActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_sample_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btnPuzzle).setOnClickListener(this);
        findViewById(R.id.btnTagView).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPuzzle:
                startActivity(new Intent(this, PuzzlePictureSampleActivity.class));
                break;
            case R.id.btnTagView:
                startActivity(new Intent(this, TagSampleMainActivity.class));
                break;
        }
    }
}
