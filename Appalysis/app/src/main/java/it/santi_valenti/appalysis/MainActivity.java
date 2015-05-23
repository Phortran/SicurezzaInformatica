package it.santi_valenti.appalysis;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class MainActivity extends Activity {
    TextView etList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        File dest = createFile();
        etList = (TextView) findViewById(R.id.editText);
        etList.setMovementMethod(new ScrollingMovementMethod());

        int cazzo = 0;

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (Object object : pkgAppsList) {
            ResolveInfo info = (ResolveInfo) object;
            File file = new File(info.activityInfo.applicationInfo.publicSourceDir);
            etList.append(file.getName() + "     size: " + file.length() / 1000000 + " MB" + "\n");
            if (cazzo == 10) {
                try {
                    copy(file, dest);
                } catch (IOException e) {
                    Log.e("CAZZO", "cazzo = " + cazzo);
                    e.printStackTrace();
                }
            }

            cazzo++;
        }
    }

    private File createFile() {
        File dest = Environment.getExternalStorageDirectory();
        String path = dest.getAbsolutePath() + "/dir1/dir2";
        Log.e("VALENTI", path);
        File dir = new File(path);
        dir.mkdirs();

        return new File(dir, "filename.apk");
    }

    private void copy(File src, File dst) throws IOException {
        if (!dst.createNewFile()) {
            Log.e("VALENTI", "impossibile creare file");
            return;
        }

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
