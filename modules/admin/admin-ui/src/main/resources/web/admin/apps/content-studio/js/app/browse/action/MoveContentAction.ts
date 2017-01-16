import "../../../api.ts";
import {MoveContentEvent} from "../MoveContentEvent";
import {ContentTreeGrid} from "../ContentTreeGrid";

import Action = api.ui.Action;

export class MoveContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super('Move');
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new MoveContentEvent(contents).fire();
        });
    }
}
