import '../../api.ts';
import {ContentTreeGridActions} from './action/ContentTreeGridActions';
import {MenuButtonProgressBarManager} from './MenuButtonProgressBarManager';

import MenuButton = api.ui.button.MenuButton;
import ProgressBar = api.ui.ProgressBar;

export class ContentPublishMenuButton extends MenuButton {

    constructor(actions: ContentTreeGridActions) {
        super(actions.PUBLISH_CONTENT, [actions.PUBLISH_TREE_CONTENT,  actions.CREATE_ISSUE, actions.UNPUBLISH_CONTENT]);
        this.addClass('content-publish-menu');
        this.appendChild(MenuButtonProgressBarManager.getProgressBar());
    }
}
