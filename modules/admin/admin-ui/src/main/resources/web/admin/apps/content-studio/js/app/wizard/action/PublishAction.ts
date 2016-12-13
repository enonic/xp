import "../../../api.ts";
import {ContentWizardPanel} from "../ContentWizardPanel";
import {ContentPublishPromptEvent} from "../../browse/ContentPublishPromptEvent";

import Content = api.content.Content;
import ContentId = api.content.ContentId;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class PublishAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel, includeChildItems: boolean = false) {
        super("Publish...");

        this.setEnabled(false);

        this.onExecuted(() => {

            if (wizard.checkContentCanBePublished()) {
                wizard.setRequireValid(true);

                if (wizard.hasUnsavedChanges()) {
                    this.setEnabled(false);
                    wizard.saveChanges().then((content) => {
                        if (content) {
                            let contentSummary = ContentSummaryAndCompareStatus.fromContentSummary(content);
                            contentSummary.setCompareStatus(wizard.getContentCompareStatus());
                            new ContentPublishPromptEvent([contentSummary], includeChildItems).fire();
                        }
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason)
                    }).finally(() => this.setEnabled(true)).done();
                } else {
                    let contentSummary = ContentSummaryAndCompareStatus.fromContentSummary(wizard.getPersistedItem());
                    contentSummary.setCompareStatus(wizard.getContentCompareStatus());
                    new ContentPublishPromptEvent([contentSummary], includeChildItems).fire();
                }
            } else {
                api.notify.showWarning('The content cannot be published yet. One or more form values are not valid.');
            }
        });
    }
}
