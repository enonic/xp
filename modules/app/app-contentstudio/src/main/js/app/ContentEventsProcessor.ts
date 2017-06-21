import '../api.ts';
import {ContentWizardPanelParams} from './wizard/ContentWizardPanelParams';
import {NewContentEvent} from './create/NewContentEvent';
import {SortContentEvent} from './browse/SortContentEvent';
import {OpenSortDialogEvent} from './browse/OpenSortDialogEvent';
import {MoveContentEvent} from './browse/MoveContentEvent';
import {OpenMoveDialogEvent} from './browse/OpenMoveDialogEvent';
import AppBarTabId = api.app.bar.AppBarTabId;
import Content = api.content.Content;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import ShowBrowsePanelEvent = api.app.ShowBrowsePanelEvent;
import i18n = api.util.i18n;

export class ContentEventsProcessor {

    static openWizardTab(params: ContentWizardPanelParams, tabId: AppBarTabId): Window {
        let wizardUrl = 'main#/' + params.toString();
        let isNew = !params.contentId;
        let wizardId;
        if (!isNew && navigator.userAgent.search('Chrome') > -1) {
            // add tab id for browsers that can focus tabs by id
            // don't do it for new to be able to create multiple
            // contents of the same type simultaneously
            wizardId = tabId.toString();
        }
        return window.open(wizardUrl, wizardId);
    }

    static popupBlocked(win: Window) {
        return !win || win.closed || typeof win.closed === 'undefined';
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

        event.getModels().every((content: ContentSummaryAndCompareStatus) => {

            if (!content || !content.getContentSummary()) {
                return true;
            }

            let contentSummary = content.getContentSummary();
            let contentTypeName = contentSummary.getType();

            let tabId = AppBarTabId.forEdit(contentSummary.getId());

            let wizardParams = new ContentWizardPanelParams()
                .setTabId(tabId)
                .setContentTypeName(contentTypeName)
                .setContentId(contentSummary.getContentId());

            let win = ContentEventsProcessor.openWizardTab(wizardParams, tabId);

            if (ContentEventsProcessor.popupBlocked(win)) {
                api.notify.showWarning(i18n('notify.popupBlocker.admin'), false);

                return false;
            }

            return true;
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
        new OpenMoveDialogEvent(contents.map(content => content.getContentSummary()), event.getRootNode()).fire();
    }
}
