import "../api.ts";
import {ViewContentEvent} from "./browse/ViewContentEvent";

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

export class ContentAppPanel extends api.app.BrowseAndWizardBasedAppPanel<ContentSummaryAndCompareStatus> {

    private path: api.rest.Path;
    private mask: api.ui.mask.LoadMask;

    constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

        super({
            appBar: appBar
        });
        this.path = path;

        this.mask = new api.ui.mask.LoadMask(this);
    }


    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered) => {
            this.route(this.path);
            return rendered;
        });
    }

    private route(path?: api.rest.Path) {
        const action = path ? path.getElement(0) : null;
        const id = path ? path.getElement(1) : null;

        switch (action) {
        case 'edit':
            if (id) {
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetch(new ContentId(id)).done(
                    (content: ContentSummaryAndCompareStatus) => {
                        new api.content.event.EditContentEvent([content]).fire();
                    });
            }
            break;
        case 'view' :
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

}
