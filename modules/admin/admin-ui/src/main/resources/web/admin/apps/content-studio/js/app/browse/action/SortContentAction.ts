import "../../../api.ts";
import {ContentTreeGrid} from "../ContentTreeGrid";
import {SortContentEvent} from "../SortContentEvent";

import Action = api.ui.Action;

export class SortContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super('Sort');
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new SortContentEvent(contents).fire();
        });
    }
}
