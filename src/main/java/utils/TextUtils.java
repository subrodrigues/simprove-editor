/*
 * Created by Filipe André Rodrigues on 20-03-2019 18:05
 */

package utils;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class TextUtils {

    /**
     * Method that returns a Listener to control the max number os characters allowed by an editable JFXComboBox.
     *
     * @param comboBox that will receive the listener
     * @return the ChangeListener expected, to be used with the respective JFXComboBox
     */
    public static ChangeListener<String> getComboBoxTextInputMaxCharactersListener(JFXComboBox comboBox){
        return getComboBoxTextInputMaxCharactersListener(comboBox, ConstantUtils.ACTION_STATE_INPUT_MAX_NUMBER_CHARS);
    }

    /**
     * Method that returns a Listener to control the max number os characters allowed by an editable JFXComboBox.
     *
     * @param comboBox that will receive the listener
     * @param numMaxChars that will be used as a limit
     * @return the ChangeListener expected, to be used with the respective JFXComboBox
     */
    public static ChangeListener<String> getComboBoxTextInputMaxCharactersListener(JFXComboBox comboBox, int numMaxChars){
        return getComboBoxTextInputMaxCharactersListener(comboBox.getEditor(), numMaxChars);
    }

    /**
     * Method that returns a Listener to control the max number os characters allowed by an editable JFXTextField.
     *
     * @param textField that will receive the listener
     * @return the ChangeListener expected, to be used with the respective JFXComboBox
     */
    public static ChangeListener<String> getComboBoxTextInputMaxCharactersListener(TextField textField){
        return getComboBoxTextInputMaxCharactersListener(textField, ConstantUtils.ACTION_STATE_INPUT_MAX_NUMBER_CHARS);
    }

    /**
     * Method that returns a Listener to control the max number os characters allowed by an editable JFXTextField.
     *
     * @param textField that will receive the listener
     * @param numMaxChars that will be used as a limit
     * @return the ChangeListener expected, to be used with the respective JFXComboBox
     */
    public static ChangeListener<String> getComboBoxTextInputMaxCharactersListener(TextField textField, int numMaxChars){
        return new javafx.beans.value.ChangeListener<String>() {

            @Override
            public void changed(
                    ObservableValue<? extends String> observableValue,
                    String oldValue, String newValue) {

                if (newValue.length() > numMaxChars) {
                    textField.setText(newValue.substring(0, numMaxChars));
                }
            }
        };
    }

}
