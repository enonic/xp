import "../../api.ts";
import {ContentTreeGridActions} from "./action/ContentTreeGridActions";

import MenuButton = api.ui.button.MenuButton;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;

export class ContentPublishMenuManager {

    private publishMenuButton: MenuButton;

    constructor(actions: ContentTreeGridActions) {
        this.initPublishMenuButton(actions);
    }

    private initPublishMenuButton(actions: ContentTreeGridActions) {
        let mainAction = actions.PUBLISH_CONTENT;
        let menuAction = [actions.PUBLISH_TREE_CONTENT, actions.UNPUBLISH_CONTENT];

        this.publishMenuButton = new MenuButton(mainAction, menuAction);
        this.publishMenuButton.addClass('content-publish-menu');
    }

    getPublishMenuButton(): MenuButton {
        return this.publishMenuButton;
    }
}