/*
 * Created by Filipe André Rodrigues on 18-06-2019 22:44
 */

package ui.widgets;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.input.Clipboard;

import java.util.Locale;

/**
 * A JFXTextField widget that only allows decimal numeric input.
 */
/**
 * Define a text field that accept only doubles.
 */
public class JFXDecimalTextField extends JFXTextField {

    public JFXDecimalTextField() {
    }

    @Override
    public void replaceText(int start, int end, String text) {
        String newText = getNewText(start, end, text);
        if (!text.isEmpty() // Always allow text deletion
                && (!newText.equals("-") && !newText.equals(".") && !newText.equals("-."))) { //NOI18N
            try {
                if (newText.toLowerCase(Locale.ROOT).contains("d") || newText.toLowerCase(Locale.ROOT).contains("f")) { //NOI18N
                    // 'd' and 'f' are valid for a Double,
                    // but we don't want to accept them in the editor
                    return;
                }
                // Replace ',' by '.'
                newText = newText.replace(',', '.'); //NOI18N
                Double.parseDouble(newText);
            } catch (NumberFormatException e) {
                return;
            }
        }
        // Replace ',' by '.'
        text = text.replace(',', '.'); //NOI18N
        super.replaceText(start, end, text);
    }

    @Override
    public void paste() {
        String strToPaste = Clipboard.getSystemClipboard().getString();
        try {
            Double.parseDouble(strToPaste);
        } catch (NumberFormatException e) {
            return;
        }
        super.paste();
    }

    private String getNewText(int start, int end, String text) {
        String oldText = getText();
        String toReplace = oldText.substring(start, end);
        String newText;
        if (toReplace.isEmpty()) {
            // start/end is outside oldText ==> add
            newText = oldText + text;
        } else {
            String headerStr = oldText.substring(0, start);
            String trailerStr = ""; //NOI18N
            if (end < oldText.length()) {
                trailerStr = oldText.substring(end, oldText.length());
            }
            newText = headerStr + text + trailerStr;
        }
        return newText;
    }

}