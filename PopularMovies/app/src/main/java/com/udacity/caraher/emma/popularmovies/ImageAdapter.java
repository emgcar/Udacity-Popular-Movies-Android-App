package com.udacity.caraher.emma.popularmovies;

/* class from https://developer.android.com/guide/topics/ui/layout/gridview.html */

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.udacity.caraher.emma.popularmovies.data.MovieContract;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int count;
    private String movies[];

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void clear() {
        movies = null;
        setCount(0);
        notifyDataSetChanged();
    }

    public void add(String[] movieList) {
        movies = movieList;
        setCount(movies.length);
        notifyDataSetChanged();
    }

    public int getCount() {
        if (movies == null)
            return 8;
        return count;
    }

    public void setCount(int newCount) {
        count = newCount;
    }

    public Object getItem(int position) {
        return this.getItem(position);
    }

    public String getItemAtPosition(int position) {
        if (movies != null)
            return movies[position];
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
            imageView.setPadding(6, 6, 6, 6);
        } else {
            imageView = (ImageView) convertView;
        }

        if (movies == null || movies[position] == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            try {
                ContentValues values = Utility.getMovieContentValues(mContext, movies[position]);
                String posterPath = values.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);


                String baseUrl = convertView.getContext().getResources()
                        .getString(R.string.base_poster_url) + posterPath + "?";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(convertView.getContext().getResources().getString(R.string.api),
                                convertView.getContext().getResources().getString(R.string.api_key))
                        .build();

                /* START http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
                new ImageLoadTask(builtUri.toString(), imageView).execute();

                /* END http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */

            } catch (Exception e) {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }

        return imageView;
    }

    /* START http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

    /* END http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
}