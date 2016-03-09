package freedom.nightq.libraryapp;

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
import freedom.nightq.puzzlepicture.ProcessPicsComposeActivity_;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ToProcessPicEvent;

public class SampleActivity extends BaseActivity {

    int ACTIVITY_GET_AVATAR = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(SampleActivity.this, ProcessPicsComposeActivity_.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, ACTIVITY_GET_AVATAR);
            } catch (Exception ex) {
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
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
                    startActivity(new Intent(SampleActivity.this, ProcessPicsComposeActivity_.class));
                }
            }
        });
    }
}
