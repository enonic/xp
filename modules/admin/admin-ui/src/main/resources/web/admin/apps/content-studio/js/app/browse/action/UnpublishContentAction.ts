import '../../../api.ts';
import {ContentUnpublishPromptEvent} from '../ContentUnpublishPromptEvent';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;

export class UnpublishContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super('Unpublish');

        this.setEnabled(false);

        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ContentUnpublishPromptEvent(contents).fire();
        });
    }
}
