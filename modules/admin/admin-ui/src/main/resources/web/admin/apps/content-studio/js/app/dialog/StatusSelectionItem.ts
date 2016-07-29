import "../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import BrowseItem = api.app.browse.BrowseItem;

export class StatusSelectionItem extends api.app.browse.SelectionItem<ContentSummaryAndCompareStatus> {

    constructor(viewer: api.ui.Viewer<ContentSummaryAndCompareStatus>, item: BrowseItem<ContentSummaryAndCompareStatus>) {
        super(viewer, item);
    }

    doRender(): wemQ.Promise<boolean> {
        return super.doRender().then((rendered) => {

            var statusDiv = this.initStatusDiv(this.item.getModel().getCompareStatus());
            this.appendChild(statusDiv);

            return rendered;
        });
    }

    private initStatusDiv(status: CompareStatus) {
        var statusDiv = new api.dom.DivEl("status");
        statusDiv.setHtml(api.content.CompareStatusFormatter.formatStatus(status));
        var statusClass = "" + CompareStatus[status];
        statusDiv.addClass(statusClass.toLowerCase());
        return statusDiv;
    }
}
