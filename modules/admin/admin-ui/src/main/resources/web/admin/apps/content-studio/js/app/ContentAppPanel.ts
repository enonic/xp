import "../api.ts";
import {ViewContentEvent} from "./browse/ViewContentEvent";
import {ContentBrowsePanel} from "./browse/ContentBrowsePanel";
import {NewContentEvent} from "./create/NewContentEvent";

import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Content = api.content.Content;
import ContentId = api.content.ContentId;
import ContentNamedEvent = api.content.event.ContentNamedEvent;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import AppBarTabId = api.app.bar.AppBarTabId;
import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
import AppBarTabMenuItemBuilder = api.app.bar.AppBarTabMenuItemBuilder;
import ShowBrowsePanelEvent = api.app.ShowBrowsePanelEvent;
import UriHelper = api.util.UriHelper;
import AppPanel = api.app.AppPanel;

export class ContentAppPanel extends AppPanel<ContentSummaryAndCompareStatus> {

    private path: api.rest.Path;

    constructor(path?: api.rest.Path) {
        super();
        this.path = path;
    }


    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered) => {
            this.route(this.path);
            return rendered;
        });
    }

    private route(path?: api.rest.Path) {
        var action = path ? path.getElement(0) : undefined;

        switch (action) {
        case 'edit':
            var id = path.getElement(1);
            if (id) {
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetch(new ContentId(id)).done(
                    (content: ContentSummaryAndCompareStatus) => {
                        new api.content.event.EditContentEvent([content]).fire();
                    });
            }
            break;
        case 'view' :
            var id = path.getElement(1);
            if (id) {
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetch(new ContentId(id)).done(
                    (content: ContentSummaryAndCompareStatus) => {
                        new ViewContentEvent([content]).fire();
                    });
            }
            break;
        default:
            new ShowBrowsePanelEvent().fire();
            break;
        }
    }

    protected handleGlobalEvents() {
        super.handleGlobalEvents();

        NewContentEvent.on((newContentEvent) => {
            this.handleNew(newContentEvent);
        });
    }

    protected createBrowsePanel() {
        return new ContentBrowsePanel();
    }

    private handleNew(newContentEvent: NewContentEvent) {
        if (newContentEvent.getContentType().isSite() && this.browsePanel) {
            var content: Content = newContentEvent.getParentContent();
            if (!!content) { // refresh site's node
                this.browsePanel.getTreeGrid().refreshNodeById(content.getId());
            }
        }
    }

}
