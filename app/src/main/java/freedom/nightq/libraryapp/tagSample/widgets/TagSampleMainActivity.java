package freedom.nightq.libraryapp.tagSample.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import freedom.nightq.libraryapp.R;
import freedom.nightq.widgets.tagview.TagGroupDefaultLayout;
import freedom.nightq.widgets.tagview.bean.TagInfoBeanImpl;

public class TagSampleMainActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TAG = 100;

    public List<TagInfoBeanImpl> list;

    private FrameLayout rootRL;
    public TagGroupDefaultLayout tagGroupDefaultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_sample_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button fab = (Button) findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(TagSampleMainActivity.this, AddTagViewActivity.class), REQUEST_ADD_TAG);
            }
        });

        list = new ArrayList<>();
        tagGroupDefaultLayout = (TagGroupDefaultLayout) findViewById(R.id.tags);
        tagGroupDefaultLayout.setData(list);

        rootRL = (FrameLayout) findViewById(R.id.layoutRoot);
        int widthPixels = getSreenWidth();
        ViewGroup.LayoutParams lp = rootRL.getLayoutParams();
        lp.width = widthPixels;
        lp.height = widthPixels;
        rootRL.setLayoutParams(lp);

    }

    public int getSreenWidth () {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_TAG && resultCode == RESULT_OK) {
            list.addAll(((TagsModel) data.getSerializableExtra("DATA")).list);
            tagGroupDefaultLayout.setData(list);
        }
    }

}
