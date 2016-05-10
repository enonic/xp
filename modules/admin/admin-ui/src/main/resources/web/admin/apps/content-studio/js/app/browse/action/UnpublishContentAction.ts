import "../../../api.ts";

import Action = api.ui.Action;
import {ContentPublishPromptEvent} from "../ContentPublishPromptEvent";
import {ContentTreeGrid} from "../ContentTreeGrid";

export class UnpublishContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Unpublish");

        this.setVisible(false);
        this.setEnabled(false);

        this.onExecuted(() => {
            console.log('Unpublish');
        });
    }
}
