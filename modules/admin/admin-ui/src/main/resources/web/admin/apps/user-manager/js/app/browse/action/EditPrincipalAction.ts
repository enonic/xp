import "../../../api.ts";

import Action = api.ui.Action;
import {EditPrincipalEvent} from "../EditPrincipalEvent";
import {UserTreeGridItem} from "../UserTreeGridItem";
import {UserItemsTreeGrid} from "../UserItemsTreeGrid";

export class EditPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super("Edit", "f4");
        this.setEnabled(false);
        this.onExecuted(() => {
            var principals: UserTreeGridItem[] = grid.getSelectedDataList();
            new EditPrincipalEvent(principals).fire();
        });
    }
}
