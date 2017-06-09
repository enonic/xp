import '../../../api.ts';
import {ContentPublishPromptEvent} from '../ContentPublishPromptEvent';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;
import ContentSummary = api.content.ContentSummary;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import i18n = api.util.i18n;

export class PublishContentAction extends Action {

    constructor(grid: ContentTreeGrid, includeChildItems: boolean = false) {
        super(i18n('action.publishMore'));
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ContentPublishPromptEvent(contents, includeChildItems).fire();
        });
    }
}
