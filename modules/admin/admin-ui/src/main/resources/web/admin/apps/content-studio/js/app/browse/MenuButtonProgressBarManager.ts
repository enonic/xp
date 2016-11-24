import "../../api.ts";

import MenuButton = api.ui.button.MenuButton;
import ProgressBar = api.ui.ProgressBar;

export class MenuButtonProgressBarManager {

    private static progressBar: ProgressBar;

    private static progressHandler: () => void;

    static updateProgressHandler(progressHandler: () => void) {
        if (MenuButtonProgressBarManager.progressBar) {
            MenuButtonProgressBarManager.progressBar.unClicked(MenuButtonProgressBarManager.progressHandler || (() => {
                }));
            MenuButtonProgressBarManager.progressHandler = progressHandler;
            MenuButtonProgressBarManager.progressBar.onClicked(MenuButtonProgressBarManager.progressHandler);
        }
    }

    static getProgressBar(): ProgressBar {
        if (!MenuButtonProgressBarManager.progressBar) {
            MenuButtonProgressBarManager.progressBar = new api.ui.ProgressBar(0);
        }
        return MenuButtonProgressBarManager.progressBar;
    }
}
