import "../../../api.ts";

import Action = api.ui.Action;
import {ContentTreeGrid} from "../ContentTreeGrid";

export class EditContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Edit", "mod+e");
        this.setEnabled(false);
        this.onExecuted(() => {
            var contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new api.content.event.EditContentEvent(contents).fire();
        });
    }
}
