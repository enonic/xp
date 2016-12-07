import "../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import PublishStatus = api.content.PublishStatus;
import BrowseItem = api.app.browse.BrowseItem;

export class StatusSelectionItem extends api.app.browse.SelectionItem<ContentSummaryAndCompareStatus> {

    constructor(viewer: api.ui.Viewer<ContentSummaryAndCompareStatus>, item: BrowseItem<ContentSummaryAndCompareStatus>) {
        super(viewer, item);
    }

    doRender(): wemQ.Promise<boolean> {
        return super.doRender().then((rendered) => {

            var statusDiv = this.initStatusDiv(this.item.getModel().getCompareStatus(), this.item.getModel().getPublishStatus());
            this.appendChild(statusDiv);

            return rendered;
        });
    }

    private initStatusDiv(compareStatus: CompareStatus, publishStatus: PublishStatus) {
        var statusDiv = new api.dom.DivEl("status");
        var compareStatusFormatted = api.content.CompareStatusFormatter.formatStatus(compareStatus);
        if (publishStatus && (publishStatus == PublishStatus.PENDING || publishStatus == PublishStatus.EXPIRED)) {
            var publishStatusFormatted = api.content.PublishStatusFormatter.formatStatus(publishStatus);
            var compareStatusDiv = new api.dom.DivEl();
            var publishStatusDiv = new api.dom.DivEl();
            compareStatusDiv.setHtml(compareStatusFormatted);
            publishStatusDiv.setHtml("(" + publishStatusFormatted + ")");
            statusDiv.appendChildren(compareStatusDiv, publishStatusDiv);
        } else {
            statusDiv.setHtml(compareStatusFormatted);
        }
        var statusClass = "" + CompareStatus[compareStatusFormatted];
        statusDiv.addClass(statusClass.toLowerCase());
        return statusDiv;
    }
}
