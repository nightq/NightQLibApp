package freedom.nightq.libraryapp.puzzlepicturesample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.LinkedHashMap;

import de.greenrobot.event.EventBus;
import freedom.nightq.baselibrary.os.BaseActivity;
import freedom.nightq.baselibrary.threadPool.NormalEngine;
import freedom.nightq.baselibrary.utils.FileUtils;
import freedom.nightq.libraryapp.R;
import freedom.nightq.puzzlepicture.ProcessPicsComposeActivity_;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ToProcessPicEvent;

public class PuzzlePictureSampleActivity extends BaseActivity {

    int ACTIVITY_GET_AVATAR = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_sample_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btnPuzzle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, ACTIVITY_GET_AVATAR);
                } catch (Exception ex) {
                    finish();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        NormalEngine.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Uri selectedImageURI = data.getData();
                String getContentPath = FileUtils.getRealPathFromURI(selectedImageURI);
                if (!TextUtils.isEmpty(getContentPath)) {
                    LinkedHashMap<String, String> selectedBeans = new LinkedHashMap<String, String>();
                    selectedBeans.put(getContentPath, getContentPath);
                    ProcessComposeModel processComposeModel = new ProcessComposeModel(
                            selectedBeans, true
                    );
                    ToProcessPicEvent toProcessPicEvent = new ToProcessPicEvent(processComposeModel, 0);
                    EventBus.getDefault().postSticky(toProcessPicEvent);
                    startActivity(new Intent(PuzzlePictureSampleActivity.this, ProcessPicsComposeActivity_.class));
                }
            }
        });
    }
}
