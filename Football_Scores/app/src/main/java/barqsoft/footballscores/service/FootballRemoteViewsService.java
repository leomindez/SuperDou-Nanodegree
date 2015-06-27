package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by leo on 6/23/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FootballRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FootballRemoteViewsFactory(getApplicationContext());
    }


    public class FootballRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private Cursor footballCursor;
        public static final int COL_HOME = 3;
        public static final int COL_AWAY = 4;
        public static final int COL_HOME_GOALS = 6;
        public static final int COL_AWAY_GOALS = 7;
        public static final int COL_MATCHTIME = 2;
        public static final int COL_ID = 8;


        public FootballRemoteViewsFactory(Context context) {
            this.mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            if (footballCursor != null)
                footballCursor.close();
            footballCursor = getMatchesWithScores();
        }

        @Override
        public void onDestroy() {
            if(footballCursor != null)
                footballCursor.close();
        }

        @Override
        public int getCount() {
            if (footballCursor != null)
                return footballCursor.getCount();
            else
                return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            String homeName = "";
            String awayName = "";
            String schedule = "";
            String score = "";

            if (footballCursor.moveToPosition(position)) {
                homeName = footballCursor.getString(COL_HOME);
                awayName = footballCursor.getString(COL_AWAY);
                schedule = footballCursor.getString(COL_MATCHTIME);
                score = Utilies.getScores(footballCursor.getInt(COL_HOME_GOALS), footballCursor.getInt(COL_AWAY_GOALS));
            }

            Intent fillInIntent = new Intent();
            RemoteViews footballRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.football_item_widget);

            footballRemoteViews.setTextViewText(R.id.widget_home_name, homeName);
            footballRemoteViews.setTextViewText(R.id.widget_away_home, awayName);
            footballRemoteViews.setTextViewText(R.id.widget_final_score, score);
            footballRemoteViews.setTextViewText(R.id.widget_date, schedule);

            footballRemoteViews.setOnClickFillInIntent(R.id.widget_item_container,fillInIntent);
            return footballRemoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (footballCursor != null)
                return footballCursor.getLong(footballCursor.getColumnIndex(DatabaseContract.scores_table.MATCH_ID));
            else
                return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private Cursor getMatchesWithScores() {
            return mContext.getContentResolver().query(DatabaseContract.BASE_CONTENT_URI, null, null, null, null);
        }
    }
}
