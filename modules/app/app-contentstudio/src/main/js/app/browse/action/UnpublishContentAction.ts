import '../../../api.ts';
import {ContentUnpublishPromptEvent} from '../ContentUnpublishPromptEvent';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class UnpublishContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super(i18n('action.unpublishMore'));

        this.setEnabled(false);

        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ContentUnpublishPromptEvent(contents).fire();
        });
    }
}
