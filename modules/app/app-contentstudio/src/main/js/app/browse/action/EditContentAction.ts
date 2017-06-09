import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class EditContentAction extends Action {

    private static MAX_ITEMS_TO_EDIT: number = 50;

    constructor(grid: ContentTreeGrid) {
        super(i18n('action.edit'), 'mod+e');
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();

            if (contents.length > EditContentAction.MAX_ITEMS_TO_EDIT) {
                api.notify.showWarning(i18n('notify.edit.tooMuch'));
            } else {
                new api.content.event.EditContentEvent(contents).fire();
            }

        });
    }
}
