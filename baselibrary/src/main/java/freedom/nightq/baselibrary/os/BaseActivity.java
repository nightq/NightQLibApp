package freedom.nightq.baselibrary.os;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by Nightq on 16/3/7.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        NightQAppLib.init(this);
        super.onCreate(savedInstanceState);
    }
}
