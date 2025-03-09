package local.soundexample.sound.explorer;

import local.soundexample.sound.explorer.controller.SoundController;
import local.soundexample.sound.explorer.model.SoundModel;
import local.soundexample.sound.explorer.view.SoundView;


/**
 *
 * @author pc
 */

public class SoundExplorer {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            SoundModel model = new SoundModel();
            SoundView view = new SoundView();
            new SoundController(model, view);
            view.setVisible(true);
        });
    }
}