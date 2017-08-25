package com.systers.conference.profile;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Attendee;
import com.systers.conference.testutils.DrawableMatcherHelper;
import com.systers.conference.testutils.PermissionGranterHelper;
import com.systers.conference.testutils.ToastMatcher;
import com.systers.conference.util.FirebaseAuthUtil;
import com.systers.conference.util.PermissionsUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.realm.Realm;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.systers.conference.testutils.TextInputLayoutHelper.hasTextInputLayoutErrorText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileActivity_InteractionTests {
    private final String[] RUN_TIME_PERMISSIONS = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ? new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE} : new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Rule
    public ActivityTestRule<EditProfileActivity> editProfileActivityActivityTestRule = new ActivityTestRule<>(EditProfileActivity.class);
    private Context context = InstrumentationRegistry.getTargetContext();
    private Attendee attendee;
    private Realm realm;

    @Before
    public void fetch_attendee_fromRealm() {
        realm = Realm.getDefaultInstance();
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(realm);
        attendee = realmDataRepository.getAttendeeFromRealmSync(FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser().getUid());
    }

    @After
    public void close_Realm() {
        realm.close();
    }

    @RequiresApi(23)
    @Test
    public void permissionIfNeeded_granted_dialogShown() {
        onView(withId(R.id.edit_icon)).perform(click());
        if (PermissionsUtil.areAllRunTimePermissionsGranted(RUN_TIME_PERMISSIONS, context)) {
            onView(withText(context.getString(R.string.edit_avatar))).check(matches(isCompletelyDisplayed()));
        } else {
            PermissionGranterHelper.grantPermissions();
            onView(withText(context.getString(R.string.edit_avatar))).check(matches(isCompletelyDisplayed()));
        }
    }

    @RequiresApi(23)
    @Test
    public void permissionIfNeeded_denied_rationaleShown() {
        onView(withId(R.id.edit_icon)).perform(click());
        if (PermissionsUtil.areAllRunTimePermissionsGranted(RUN_TIME_PERMISSIONS, context)) {
            onView(withText(context.getString(R.string.edit_avatar))).check(matches(isCompletelyDisplayed()));
        } else {
            PermissionGranterHelper.denyPermissions();
            onView(withText(context.getString(R.string.permissions_dialog_title))).check(matches(isCompletelyDisplayed()));
        }
    }

    @RequiresApi(23)
    @Test
    public void permissionIfNeeded_denied_rationaleShown_accepted_askAgain() {
        onView(withId(R.id.edit_icon)).perform(click());
        if (PermissionsUtil.areAllRunTimePermissionsGranted(RUN_TIME_PERMISSIONS, context)) {
            onView(withText(context.getString(R.string.edit_avatar))).check(matches(isCompletelyDisplayed()));
        } else {
            PermissionGranterHelper.denyPermissions();
            onView(withText(context.getString(R.string.permissions_dialog_title))).check(matches(isCompletelyDisplayed()));
            onView(withId(android.R.id.button1)).perform(click());
            PermissionGranterHelper.grantPermissions();
        }
    }

    @RequiresApi(23)
    @Test
    public void permissionIfNeeded_denied_rationaleShown_declined_showSnackBar() {
        onView(withId(R.id.edit_icon)).perform(click());
        if (PermissionsUtil.areAllRunTimePermissionsGranted(RUN_TIME_PERMISSIONS, context)) {
            onView(withText(context.getString(R.string.edit_avatar))).check(matches(isCompletelyDisplayed()));
        } else {
            PermissionGranterHelper.denyPermissions();
            onView(withText(context.getString(R.string.permissions_dialog_title))).check(matches(isCompletelyDisplayed()));
            onView(withId(android.R.id.button2)).perform(click());
            onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(context.getString(R.string.permissions_snackbar))))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void back_pressed_navigatesToProfileView() {
        pressBack();
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(context.getString(R.string.profile_title))));
    }

    @Test
    public void back_pressed_withEmptyField_showsErrorMessage() {
        onView(withId(R.id.edit_first_name)).perform(replaceText(""), closeSoftKeyboard());
        pressBack();
        onView(withId(R.id.text_input_firstname)).check(matches(hasTextInputLayoutErrorText(context.getString(R.string.error_field_required))));
    }

    @Test
    public void back_pressed_withChanges_showsDialog() {
        if (attendee.getFirstName() != null) {
            onView(withId(R.id.edit_first_name)).perform(replaceText(attendee.getFirstName() + "test"), closeSoftKeyboard());
        } else {
            onView(withId(R.id.edit_first_name)).perform(replaceText("test"), closeSoftKeyboard());
        }
        pressBack();
        onView(withText(context.getString(R.string.discard_changes))).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void saveClicked_onDialog_savesChangesAndGoesToParent() {
        if (attendee.getFirstName() != null) {
            onView(withId(R.id.edit_first_name)).perform(replaceText(attendee.getFirstName() + "test"), closeSoftKeyboard());
        } else {
            onView(withId(R.id.edit_first_name)).perform(replaceText("test"), closeSoftKeyboard());
        }
        pressBack();
        onView(withText(context.getString(R.string.discard_changes))).check(matches(isCompletelyDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText(R.string.save_toast)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(context.getString(R.string.profile_title))));
    }

    @Test
    public void discardClicked_onDialog_discardsChangesAndGoesToParent() {
        if (attendee.getFirstName() != null) {
            onView(withId(R.id.edit_first_name)).perform(replaceText(attendee.getFirstName() + "test"), closeSoftKeyboard());
        } else {
            onView(withId(R.id.edit_first_name)).perform(replaceText("test"), closeSoftKeyboard());
        }
        pressBack();
        onView(withText(context.getString(R.string.discard_changes))).check(matches(isCompletelyDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(context.getString(R.string.profile_title))));
    }

    @Test
    public void save_pressed_withEmptyField_showsErrorMessage() {
        onView(withId(R.id.edit_first_name)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.action_check)).perform(click());
        onView(withId(R.id.text_input_firstname)).check(matches(hasTextInputLayoutErrorText(context.getString(R.string.error_field_required))));
    }

    @Test
    public void save_pressed_withChanges_savesChangesAndGoesToParent() {
        if (attendee.getFirstName() != null) {
            onView(withId(R.id.edit_first_name)).perform(replaceText(attendee.getFirstName() + "test"), closeSoftKeyboard());
        } else {
            onView(withId(R.id.edit_first_name)).perform(replaceText("test"), closeSoftKeyboard());
        }
        onView(withId(R.id.action_check)).perform(click());
        onView(withText(R.string.save_toast)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(context.getString(R.string.profile_title))));
    }

    @Test
    public void save_pressed_withoutChanges_goesToParent() {
        onView(withId(R.id.action_check)).perform(click());
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(context.getString(R.string.profile_title))));
    }

    @Test
    public void deleteAvatar_clicked_replacesDrawable() {
        onView(withId(R.id.edit_icon)).perform(click());
        onView(withText(context.getString(R.string.delete_avatar))).check(matches(isCompletelyDisplayed()));
        onView(withText(context.getString(R.string.delete_avatar))).perform(click());
        onView(withId(R.id.avatar)).check(matches(DrawableMatcherHelper.withDrawable(R.drawable.male_icon_9_glasses)));
    }

    @Test
    public void checkEmailAndAttendeeType_notEditable() {
        onView(withId(R.id.edit_email)).check(matches(not(isEnabled())));
        onView(withId(R.id.edit_attendee_type)).check(matches(not(isEnabled())));
    }
}
