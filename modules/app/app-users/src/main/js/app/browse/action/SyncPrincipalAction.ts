import '../../../api.ts';
import {UserItemsTreeGrid} from '../UserItemsTreeGrid';
import {UserTreeGridItem} from '../UserTreeGridItem';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class SyncPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super(i18n('action.sync'));
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
