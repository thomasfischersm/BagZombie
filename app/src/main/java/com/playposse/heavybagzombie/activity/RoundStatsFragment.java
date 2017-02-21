package com.playposse.heavybagzombie.activity;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playposse.heavybagzombie.R;
import com.playposse.heavybagzombie.provider.RoundStatsRecord;

/**
 * A {@link Fragment} that shows the round stats. It's intended to be used inside of a
 * {@link android.support.v4.view.ViewPager} that shows many fragments and one round per fragment.
 */
public class RoundStatsFragment extends Fragment {

    public static final String TAG = "RoundStatsFragment";

    private RoundStatsRecord roundStatsRecord;

    private TextView roundTitleTextView;
    private TextView hitValueTextView;
    private TextView heavyHitValueTextView;
    private TextView timeoutValueTextView;
    private TextView missesValueTextView;
    private TextView fastestReactionTimeValueTextView;
    private TextView averageReactionTimeValueTextView;

    public RoundStatsFragment() {
        // Required empty public constructor
    }

    public static RoundStatsFragment newInstance(RoundStatsRecord roundStatsRecord) {
        RoundStatsFragment fragment = new RoundStatsFragment();
        fragment.setArguments(roundStatsRecord.toBundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roundStatsRecord = new RoundStatsRecord(getArguments());
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootLayout = inflater.inflate(R.layout.fragment_round_stats, container, false);

        roundTitleTextView = (TextView) rootLayout.findViewById(R.id.roundTitleTextView);
        hitValueTextView = (TextView) rootLayout.findViewById(R.id.hitValueTextView);
        heavyHitValueTextView = (TextView) rootLayout.findViewById(R.id.heavyHitValueTextView);
        timeoutValueTextView = (TextView) rootLayout.findViewById(R.id.timeoutValueTextView);
        missesValueTextView = (TextView) rootLayout.findViewById(R.id.missesValueTextView);
        fastestReactionTimeValueTextView =
                (TextView) rootLayout.findViewById(R.id.fastestReactionTimeValueTextView);
        averageReactionTimeValueTextView =
                (TextView) rootLayout.findViewById(R.id.averageReactionTimeValueTextView);

        updateUi();

        return rootLayout;
    }

    public void updateUi(RoundStatsRecord roundStatsRecord) {
        this.roundStatsRecord = roundStatsRecord;
//        setArguments(roundStatsRecord.toBundle());

        updateUi();
    }

    private void updateUi() {
        if (roundStatsRecord != null) {
            int roundIndex = roundStatsRecord.getRoundIndex();
            if (roundIndex < 0) {
                roundTitleTextView.setText(R.string.summary_round_title);
            } else {
                roundTitleTextView.setText(getString(R.string.rounds_title, roundIndex + 1));
            }
            hitValueTextView.setText(Integer.toString(roundStatsRecord.getHitCount()));
            heavyHitValueTextView.setText(Integer.toString(roundStatsRecord.getHeavyHitCount()));
            timeoutValueTextView.setText(Integer.toString(roundStatsRecord.getTimeoutCount()));
            missesValueTextView.setText(Integer.toString(roundStatsRecord.getMissCount()));
            fastestReactionTimeValueTextView.setText(
                    getString(R.string.ms_value, roundStatsRecord.getFastestReactionTime()));
            averageReactionTimeValueTextView.setText(
                    getString(R.string.ms_value, roundStatsRecord.getAverageReactionTime()));
        }
    }
}
