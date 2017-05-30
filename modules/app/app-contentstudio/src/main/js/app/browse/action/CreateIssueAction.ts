import '../../../api.ts';
import {ContentUnpublishPromptEvent} from '../ContentUnpublishPromptEvent';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;
import {CreateIssuePromptEvent} from '../CreateIssuePromptEvent';

export class CreateIssueAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super('Create Issue...');

        this.setEnabled(false);

        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new CreateIssuePromptEvent(contents).fire();
        });
    }
}
