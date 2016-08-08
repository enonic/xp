import "../../../api.ts";
import {ContentPublishPromptEvent} from "../ContentPublishPromptEvent";
import {ContentTreeGrid} from "../ContentTreeGrid";

import Action = api.ui.Action;
import ContentSummary = api.content.ContentSummary;
import PublishContentRequest = api.content.resource.PublishContentRequest;

export class PublishTreeContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Publish Tree");
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ContentPublishPromptEvent(contents, true).fire();
        });
    }
}
