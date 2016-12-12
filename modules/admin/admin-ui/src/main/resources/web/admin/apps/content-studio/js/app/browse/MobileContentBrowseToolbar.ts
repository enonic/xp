import "../../api.ts";

import FoldButton = api.ui.toolbar.FoldButton;
import ActionButton = api.ui.button.ActionButton;

export interface MobileContentBrowseToolbarActions {
    newContentAction: api.ui.Action;
    editContentAction: api.ui.Action;
    publishAction: api.ui.Action;
    unpublishAction: api.ui.Action;
    duplicateAction: api.ui.Action;
    deleteAction: api.ui.Action;
    sortAction: api.ui.Action;
    moveAction: api.ui.Action;
}

export class MobileContentBrowseToolbar extends api.ui.toolbar.Toolbar {

    constructor(actions: MobileContentBrowseToolbarActions) {
        super("toolbar");

        super.addActions([actions.newContentAction, actions.publishAction, actions.unpublishAction, actions.duplicateAction,
            actions.deleteAction, actions.sortAction, actions.moveAction]);

        this.initEditButton(actions.editContentAction);
    }

    private initEditButton(editAction: api.ui.Action) {
        var editButton = new api.ui.button.ActionButton(editAction);
        editButton.addClass("mobile-edit-action");
        this.appendChild(editButton);
    }

    addElement(button: ActionButton): ActionButton {
        var buttonWidth = button.getEl().getWidthWithBorder();
        this.fold.push(button, buttonWidth);
        return button;
    }

    protected foldOrExpand() {
        if (!this.fold.isEmpty()) {
            this.fold.show();
        }
    }
}
