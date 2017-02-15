package com.playposse.heavybagzombie.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.R;
import com.playposse.heavybagzombie.util.IntentParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ManualFightSetupActivity extends ParentActivity {

    private static final int SINGLE_PUNCHES_POSITION = 0;
    private static final int TWO_PUNCH_COMBO_POSITION = 1;
    private static final int POPULAR_COMBOS_POSITION = 2;
    private static final int ALL_COMBOS_POSITION = 3;
    private static final int CUSTOM_COMBO_POSITION = 4;

    private EditText roundCountEditText;
    private EditText roundDurationEditText;
    private EditText restDurationEditText;
    private Spinner combinationSpinner;
    private LinearLayout customPunchesLayout;
    private GridLayout customPunchesGrid;
    private TextView clearPunchesLink;
    private FloatingActionButton startFightButton;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_manual_fight_setup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        roundCountEditText = (EditText) findViewById(R.id.roundCountEditText);
        roundDurationEditText = (EditText) findViewById(R.id.roundDurationEditText);
        restDurationEditText = (EditText) findViewById(R.id.restDurationEditText);
        combinationSpinner = (Spinner) findViewById(R.id.combinationSpinner);
        customPunchesLayout = (LinearLayout) findViewById(R.id.customPunchesLayout);
        customPunchesGrid = (GridLayout) findViewById(R.id.customPunchesGrid);
        clearPunchesLink = (TextView) findViewById(R.id.clearPunchesLink);
        startFightButton = (FloatingActionButton) findViewById(R.id.startFightButton);

        roundCountEditText.setText(BagZombiePreferences.getCustomRoundCount(this).toString());
        roundDurationEditText.setText(BagZombiePreferences.getCustomRoundDuration(this).toString());
        restDurationEditText.setText(BagZombiePreferences.getCustomRestDuration(this).toString());

        roundCountEditText.addTextChangedListener(new PreferencesTextWatcher() {
            @Override
            protected void saveToPreferences(Context context, int number) {
                BagZombiePreferences.setCustomRoundCount(context, number);
            }
        });

        roundDurationEditText.addTextChangedListener(new PreferencesTextWatcher() {
            @Override
            protected void saveToPreferences(Context context, int number) {
                BagZombiePreferences.setCustomRoundDuration(context, number);
            }
        });

        roundDurationEditText.addTextChangedListener(new PreferencesTextWatcher() {
            @Override
            protected void saveToPreferences(Context context, int number) {
                BagZombiePreferences.setCustomRestDuration(context, number);
            }
        });

        combinationSpinner.setSelection(BagZombiePreferences.getCustomComboChoice(this));

        combinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isCustomCombo = (position == CUSTOM_COMBO_POSITION);
                customPunchesLayout.setVisibility(isCustomCombo ? View.VISIBLE : View.GONE);

                BagZombiePreferences.setCustomComboChoice(getApplicationContext(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                customPunchesLayout.setVisibility(View.GONE);
            }
        });

        rebuildComboGrid();

        clearPunchesLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BagZombiePreferences.setCustomComboSet(
                        getApplicationContext(),
                        new HashSet<String>());
                rebuildComboGrid();
            }
        });

        startFightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(IntentParameters.createManualFightIntent(
                        getApplicationContext(),
                        combinationSpinner.getSelectedItemPosition()));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // When rotating the phone, the combo EditText doesn't receive a focus lost event to save
        // the last change.
        saveCustomPunchesToPreferences();
    }

    private void rebuildComboGrid() {
        customPunchesGrid.removeAllViews();
        List<String> customComboList =
                new ArrayList<>(BagZombiePreferences.getCustomComboSet(this));
        Collections.sort(customComboList);
        for (String combo : customComboList) {
            addCustomPunchEditText(combo);
        }
        addCustomPunchEditText(""); // Blank one for user to add a new combo

        recalculateCustomPunchesGridColumnCount();
    }

    private void recalculateCustomPunchesGridColumnCount() {
        customPunchesGrid.post(new Runnable() {
            @Override
            public void run() {
                int childCount = customPunchesGrid.getChildCount();
                if (childCount > 0) {
                    // Calculate column count.
                    View lastChild = customPunchesGrid.getChildAt(childCount - 1);
                    int childWidth = lastChild.getWidth();
                    int gridWidth = customPunchesGrid.getWidth();
                    if (childWidth == 0) {
                        // The view is not attached.
                        return;
                    }
                    int columnCount = gridWidth / childWidth;
                    if (customPunchesGrid.getColumnCount() == columnCount) {
                        return;
                    }

                    // Remove children if the column count change requires a layout change.
                    // GridLayout has a limitation where it doesn't smartly lay out rows again.
                    List<View> children = new LinkedList<>();
                    if (columnCount < customPunchesGrid.getChildCount()) {
                        while (customPunchesGrid.getChildCount() > 0) {
                            children.add(customPunchesGrid.getChildAt(0));
                            customPunchesGrid.removeViewAt(0);
                        }
                    }

                    // Set column count.
                    customPunchesGrid.setColumnCount(columnCount);

                    // Re-attach children
                    while (children.size() > 0) {
                        View child = children.remove(0);
                        child.setLayoutParams(new GridLayout.LayoutParams());
                        customPunchesGrid.addView(child);
                    }
                }
            }
        });
    }

    private void addCustomPunchEditText(String combo) {
        final EditText customPunchEditText = new EditText(this);
        customPunchEditText.setText(combo);
        customPunchEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        customPunchEditText.setKeyListener(DigitsKeyListener.getInstance("123456 "));
        customPunchEditText.setHint(R.string.custom_combo_hint);
        customPunchesGrid.addView(customPunchEditText);

        customPunchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    onCustomComboTextChanged(customPunchEditText);
                }
            }
        });

        customPunchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ignore.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ignore.
            }

            @Override
            public void afterTextChanged(Editable s) {
                int index = customPunchesGrid.indexOfChild(customPunchEditText);
                int childCount = customPunchesGrid.getChildCount();
                boolean isLast = (index == childCount - 1);
                boolean isBlank = (customPunchEditText.getText().toString().trim().length() == 0);
                if (isLast && !isBlank) {
                    addCustomPunchEditText("");
                }
            }
        });
    }

    private void onCustomComboTextChanged(EditText customPunchEditText) {
        int index = customPunchesGrid.indexOfChild(customPunchEditText);

        // Clean up string
        String combo = customPunchEditText.getText().toString();
        String cleanCombo = getValidComboString(customPunchEditText);
        if (!cleanCombo.equals(combo)) {
            customPunchEditText.setText(cleanCombo);
        }

        // Remove or add an EditText.
        int childCount = customPunchesGrid.getChildCount();
        boolean isLast = (index == childCount - 1);
        boolean isBlank = (cleanCombo.trim().length() == 0);
        if (!isLast && isBlank) {
            customPunchesGrid.removeView(customPunchEditText);
        } else if (isLast && !isBlank) {
            addCustomPunchEditText("");
        }

        // Save to preferences
        saveCustomPunchesToPreferences();
    }

    private void saveCustomPunchesToPreferences() {
        Set<String> customComboSet = new HashSet<>(customPunchesGrid.getChildCount());
        for (int i = 0; i < customPunchesGrid.getChildCount(); i++) {
            EditText editText = (EditText) customPunchesGrid.getChildAt(i);
            String combo = getValidComboString(editText);
            if (combo.length() > 0) {
                customComboSet.add(combo);
            }
        }
        BagZombiePreferences.setCustomComboSet(this, customComboSet);
    }

    private static String getValidComboString(EditText editText) {
        return getValidComboString(editText.getText().toString());
    }

    static String getValidComboString(String str) {
        str = str.replaceAll("[,;]", " "); // Convert separators to spaces.
        str = str.replaceAll("[^123456 ]", " "); // Remove any non-numbers because they are noise.
        str = str.replaceAll(" +", " "); // Collapse all separators to a single space.
        str = str.replaceAll("([123456])(?=[123456])", "$1 "); // Add spaces between digits.

        int maxLength = "X X X X".length();
        if (str.length() > maxLength) {
            str = str.substring(0, maxLength);
        }
        return str.trim();
    }

    /**
     * A {@link TextWatcher} that makes saving the contents of the {@link EditText} to preferences
     * a little easier.
     */
    private abstract class PreferencesTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Nothing to do.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Nothing to do.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            try {
                int number = Integer.parseInt(str);
                saveToPreferences(getApplicationContext(), number);
            } catch (NumberFormatException ex) {
                // Ignore text that cannot be parsed. The user may be middle in typing something.
            }
        }

        protected abstract void saveToPreferences(Context context, int number);
    }
}
