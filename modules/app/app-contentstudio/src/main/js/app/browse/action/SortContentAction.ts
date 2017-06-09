import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';
import {SortContentEvent} from '../SortContentEvent';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class SortContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super(i18n('action.sortMore'));
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new SortContentEvent(contents).fire();
        });
    }
}
