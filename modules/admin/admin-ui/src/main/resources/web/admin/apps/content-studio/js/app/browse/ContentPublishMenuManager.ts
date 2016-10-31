import "../../api.ts";
import {ContentTreeGridActions} from "./action/ContentTreeGridActions";
import {ContentPublishPromptEvent} from "./ContentPublishPromptEvent";

import MenuButton = api.ui.button.MenuButton;
import ProgressBar = api.ui.ProgressBar;

export class ContentPublishMenuManager {

    private static publishMenuButton: MenuButton;
    private static progressBar: ProgressBar;
    
    constructor(actions: ContentTreeGridActions) {
        this.initPublishMenuButton(actions);
    }

    private initPublishMenuButton(actions: ContentTreeGridActions) {
        let mainAction = actions.PUBLISH_CONTENT;
        let menuAction = [actions.PUBLISH_TREE_CONTENT, actions.UNPUBLISH_CONTENT];

        ContentPublishMenuManager.publishMenuButton = new MenuButton(mainAction, menuAction);
        ContentPublishMenuManager.publishMenuButton.addClass('content-publish-menu');
    }

    static getProgressBar(): ProgressBar {
        if (!ContentPublishMenuManager.progressBar) {
            let progressBar = new api.ui.ProgressBar(0);
            progressBar.onClicked(() => {
                new ContentPublishPromptEvent([]).fire();
            });
            ContentPublishMenuManager.getPublishMenuButton().appendChild(progressBar);

            ContentPublishMenuManager.progressBar = progressBar;
        }

        return ContentPublishMenuManager.progressBar;
    }

    static getPublishMenuButton(): MenuButton {
        if (!ContentPublishMenuManager.publishMenuButton) {
            throw "Publish button is not available";
        }
        return ContentPublishMenuManager.publishMenuButton;
    }
}