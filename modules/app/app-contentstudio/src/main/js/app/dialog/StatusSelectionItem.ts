import '../../api.ts';
import {ContentRowFormatter} from '../browse/ContentRowFormatter';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import PublishStatus = api.content.PublishStatus;
import BrowseItem = api.app.browse.BrowseItem;
import Tooltip = api.ui.Tooltip;

export class StatusSelectionItem extends api.app.browse.SelectionItem<ContentSummaryAndCompareStatus> {

    private removeHandlerFn: () => void;
    private isRemovableFn: () => boolean;

    constructor(viewer: api.ui.Viewer<ContentSummaryAndCompareStatus>, item: BrowseItem<ContentSummaryAndCompareStatus>) {
        super(viewer, item);

        let onRemoveClicked = api.util.AppHelper.debounce(() => {
            Tooltip.hideOtherInstances();
            if (this.isRemovable()) {
                this.removeHandlerFn();
            } else {
                let tooltip = new Tooltip(this.getRemoveButton(), 'This item is required for publishing');
                tooltip.setTrigger(Tooltip.TRIGGER_NONE);
                tooltip.showFor(1500);
            }
        }, 1000, true);

        this.onRemoveClicked(onRemoveClicked);
    }

    private isRemovable(): boolean {
        if (!this.isRemovableFn || !this.removeHandlerFn) {
            return true;
        }

        return this.isRemovableFn();
    }

    setIsRemovableFn(fn: () => boolean) {
        this.isRemovableFn = fn;
    }

    setRemoveHandlerFn(fn: () => void) {
        this.removeHandlerFn = fn;
    }

    doRender(): wemQ.Promise<boolean> {
        return super.doRender().then((rendered) => {

            let statusDiv = this.initStatusDiv(this.item.getModel());
            this.appendChild(statusDiv);

            return rendered;
        });
    }

    private initStatusDiv(content:ContentSummaryAndCompareStatus) {

        const compareStatus = content.getCompareStatus();
        const publishStatus = content.getPublishStatus();

        let statusDiv = new api.dom.DivEl('status');
        let statusClass = '' + CompareStatus[compareStatus];

        let compareStatusFormatted = api.content.CompareStatusFormatter.formatStatusFromContent(content);

        if (publishStatus && (publishStatus === PublishStatus.PENDING || publishStatus === PublishStatus.EXPIRED)) {
            let publishStatusFormatted = api.content.PublishStatusFormatter.formatStatus(publishStatus);
            statusClass += ' ' + PublishStatus[publishStatus] + ' ' + ContentRowFormatter.makeClassName(compareStatusFormatted);
            compareStatusFormatted += ` (${publishStatusFormatted})`;
            statusClass += ' ' + PublishStatus[publishStatus];
        } else {
            statusClass += ' ' + ContentRowFormatter.makeClassName(compareStatusFormatted);
        }
        statusDiv.setHtml(compareStatusFormatted);
        statusDiv.addClass(statusClass.toLowerCase());
        return statusDiv;
    }
}
