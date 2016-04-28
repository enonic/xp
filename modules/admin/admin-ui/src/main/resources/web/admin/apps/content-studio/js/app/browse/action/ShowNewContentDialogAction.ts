import "../../../api.ts";

import Action = api.ui.Action;
import {ShowNewContentDialogEvent} from "../ShowNewContentDialogEvent";
import {ContentTreeGrid} from "../ContentTreeGrid";

export class ShowNewContentDialogAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("New", "alt+n");
        this.setEnabled(true);
        this.onExecuted(() => {
            var contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ShowNewContentDialogEvent(contents.length > 0 ? contents[0] : null).fire();
        });
    }
}
