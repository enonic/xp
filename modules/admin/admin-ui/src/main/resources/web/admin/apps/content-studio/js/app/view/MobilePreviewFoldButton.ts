import '../../api.ts';
import {PublishContentAction} from '../browse/action/PublishContentAction';

import Action = api.ui.Action;
import ActionButton = api.ui.button.ActionButton;

export class MobilePreviewFoldButton extends api.ui.toolbar.FoldButton {

    constructor(actions: Action[], hostElement: api.dom.Element) {
        super('', hostElement);

        this.addClass('mobile-preview-fold-button');
        this.addActions(actions);
    }

    private addElement(button: ActionButton) {
        let buttonWidth = button.getEl().getWidthWithBorder();
        this.push(button, buttonWidth);
    }

    private addAction(action: Action) {
        let button = new ActionButton(action);
        if (api.ObjectHelper.iFrameSafeInstanceOf(action, PublishContentAction)) {
            button.addClass('publish');
        }
        this.addElement(button);
    }

    private addActions(actions: Action[]) {
        actions.forEach((action) => {
            this.addAction(action);
        });
    }
}
