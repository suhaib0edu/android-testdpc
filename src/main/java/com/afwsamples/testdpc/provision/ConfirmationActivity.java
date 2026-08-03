/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afwsamples.testdpc.provision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.afwsamples.testdpc.R;

/**
 * Confirmation screen shown BEFORE any real provisioning logic runs.
 *
 * <p>This activity is registered on the {@code android.app.action.GET_PROVISIONING_MODE}
 * intent-filter INSTEAD OF {@link GetProvisioningModeActivity}, so it is guaranteed to be
 * the very first screen the system launches during QR/NFC provisioning. It does nothing except
 * show a clear message to confirm the app has actually started, then forwards execution to
 * {@link GetProvisioningModeActivity} only after the user taps "Continue".
 */
public class ConfirmationActivity extends Activity {

  private static final String TAG = ConfirmationActivity.class.getSimpleName();

  private static final int REQUEST_CODE_REAL_PROVISIONING_MODE = 1001;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "ConfirmationActivity displayed - provisioning has NOT started yet");

    setContentView(R.layout.activity_qr_confirmation);

    Button continueButton = findViewById(R.id.confirmation_continue_button);
    continueButton.setOnClickListener(v -> startRealProvisioningFlow());
  }

  private void startRealProvisioningFlow() {
    Log.i(TAG, "Continue tapped - forwarding to GetProvisioningModeActivity");

    // Forward the exact same intent (extras included) to the real provisioning-mode activity.
    Intent forwardIntent = new Intent(this, GetProvisioningModeActivity.class);
    if (getIntent() != null) {
      forwardIntent.putExtras(getIntent());
      forwardIntent.setAction(getIntent().getAction());
    }

    startActivityForResult(forwardIntent, REQUEST_CODE_REAL_PROVISIONING_MODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_REAL_PROVISIONING_MODE) {
      // Propagate whatever GetProvisioningModeActivity decided back to the system,
      // since the system is waiting on OUR result as the registered handler.
      setResult(resultCode, data);
      finish();
    }
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_CANCELED);
    super.onBackPressed();
  }
}
