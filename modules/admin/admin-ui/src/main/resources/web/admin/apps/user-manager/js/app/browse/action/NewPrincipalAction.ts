import "../../../api.ts";
import {NewPrincipalEvent} from "../NewPrincipalEvent";
import {UserTreeGridItem} from "../UserTreeGridItem";
import {UserItemsTreeGrid} from "../UserItemsTreeGrid";

import Action = api.ui.Action;

export class NewPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super('New', 'mod+alt+n');
        this.setEnabled(false);
        this.onExecuted(() => {
            let principals: UserTreeGridItem[] = grid.getSelectedDataList();
            new NewPrincipalEvent(principals).fire();
        });
    }
}
