import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';
import {CreateIssuePromptEvent} from '../CreateIssuePromptEvent';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class CreateIssueAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super(i18n('action.createIssueMore'));

        this.setEnabled(false);

        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new CreateIssuePromptEvent(contents).fire();
        });
    }
}
