package com.lipengwei.myalarm;
import com.lipengwei.myalarm.provider.Alarm;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LabelDialogFragment extends DialogFragment {

    private static final String KEY_LABEL = "label";
    private static final String KEY_ALARM = "alarm";
    private static final String KEY_TAG = "tag";

    private EditText mLabelBox;

    public static LabelDialogFragment newInstance(Alarm alarm, String label, String tag) {
        final LabelDialogFragment frag = new LabelDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_LABEL, label);
        args.putParcelable(KEY_ALARM, alarm);
        args.putString(KEY_TAG, tag);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final String label = bundle.getString(KEY_LABEL);
        final Alarm alarm = bundle.getParcelable(KEY_ALARM);
        final String tag = bundle.getString(KEY_TAG);

        final View view = inflater.inflate(R.layout.label_dialog, container, false);

        mLabelBox = (EditText) view.findViewById(R.id.labelBox);
        mLabelBox.setText(label);
        mLabelBox.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    set(alarm, tag);
                    return true;
                }
                return false;
            }
        });
        mLabelBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLabelBoxBackground(s == null || TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        setLabelBoxBackground(TextUtils.isEmpty(label));

        final Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        final Button setButton = (Button) view.findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set(alarm,tag);
            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }
    
    private void set(Alarm alarm, String tag) {
        String label = mLabelBox.getText().toString();
        if (label.trim().length() == 0) {
            label = "";
        }

        if (alarm != null) {
            set(alarm, tag, label);
        }
    }

    private void set(Alarm alarm, String tag, String label) {
        final Activity activity = getActivity();
        // TODO just pass in a listener in newInstance()
        if (activity instanceof AlarmLabelDialogHandler) {
            ((MainActivity) getActivity()).onDialogLabelSet(alarm, label, tag);
        }
        dismiss();
    }

    private void setLabelBoxBackground(boolean emptyText) {
        mLabelBox.setBackgroundResource(emptyText ?
                R.drawable.bg_edittext_default : R.drawable.bg_edittext_activated);
    }

    interface AlarmLabelDialogHandler {
        void onDialogLabelSet(Alarm alarm, String label, String tag);
    }

}
