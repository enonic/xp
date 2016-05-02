import "../../../api.ts";

import Action = api.ui.Action;
import {MoveContentEvent} from "../MoveContentEvent";
import {ContentTreeGrid} from "../ContentTreeGrid";

export class MoveContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Move");
        this.setEnabled(false);
        this.onExecuted(() => {
            var contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new MoveContentEvent(contents).fire();
        });
    }
}
