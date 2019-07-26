package com.example.keyboardforkeystrokedynamics;

import android.app.Service;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView keyboardView;
    private Keyboard keyboard;

    private boolean isCaps = false;   // Caps Lock

    private void showMsg(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void saveKeyLog(int primaryCode, int type) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LOG/";
        File file = new File(dirPath, "LOG.TXT");
        try {
            if(!file.exists())
                file.mkdirs();
            BufferedWriter bfw = new BufferedWriter((new FileWriter(dirPath+"LOG.txt",true)));
            bfw.write(Integer.toString(primaryCode));
            if(type == 0) {
                bfw.write(" P\n");
            } else {
                bfw.write(" R\n");
            }
            bfw.flush();
            bfw.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.keys_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode) {
        // Shows message when Certain Key is Pressed
        showMsg("Press - " + Integer.toString(primaryCode));

        saveKeyLog(primaryCode, 0);
    }

    @Override
    public void onRelease(int primaryCode) {
        // Shows message when Certain Key is Released
        showMsg("Release - "+Integer.toString(primaryCode));

        saveKeyLog(primaryCode, 1);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null)
            return;

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE :
                CharSequence selectedText = inputConnection.getSelectedText(0);
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);
                } else {
                    inputConnection.commitText("", 1);
                }
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                inputConnection.commitText(String.valueOf(code), 1);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}