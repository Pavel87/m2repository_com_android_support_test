/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.espresso.remote;


import android.support.test.espresso.EspressoException;

/**
 * Espresso exception representing any protocol related errors in a multi-process environment
 */
public class RemoteProtocolException extends RuntimeException
    implements EspressoException {

  public RemoteProtocolException(String description) {
    super(description);
  }

  public RemoteProtocolException(String description, Throwable cause) {
    super(description, cause);
  }
}
