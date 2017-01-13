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

    static popupBlocked(win: Window) {
        return !win || win.closed || typeof win.closed == "undefined";
    }

    static handleNew(newContentEvent: NewContentEvent) {

        var contentTypeSummary = newContentEvent.getContentType();
        var tabId = AppBarTabId.forNew(contentTypeSummary.getName());

        var wizardParams = new ContentWizardPanelParams()
            .setTabId(tabId)
            .setContentTypeName(contentTypeSummary.getContentTypeName())
            .setParentContentId(newContentEvent.getParentContent() ? newContentEvent.getParentContent().getContentId() : undefined)
            .setCreateSite(newContentEvent.getContentType().isSite());

        ContentEventsProcessor.openWizardTab(wizardParams, tabId);
    }

    static handleEdit(event: api.content.event.EditContentEvent) {

        event.getModels().every((content: ContentSummaryAndCompareStatus) => {

            if (!content || !content.getContentSummary()) {
                return true;
            }

            var contentSummary = content.getContentSummary(),
                contentTypeName = contentSummary.getType();

            var tabId = AppBarTabId.forEdit(contentSummary.getId());

            var wizardParams = new ContentWizardPanelParams()
                .setTabId(tabId)
                .setContentTypeName(contentTypeName)
                .setContentId(contentSummary.getContentId());

            let win = ContentEventsProcessor.openWizardTab(wizardParams, tabId);

            if (ContentEventsProcessor.popupBlocked(win)) {
                const message = "Pop-up Blocker is enabled in browser settings! Please add the XP admin to the exception list.";
                api.notify.showWarning(message, false);

                return false;
            }

            return true;
        });
    }

    static handleUpdated(event: ContentUpdatedEvent) {
        // do something when content is updated
    }

    static handleSort(event: SortContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenSortDialogEvent(contents[0]).fire();
    }

    static handleMove(event: MoveContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenMoveDialogEvent(contents.map(content => content.getContentSummary())).fire();
    }
}