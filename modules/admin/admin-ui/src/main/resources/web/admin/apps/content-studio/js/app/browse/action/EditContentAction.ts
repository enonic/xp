import "../../../api.ts";
import {ContentTreeGrid} from "../ContentTreeGrid";

import Action = api.ui.Action;

export class EditContentAction extends Action {

    private static MAX_ITEMS_TO_EDIT: number = 5;

    constructor(grid: ContentTreeGrid) {
        super("Edit", "mod+e");
        this.setEnabled(false);
        this.onExecuted(() => {
            var contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();

            if (contents.length > EditContentAction.MAX_ITEMS_TO_EDIT) {
                api.notify.showWarning("Too many items selected for edit ("
                                       + EditContentAction.MAX_ITEMS_TO_EDIT +
                                       " allowed) - performance may degrade. Please deselect some of the items.");
            }
            else {
                new api.content.event.EditContentEvent(contents).fire();
            }

        });
    }
}
