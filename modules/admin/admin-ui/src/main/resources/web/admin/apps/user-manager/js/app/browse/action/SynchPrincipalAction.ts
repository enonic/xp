import "../../../api.ts";

import Action = api.ui.Action;
import {UserItemsTreeGrid} from "../UserItemsTreeGrid";
import {UserTreeGridItem} from "../UserTreeGridItem";

export class SynchPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super("Synch");
        this.setEnabled(false);
        this.onExecuted(() => {
            var principals: UserTreeGridItem[] = grid.getSelectedDataList();
            grid.getSelectedDataList().forEach((elem) => {
                this.synch(elem);
            });
        });
    }

    private synch(principal: UserTreeGridItem) {
        console.log('Synch principals action');
    }
}
