/**
 *
 */
package net.suteren.android.jidelak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import net.suteren.android.jidelak.ErrorType;
import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.NetworkUtils;
import net.suteren.android.jidelak.NotificationUtils;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.Utils;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.provider.JidelakProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * @author Petr
 */
public class TemplateImporterActivity extends Activity {

    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private static Logger log = LoggerFactory
            .getLogger(TemplateImporterActivity.class);

    private Uri sourceUri;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            sourceUri = intent.getData();
        } else if (Intent.ACTION_SEND.equals(intent.getAction()) || Intent.ACTION_SENDTO.equals(intent.getAction())) {
            sourceUri = bundle != null ? (Uri) bundle.get(Intent.EXTRA_STREAM) : null;
        }

        // showIntent();
        if (sourceUri != null)
            ask();

    }

    private void ask() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        new Worker().execute(new Void[0]);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
                finish();
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                getResources().getString(R.string.import_template_question,
                        sourceUri.getLastPathSegment()))
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .setCancelable(false).show();
    }

    void importTemplate() throws JidelakException {

        mHandler.post(new ToastRunnable(getResources().getString(
                R.string.importing_template, sourceUri.getLastPathSegment())));

        SQLiteDatabase db = JidelakDbHelper.getInstance(getApplicationContext()).getReadableDatabase();
        RestaurantDao restaurantDao = new RestaurantDao(db);

        Restaurant restaurant = new Restaurant();

        try {
            restaurantDao.insert(restaurant);

            String fileName = saveLocally(sourceUri, restaurant);

            try {

                Utils.parseConfig(openFileInput(fileName), restaurant);
            } catch (ParserConfigurationException e) {
                throw new JidelakException(e.getMessage(), e)
                        .setRestaurant(restaurant)
                        .setErrorType(ErrorType.PARSING).setHandled(true);
            } catch (TransformerConfigurationException e) {
                throw new JidelakException(e.getMessage(), e)
                        .setRestaurant(restaurant)
                        .setErrorType(ErrorType.PARSING).setHandled(true);
            } catch (TransformerFactoryConfigurationError e) {
                throw new JidelakException(e.getMessage(), e)
                        .setRestaurant(restaurant)
                        .setErrorType(ErrorType.PARSING).setHandled(true);
            } catch (TransformerException e) {
                throw new JidelakException(e.getMessage(), e)
                        .setRestaurant(restaurant)
                        .setErrorType(ErrorType.PARSING).setHandled(true);
            } catch (JidelakException e) {
                throw e.setRestaurant(restaurant);
            }

            restaurantDao.update(restaurant);

            new SourceDao(db).insert(restaurant.getSource());

            new AvailabilityDao(db).insert(restaurant.getOpeningHours());

        } catch (Exception e) {
            deleteFile(restaurant.getTemplateName());
            getContentResolver().delete(JidelakProvider.RESTAURANTS_URI, String.format("%s=?", RestaurantDao.ID
                    .getName()),new String[]{String.valueOf(restaurant.getId())});
            if (e instanceof JidelakException)
                throw (JidelakException) e;
            else
                throw new JidelakException(getResources().getString(
                        R.string.unexpected_exception), e);
        }
    }

    String saveLocally(Uri uri, Restaurant restaurant) throws IOException,
            JidelakException {

        String fileName = restaurant.getTemplateName();

        log.debug("URI: " + uri);

        InputStream sourceStream = NetworkUtils.streamFromUrl(
                getApplicationContext(), uri);

        log.debug("Available: " + sourceStream.available());

        FileOutputStream out = openFileOutput(fileName, MODE_PRIVATE);

        Writer bw = new BufferedWriter(new OutputStreamWriter(out));
        long cnt = 0;
        try {
            Reader br = new BufferedReader(new InputStreamReader(sourceStream));
            try {
                CharBuffer buf = CharBuffer.allocate(64);
                while ((cnt += br.read(buf)) >= 0) {
                    bw.append((CharSequence) buf.flip());
                    buf.clear();
                }
            } finally {
                br.close();
            }
        } finally {
            bw.close();
            log.debug("read " + cnt + " bytes");
        }
        return fileName;
    }

    private class Worker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                importTemplate();
            } catch (JidelakException e) {
                log.error(e.getMessage(), e);

                NotificationUtils.makeNotification(getApplicationContext(), e);
            }
            return null;
        }

    }
}
