import "../api.ts";
import {ContentWizardPanelParams} from "./wizard/ContentWizardPanelParams";
import {NewContentEvent} from "./create/NewContentEvent";
import {SortContentEvent} from "./browse/SortContentEvent";
import {OpenSortDialogEvent} from "./browse/OpenSortDialogEvent";
import {MoveContentEvent} from "./browse/MoveContentEvent";
import {OpenMoveDialogEvent} from "./browse/OpenMoveDialogEvent";
import AppBarTabId = api.app.bar.AppBarTabId;
import Content = api.content.Content;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import ShowBrowsePanelEvent = api.app.ShowBrowsePanelEvent;

export class ContentEventsProcessor {

    static openWizardTab(params: ContentWizardPanelParams, tabId: AppBarTabId): Window {
        let wizardUrl = 'content-studio#/' + params.toString(),
            isNew = !params.contentId,
            wizardId;
        if (!isNew && navigator.userAgent.search("Chrome") > -1) {
            // add tab id for browsers that can focus tabs by id
            // don't do it for new to be able to create multiple
            // contents of the same type simultaneously
            wizardId = tabId.toString();
        }
        return window.open(wizardUrl, wizardId);
    }

    static handleNew(newContentEvent: NewContentEvent) {

        let contentTypeSummary = newContentEvent.getContentType();
        let tabId = AppBarTabId.forNew(contentTypeSummary.getName());

        let wizardParams = new ContentWizardPanelParams()
            .setTabId(tabId)
            .setContentTypeName(contentTypeSummary.getContentTypeName())
            .setParentContentId(newContentEvent.getParentContent() ? newContentEvent.getParentContent().getContentId() : undefined)
            .setCreateSite(newContentEvent.getContentType().isSite());

        ContentEventsProcessor.openWizardTab(wizardParams, tabId);
    }

    static handleEdit(event: api.content.event.EditContentEvent) {

        event.getModels().forEach((content: ContentSummaryAndCompareStatus) => {

            if (!content || !content.getContentSummary()) {
                return;
            }

            let contentSummary = content.getContentSummary(),
                contentTypeName = contentSummary.getType();

            let tabId = AppBarTabId.forEdit(contentSummary.getId());

            let wizardParams = new ContentWizardPanelParams()
                .setTabId(tabId)
                .setContentTypeName(contentTypeName)
                .setContentId(contentSummary.getContentId());

            ContentEventsProcessor.openWizardTab(wizardParams, tabId);
        });
    }

    static handleUpdated(event: ContentUpdatedEvent) {
        // do something when content is updated
    }

    static handleSort(event: SortContentEvent) {

        let contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenSortDialogEvent(contents[0]).fire();
    }

    static handleMove(event: MoveContentEvent) {

        let contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenMoveDialogEvent(contents.map(content => content.getContentSummary())).fire();
    }
}