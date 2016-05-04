import "../../../api.ts";

import ContentId = api.content.ContentId;
import ContentPath = api.content.ContentPath;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import {ContentWizardPanel} from "../ContentWizardPanel";
import {ContentDeleteDialog} from "../../remove/ContentDeleteDialog";

export class DeleteContentAction extends api.ui.Action {

    constructor(wizardPanel: ContentWizardPanel) {
        super("Delete", "mod+del", true);
        this.onExecuted(() => {
            new ContentDeleteDialog()
                .setContentToDelete(
                    [new ContentSummaryAndCompareStatus().setContentSummary(wizardPanel.getPersistedItem()).setCompareStatus(
                        wizardPanel.getContentCompareStatus())
                    ])
                .open();
        });
    }
}
