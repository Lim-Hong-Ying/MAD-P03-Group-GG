package sg.edu.np.mad_p03_group_gg.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Download Image and Set Image to Bitmap Holder
 *
 * Credits: Hong Ying
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> { //Method to download images
    ImageView bitmap;

    public ImageDownloader(ImageView bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    protected Bitmap doInBackground(String... strings) { //Downloads image
        String url = strings[0];
        Bitmap image = null;
        try {
            InputStream input = new java.net.URL(url).openStream();
            image = BitmapFactory.decodeStream(input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    protected void onPostExecute(Bitmap result) {
        bitmap.setImageBitmap(result);
    } //Sets image for bitmap holder
}
