import "../api.ts";
import {NewContentEvent} from "./create/NewContentEvent";
import {ViewContentEvent} from "./browse/ViewContentEvent";
import {SortContentEvent} from "./browse/SortContentEvent";
import {MoveContentEvent} from "./browse/MoveContentEvent";
import {ContentWizardPanelParams} from "./wizard/ContentWizardPanelParams";
import {OpenSortDialogEvent} from "./browse/OpenSortDialogEvent";
import {OpenMoveDialogEvent} from "./browse/OpenMoveDialogEvent";
import {ContentAppPanel} from "./ContentAppPanel";
import {ContentBrowsePanel} from "./browse/ContentBrowsePanel";
import EditContentEvent = api.content.event.EditContentEvent;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import ShowBrowsePanelEvent = api.app.ShowBrowsePanelEvent;
import AppBarTabId = api.app.bar.AppBarTabId;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Content = api.content.Content;

export class ContentEventsListener {

    private contentApp: ContentAppPanel;
    private applications: api.app.Application[];
    private started: boolean = false;

    constructor(applications: api.app.Application[]) {
        this.applications = applications;

        NewContentEvent.on((event) => {
            if (this.started) {
                this.handleNew(event);
            }
        });

        ViewContentEvent.on((event) => {
            if (this.started) {
                // Do we use this any more ?
            }
        });

        EditContentEvent.on((event) => {
            if (this.started) {
                this.handleEdit(event);
            }
        });

        ShowBrowsePanelEvent.on((event) => {
            if (this.started) {
                this.handleBrowse(event);
            }
        });

        ContentUpdatedEvent.on((event) => {
            if (this.started) {
                this.handleUpdated(event);
            }
        });

        SortContentEvent.on((event) => {
            if (this.started) {
                this.handleSort(event);
            }
        });

        MoveContentEvent.on((event) => {
            if (this.started) {
                this.handleMove(event);
            }
        });
    }

    start() {
        this.started = true;
    }

    stop() {
        this.started = false;
    }

    setContentApp(contentApp: ContentAppPanel) {
        this.contentApp = contentApp;
    }

    private handleBrowse(event: ShowBrowsePanelEvent) {
        if (this.contentApp) {
            var browsePanel: api.app.browse.BrowsePanel<ContentSummaryAndCompareStatus> = this.contentApp.getBrowsePanel();
            if (!browsePanel) {
                this.contentApp.addBrowsePanel(new ContentBrowsePanel());
            } else {
                this.contentApp.selectPanelByIndex(this.contentApp.getPanelIndex(browsePanel));
            }
        }
    }

    private openWizardTab(params: ContentWizardPanelParams, tabId: AppBarTabId): Window {
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

    private handleNew(newContentEvent: NewContentEvent) {

        var contentTypeSummary = newContentEvent.getContentType();
        var tabId = AppBarTabId.forNew(contentTypeSummary.getName());

        var wizardParams = new ContentWizardPanelParams()
            .setTabId(tabId)
            .setContentTypeName(contentTypeSummary.getContentTypeName())
            .setParentContentId(newContentEvent.getParentContent() ? newContentEvent.getParentContent().getContentId() : undefined)
            .setCreateSite(newContentEvent.getContentType().isSite());

        let tab: Window = this.openWizardTab(wizardParams, tabId);

        if (this.contentApp) {
            if (newContentEvent.getContentType().isSite() && this.contentApp.getBrowsePanel()) {
                var content: Content = newContentEvent.getParentContent();
                if (!!content) { // refresh site's node
                    this.contentApp.getBrowsePanel().getTreeGrid().refreshNodeById(content.getId());
                }
            }
        }
    }

    private handleEdit(event: api.content.event.EditContentEvent) {

        event.getModels().forEach((content: ContentSummaryAndCompareStatus) => {

            if (!content || !content.getContentSummary()) {
                return;
            }

            var contentSummary = content.getContentSummary(),
                contentTypeName = contentSummary.getType();

            var tabId = AppBarTabId.forEdit(contentSummary.getId());

            var wizardParams = new ContentWizardPanelParams()
                .setTabId(tabId)
                .setContentTypeName(contentTypeName)
                .setContentId(contentSummary.getContentId());

            let tab: Window = this.openWizardTab(wizardParams, tabId);
        });
    }

    private handleUpdated(event: ContentUpdatedEvent) {
        // do something when content is updated
    }

    private handleSort(event: SortContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenSortDialogEvent(contents[0]).fire();
    }

    private handleMove(event: MoveContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenMoveDialogEvent(contents.map(content => content.getContentSummary())).fire();
    }

}
