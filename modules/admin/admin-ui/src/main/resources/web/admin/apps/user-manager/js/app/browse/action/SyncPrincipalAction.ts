import "../../../api.ts";

import Action = api.ui.Action;
import {UserItemsTreeGrid} from "../UserItemsTreeGrid";
import {UserTreeGridItem} from "../UserTreeGridItem";
import {UserTreeGridItemType} from "../UserTreeGridItem";

export class SyncPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super("Sync");
        this.setEnabled(false);
        this.onExecuted(() => {
            var principals: UserTreeGridItem[] = grid.getSelectedDataList();
            grid.getSelectedDataList().forEach((elem) => {
                this.sync(elem);
            });
        });
    }

    private sync(principal: UserTreeGridItem) {
        console.log('Sync principals action');
    }
}
