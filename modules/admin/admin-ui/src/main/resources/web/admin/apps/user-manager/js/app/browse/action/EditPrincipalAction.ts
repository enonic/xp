import "../../../api.ts";
import {EditPrincipalEvent} from "../EditPrincipalEvent";
import {UserTreeGridItem} from "../UserTreeGridItem";
import {UserItemsTreeGrid} from "../UserItemsTreeGrid";

import Action = api.ui.Action;

export class EditPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super('Edit', 'f4');
        this.setEnabled(false);
        this.onExecuted(() => {
            let principals: UserTreeGridItem[] = grid.getSelectedDataList();
            new EditPrincipalEvent(principals).fire();
        });
    }
}
