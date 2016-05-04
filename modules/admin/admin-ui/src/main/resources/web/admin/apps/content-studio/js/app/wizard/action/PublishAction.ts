import "../../../api.ts";

import Content = api.content.Content;
import ContentId = api.content.ContentId;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import {ContentWizardPanel} from "../ContentWizardPanel";
import {ContentPublishPromptEvent} from "../../browse/ContentPublishPromptEvent";

export class PublishAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super("Publish");

        this.setEnabled(false);

        this.onExecuted(() => {

            if (wizard.checkContentCanBePublished(true)) {
                wizard.setRequireValid(true);

                if (wizard.hasUnsavedChanges()) {
                    this.setEnabled(false);
                    wizard.saveChanges().then((content) => {
                        if (content) {
                            new ContentPublishPromptEvent([ContentSummaryAndCompareStatus.fromContentSummary(content)]).fire();
                        }
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason)
                    }).finally(() => this.setEnabled(true)).done();
                } else {
                    new ContentPublishPromptEvent([ContentSummaryAndCompareStatus.fromContentSummary(
                        wizard.getPersistedItem())]).fire();
                }
            } else {
                api.notify.showWarning('The content cannot be published yet. One or more form values are not valid.');
            }
        });
    }
}
