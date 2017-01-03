import "../../../api.ts";
import {UserItemsTreeGrid} from "../UserItemsTreeGrid";
import {UserTreeGridItem} from "../UserTreeGridItem";

import Action = api.ui.Action;

export class SyncPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super("Sync");
        this.setEnabled(false);
        this.onExecuted(() => {
            let principals: UserTreeGridItem[] = grid.getSelectedDataList();
            grid.getSelectedDataList().forEach((elem) => {
                this.sync(elem);
            });
        });
    }

    private sync(principal: UserTreeGridItem) {
        console.log('Sync principals action');
    }
}
