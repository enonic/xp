import "../../../api.ts";
import {ContentTreeGrid} from "../ContentTreeGrid";

import Action = api.ui.Action;

export class EditContentAction extends Action {

    private static MAX_ITEMS_TO_EDIT: number = 50;

    constructor(grid: ContentTreeGrid) {
        super('Edit', 'mod+e');
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();

            if (contents.length > EditContentAction.MAX_ITEMS_TO_EDIT) {
                api.notify.showWarning('Editing too much content may affect the performance. Please deselect some of the items.');
            } else {
                new api.content.event.EditContentEvent(contents).fire();
            }

        });
    }
}
