/*
 * Copyright (C) 2017 The Android Open Source Project
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

package android.support.test.services.shellexecutor;

import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.support.test.services.speakeasy.SpeakEasyProtocol.PublishResult;
import android.util.Log;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Server to create ShellExecutors */
final class ShellCommandExecutorServer {

  private static final String TAG = "ShellCmdExec";
  private static final ExecutorService executor =
      Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(TAG + " #%d").build());

  private final ShellCommandExecutor shellCommandExecutor = new ShellCommandExecutor(executor);
  private final HandlerThread handlerThread = new HandlerThread("SpeakEasyPublish");

  ShellCommandExecutorServer() {}

  String start() throws InterruptedException {
    if (!handlerThread.isAlive()) {
      handlerThread.setDaemon(true);
      handlerThread.start();
    }

    Command.Stub commandStub =
        new Command.Stub() {
          @Override
          public void execute(
              String command,
              List<String> parameters,
              @SuppressWarnings("unchecked")
                  Map shellEnv, // shellEnv comes from aidl and must a Map without type.
              boolean executeThroughShell,
              ParcelFileDescriptor pdf) {

            OutputStream outputReceiver = new ParcelFileDescriptor.AutoCloseOutputStream(pdf);

            try {
              ShellCommand commandObject =
                  new ShellCommand(
                      command, parameters, (Map<String, String>) shellEnv, executeThroughShell);
              shellCommandExecutor.execute(commandObject, outputReceiver);
            } catch (IOException e) {
              Log.w(TAG, "Running command threw an exception", e);
            } finally {
              try {
                outputReceiver.close();
              } catch (IOException ioe) {
                Log.w(TAG, "Close threw an exception", ioe);
              }
            }
          }
        };

    PublishResult result =
        BlockingPublish.getResult(handlerThread.getLooper(), commandStub.asBinder());
    if (result.published) {
      String key = result.key;
      return key;
    } else {
      throw new RuntimeException(result.error);
    }
  }
}
