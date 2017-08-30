import '../../../../../api.ts';
import {WidgetItemView} from '../../WidgetItemView';

import CompareStatus = api.content.CompareStatus;
import PublishStatus = api.content.PublishStatus;
import CompareStatusFormatter = api.content.CompareStatusFormatter;
import PublishStatusFormatter = api.content.PublishStatusFormatter;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class StatusWidgetItemView extends WidgetItemView {

    private content: ContentSummaryAndCompareStatus;

    public static debug: boolean = false;

    constructor() {
        super('status-widget-item-view');
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        let compareStatus = item.getCompareStatus();
        let publishStatus = item.getPublishStatus();
        if (StatusWidgetItemView.debug) {
            console.debug('StatusWidgetItemView.setCompareStatus: ', compareStatus);
            console.debug('StatusWidgetItemView.setPublishStatus: ', publishStatus);
        }
        if (compareStatus !== this.getCompareStatus() || publishStatus !== this.getPublishStatus()) {
            this.content = item;
            return this.layout();
        }
        return wemQ<any>(null);
    }

    private getCompareStatus() : CompareStatus {
        return this.content ? this.content.getCompareStatus() : null;
    }

    private getPublishStatus() : PublishStatus {
        return this.content ? this.content.getPublishStatus() : null;
    }

    public layout(): wemQ.Promise<any> {
        if (StatusWidgetItemView.debug) {
            console.debug('StatusWidgetItemView.layout');
        }

        return super.layout().then(() => {
            if (this.getCompareStatus() != null) {
                let statusEl = new api.dom.SpanEl();

                statusEl.addClass(CompareStatus[this.getCompareStatus()].toLowerCase().replace('_', '-') || 'unknown');
                let statusElHtml = CompareStatusFormatter.formatStatusFromContent(this.content).toLocaleUpperCase();

                if (PublishStatus.EXPIRED === this.getPublishStatus() || PublishStatus.PENDING === this.getPublishStatus()) {
                    statusEl.addClass(PublishStatus[this.getPublishStatus()].toLowerCase().replace('_', '-') || 'unknown');
                    statusElHtml += ' (' + PublishStatusFormatter.formatStatus(this.getPublishStatus()).toLocaleUpperCase() + ')';
                }

                statusEl.setHtml(statusElHtml);
                this.removeChildren();
                this.appendChild(statusEl);
            } else {
                this.removeChildren();
            }
        });
    }
}
