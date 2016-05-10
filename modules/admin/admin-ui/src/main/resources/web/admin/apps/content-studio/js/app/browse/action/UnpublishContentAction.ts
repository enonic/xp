import "../../../api.ts";

import Action = api.ui.Action;
import {ContentUnpublishPromptEvent} from "../ContentUnpublishPromptEvent";
import {ContentTreeGrid} from "../ContentTreeGrid";

export class UnpublishContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Unpublish");

        this.setVisible(false);
        this.setEnabled(false);

        this.onExecuted(() => {
            var contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ContentUnpublishPromptEvent(contents).fire();
        });
    }
}
