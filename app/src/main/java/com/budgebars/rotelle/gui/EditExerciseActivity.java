package com.budgebars.rotelle.gui;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.IncomingFileManager;
import com.budgebars.rotelle.files.InternalFileManager;
import com.budgebars.rotelle.gui.adapters.EditIntervalAdapter;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.editable.EditableExercise;

public class EditExerciseActivity extends AppCompatActivity {

  private static final String TAG = EditExerciseActivity.class.getName();

  private static final String IMPORT_TITLE = "Import Exercise";

  private static final String EDIT_TITLE = "Edit Exercise";

  private static final String EMPTY_TITLE_MESSAGE = "Cannot have an empty exercise title.";

  private static final String DUPLICATE_TITLE_MESSAGE =
      "An exercise with that name already exists. Overwrite?";

  private static final String PROCEED_LABEL = "Ok";

  private static final String CANCEL_LABEL = "Cancel";

  private EditableExercise editableExercise;

  private EditIntervalAdapter adapter;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.activity_edit_exercise);

    Intent intent = this.getIntent();
    if (intent.getScheme() != null) {
      this.setTitle(EditExerciseActivity.IMPORT_TITLE);

      if (intent.getScheme().equals(ContentResolver.SCHEME_FILE)
          || intent.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
        this.editableExercise = new EditableExercise(this.retrieveExerciseFromPassedDocument(intent));
      } else {
        Log.e(EditExerciseActivity.TAG, "Unrecognized content scheme: " + intent.getScheme());
      }
    } else {
      this.setTitle(EditExerciseActivity.EDIT_TITLE);
      this.editableExercise = (EditableExercise)
        this.getIntent().getSerializableExtra(ExerciseListingActivity.EDITABLE_EXERCISE);
    }

    EditText titleEditor = this.findViewById(R.id.EditTitleView);
    titleEditor.setText(this.editableExercise.name());
    titleEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(final View view, final boolean hasFocus) {
        if (!hasFocus) {
          EditText titleText = (EditText) view;
          String updated = titleText.getText().toString();
          EditExerciseActivity.this.editableExercise.changeName(updated);
        }
      }
    });

    final ListView list = this.findViewById(R.id.IntervalListView);
    list.setItemsCanFocus(true);

    this.adapter = new EditIntervalAdapter(this.editableExercise, this);
    list.setAdapter(this.adapter);

    this.adapter.addItemAddedConsumer(new EditIntervalAdapter.IntervalAddedConsumer() {
      @Override
      public void intervalAddedToAdapter() {
        list.setSelection(EditExerciseActivity.this.adapter.getCount() - 1);
      }
    });

    Button addIntervalButton = this.findViewById(R.id.AddIntervalButton);
    addIntervalButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        EditExerciseActivity.this.editableExercise.addInterval();
      }
    });

    final Button saveButton = this.findViewById(R.id.SaveExerciseButton);
    saveButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
          // Make sure focus comes off of edit views so that all changes are committed
          View currentFocus = EditExerciseActivity.this.getCurrentFocus();
          if (currentFocus != null) {
            currentFocus.clearFocus();
          }
          saveButton.requestFocus();

          if (EditExerciseActivity.this.isTitleEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditExerciseActivity.this);
            builder.setMessage(EditExerciseActivity.EMPTY_TITLE_MESSAGE)
                .setPositiveButton(EditExerciseActivity.PROCEED_LABEL,
                  new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                      // Do nothing -- just return to activity so the user can plus a title.
                    }
                  });

            builder.create().show();
            return;
          }

          final Exercise saveTarget = EditExerciseActivity.this.editableExercise.toExercise();

          final InternalFileManager fileManager =
              new InternalFileManager(EditExerciseActivity.this);

          if (fileManager.hasFileForExerciseName(saveTarget.name())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditExerciseActivity.this);
            builder.setMessage(EditExerciseActivity.DUPLICATE_TITLE_MESSAGE)
                .setPositiveButton(EditExerciseActivity.PROCEED_LABEL,
                  new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                      fileManager.writeExerciseToFile(saveTarget, true);
                      EditExerciseActivity.this.goBackToListingActivity();
                    }
                  })
                .setNegativeButton(EditExerciseActivity.CANCEL_LABEL,
                    new DialogInterface.OnClickListener() {
                      public void onClick(final DialogInterface dialog, final int which) {
                        // Do nothing so user has a chance to change exercise name
                      }
                  });

              builder.create().show();
          } else {
            fileManager.writeExerciseToFile(saveTarget, false);
            EditExerciseActivity.this.goBackToListingActivity();
          }
        }
    });
  }

  private void goBackToListingActivity() {
    Intent intent = new Intent(this, ExerciseListingActivity.class);
    this.startActivity(intent);
  }

  private boolean isTitleEmpty() {
    EditText titleText = this.findViewById(R.id.EditTitleView);
    return titleText.getText().toString().trim().isEmpty();
  }

  private Exercise retrieveExerciseFromPassedDocument(final Intent intent) {
    Uri uri = intent.getData();

    IncomingFileManager manager = new IncomingFileManager(this);

    return manager.fromUri(uri);
  }
}
