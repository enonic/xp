import '../../../api.ts';
import {NewPrincipalEvent} from '../NewPrincipalEvent';
import {UserTreeGridItem} from '../UserTreeGridItem';
import {UserItemsTreeGrid} from '../UserItemsTreeGrid';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class NewPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super(i18n('action.new'), 'mod+alt+n');
        this.setEnabled(false);
        this.onExecuted(() => {
            let principals: UserTreeGridItem[] = grid.getSelectedDataList();
            new NewPrincipalEvent(principals).fire();
        });
    }
}
