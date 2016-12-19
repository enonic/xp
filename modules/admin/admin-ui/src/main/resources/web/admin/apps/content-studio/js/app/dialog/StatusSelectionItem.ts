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
        let statusDiv = new api.dom.DivEl("status");
        let statusClass = "" + CompareStatus[compareStatus];
        let compareStatusFormatted = api.content.CompareStatusFormatter.formatStatus(compareStatus);
        if (publishStatus && (publishStatus == PublishStatus.PENDING || publishStatus == PublishStatus.EXPIRED)) {
            let publishStatusFormatted = api.content.PublishStatusFormatter.formatStatus(publishStatus);
            compareStatusFormatted += ` (${publishStatusFormatted})`;
            statusClass += " " + PublishStatus[publishStatus];
        }
        statusDiv.setHtml(compareStatusFormatted);
        statusDiv.addClass(statusClass.toLowerCase());
        return statusDiv;
    }
}
