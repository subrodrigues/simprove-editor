/*
 * Created by Filipe André Rodrigues on 28-02-2019 20:51
 */

package ui.widgets;

import com.jfoenix.controls.JFXTextField;

/**
 * A JJFXTextField widget that only allows numeric input.
 */
public class JFXNumericTextField extends JFXTextField {

    @Override
    public void replaceText(int start, int end, String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text) {
        return text.matches("[0-9]*");
    }

}
