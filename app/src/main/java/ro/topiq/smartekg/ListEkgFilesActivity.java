package ro.topiq.smartekg;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FilenameFilter;


public class ListEkgFilesActivity extends ListActivity {

    private static final String LOG_TAG = ListEkgFilesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> ekgArrayList
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        setListAdapter(ekgArrayList);
        String[] ekgFiles = getEkgFilesList();

        if(ekgFiles.length > 0) {
            for(int i=0; i < ekgFiles.length; i++) {
                ekgArrayList.add(ekgFiles[i] + "\n");
                Log.i(LOG_TAG, "EKG Files: " + ekgFiles[i]);
            }
        }
    }

    private String[] getEkgFilesList() {
        String[] names = new String[0];
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SmartEKG");

        if( !dir.exists() ) {
            dir.mkdir();
        }

        if(dir.isDirectory()) {
            names = dir.list(
                    new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".ekg");
                        }
                    });
        }
        return names;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent();

        intent.setData(Uri.parse((String)l.getItemAtPosition(position)));
        setResult(RESULT_OK, intent);
        finish();
    }

}
