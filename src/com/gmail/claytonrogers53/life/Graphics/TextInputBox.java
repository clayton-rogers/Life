package com.gmail.claytonrogers53.life.Graphics;

import javax.swing.*;

/**
 * Allows the user to enter any text into a box which can be later retrieved. The calling method
 * (draw thread) is blocked while the text is entered. Width must be set. Height is auto set based
 * on contents.
 *
 * Created by Clayton on 26/1/2015.
 */
public class TextInputBox extends TextBox {

    /**
     * When the text box is clicked, it opens a dialog to enter text in. The resulting text is then
     * displayed in the text box.
     *
     * @param localX
     *        The x component of the click location. (Not used.)
     *
     * @param localY
     *        The y component of the click location. (Not used.)
     */
    @Override
    void clicked(int localX, int localY) {
        super.clicked(localX, localY);

        String localText =
                JOptionPane.showInputDialog(null,
                        "Enter some text:","Text Input Dialog", JOptionPane.PLAIN_MESSAGE);
        setText(localText);
    }
}
