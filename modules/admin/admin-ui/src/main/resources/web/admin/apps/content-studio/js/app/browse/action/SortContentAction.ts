import "../../../api.ts";

import Action = api.ui.Action;
import {ContentTreeGrid} from "../ContentTreeGrid";
import {SortContentEvent} from "../SortContentEvent";

export class SortContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Sort");
        this.setEnabled(false);
        this.onExecuted(() => {
            var contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new SortContentEvent(contents).fire();
        });
    }
}
