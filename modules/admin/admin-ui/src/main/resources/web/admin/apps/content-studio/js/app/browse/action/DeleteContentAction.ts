import "../../../api.ts";
import {ContentTreeGrid} from "../ContentTreeGrid";
import {ContentDeletePromptEvent} from "../ContentDeletePromptEvent";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import Action = api.ui.Action;

export class DeleteContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Delete", "mod+del");
        this.setEnabled(false);
        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new ContentDeletePromptEvent(contents)
                .setNoCallback(null)
                .setYesCallback((exclude?: api.content.CompareStatus[]) => {

                    let excludeStatuses = !!exclude ? exclude : [CompareStatus.EQUAL, CompareStatus.NEWER, CompareStatus.MOVED,
                            CompareStatus.PENDING_DELETE, CompareStatus.OLDER],
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
