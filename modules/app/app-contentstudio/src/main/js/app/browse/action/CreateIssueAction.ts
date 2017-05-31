import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';
import {CreateIssuePromptEvent} from '../CreateIssuePromptEvent';

import Action = api.ui.Action;

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
