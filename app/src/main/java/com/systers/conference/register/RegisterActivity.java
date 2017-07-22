package com.systers.conference.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heinrichreimersoftware.singleinputform.SingleInputFormActivity;
import com.heinrichreimersoftware.singleinputform.steps.CheckBoxStep;
import com.heinrichreimersoftware.singleinputform.steps.OptionStep;
import com.heinrichreimersoftware.singleinputform.steps.Step;
import com.heinrichreimersoftware.singleinputform.steps.TextStep;
import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.api.DataDownloadManager;
import com.systers.conference.callback.ObjectResponseCallback;
import com.systers.conference.model.AttendeeId;
import com.systers.conference.model.Question;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A screen that allows user to verify details and register for the event.
 */
public class RegisterActivity extends SingleInputFormActivity implements ObjectResponseCallback<AttendeeId> {

    private static final String LOG_TAG = LogUtils.makeLogTag(RegisterActivity.class);
    List<Question> questions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (AccountUtils.getRegisterVisited(this)) {
            startActivity(new Intent(this, MainActivity.class));
            ActivityCompat.finishAffinity(this);
        }
    }

    @Override
    protected List<Step> onCreateSteps() {
        Intent intent = getIntent();
        questions = new Gson().fromJson(intent.getStringExtra(getString(R.string.registration)), new TypeToken<List<Question>>() {
        }.getType());
        List<Step> steps = new ArrayList<>();
        setInputGravity(Gravity.CENTER);
        if (questions != null) {
            for (Question question : questions) {
                switch (question.getInputType()) {
                    case 1:
                        if (question.getFieldName().contains("email")) {
                            steps.add(new TextStep.Builder(this, question.getFieldName())
                                    .title(question.getDisplayName()).error(getString(R.string.error_invalid_email)).details(question.getDisplayName())
                                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                    .validator(new TextStep.Validator() {
                                        @Override
                                        public boolean validate(String input) {
                                            return Patterns.EMAIL_ADDRESS.matcher(input).matches();
                                        }
                                    }).build());
                        } else {
                            steps.add(new TextStep.Builder(this, question.getFieldName())
                                    .title(question.getDisplayName()).error("Error").details(question.getDisplayName()).inputType(InputType.TYPE_CLASS_TEXT).build());
                        }
                        break;
                    case 17:
                    case 16:
                        //TODO: Replace this hardcoded options with real options.
                        String[] options = {"Option1", "Option2", "Option3"};
                        steps.add(new OptionStep.Builder(this, question.getFieldName()).title(question.getDisplayName()).error("Error")
                                .details(question.getDisplayName()).options(options).build());
                        break;
                    case 3:
                        steps.add(new CheckBoxStep.Builder(this, question.getFieldName()).
                                title(question.getDisplayName())
                                .error(getString(R.string.error))
                                .details(question.getDisplayName())
                                .text(getString(R.string.yes))
                                .build());
                        break;
                    default:
                        LogUtils.LOGE(LOG_TAG, "No data found");
                        break;
                }
            }
        }
        return steps;
    }

    @Override
    protected void onFormFinished(Bundle bundle) {
        Map<String, String> responses = new HashMap<>();
        for (Question question : questions) {
            switch (question.getInputType()) {
                case 1:
                    responses.put(question.getFieldName(), TextStep.text(bundle, question.getFieldName()));
                    break;
                default:
                    LogUtils.LOGE(LOG_TAG, "It is checkbox or radio");
                    break;
            }
        }
        DataDownloadManager.getInstance().createAttendee(this, responses);
    }

    @Override
    public void OnSuccess(AttendeeId response) {
        AccountUtils.setRegisterVisited(this);
        Toast.makeText(this, getString(R.string.registration_successfull), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        ActivityCompat.finishAffinity(this);
    }

    @Override
    public void OnFailure(Throwable error) {
        Toast.makeText(this, getString(R.string.registration_unsuccessful), Toast.LENGTH_LONG).show();
        ActivityCompat.finishAffinity(this);
        startActivity(getIntent());
    }
}

