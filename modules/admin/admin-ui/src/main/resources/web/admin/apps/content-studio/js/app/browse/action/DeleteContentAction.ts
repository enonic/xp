import "../../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import Action = api.ui.Action;
import {ContentTreeGrid} from "../ContentTreeGrid";
import {ContentDeletePromptEvent} from "../ContentDeletePromptEvent";

export class DeleteContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Delete", "mod+del");
        this.setEnabled(false);
        this.onExecuted(() => {
            var contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ContentDeletePromptEvent(contents)
                .setNoCallback(null)
                .setYesCallback((exclude?: api.content.CompareStatus[]) => {

                    var excludeStatuses = !!exclude ? exclude : [CompareStatus.EQUAL, CompareStatus.NEWER, CompareStatus.MOVED],
                    //except PENDING_DELETE because it gets deleted immediately via dialog
                    deselected = [];
                    grid.getSelectedDataList().forEach((content: ContentSummaryAndCompareStatus) => {
                        if (excludeStatuses.indexOf(content.getCompareStatus()) < 0) {
                            deselected.push(content.getId());
                        }
                    });
                grid.deselectNodes(deselected);
                }).fire();
        });
    }
}