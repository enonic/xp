import "../../../../../api.ts";
import {WidgetItemView} from "../../WidgetItemView";

import CompareStatus = api.content.CompareStatus;
import CompareStatusFormatter = api.content.CompareStatusFormatter;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class StatusWidgetItemView extends WidgetItemView {

    private status: CompareStatus;

    public static debug: boolean = false;

    constructor() {
        super("status-widget-item-view");
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        var status = item.getCompareStatus();
        if (StatusWidgetItemView.debug) {
            console.debug('StatusWidgetItemView.setStatus: ', status);
        }
        if (status != this.status) {
            this.status = status;
            return this.layout();
        }
        return wemQ<any>(null);
    }

    public layout(): wemQ.Promise<any> {
        if (StatusWidgetItemView.debug) {
            console.debug('StatusWidgetItemView.layout');
        }

        return super.layout().then(() => {
            if (this.status != undefined) {
                var statusEl = new api.dom.SpanEl().setHtml(CompareStatusFormatter.formatStatus(this.status).toLocaleUpperCase());
                statusEl.addClass(CompareStatus[this.status].toLowerCase().replace("_", "-") || "unknown");

                this.removeChildren();
                this.appendChild(statusEl);
            } else {
                this.removeChildren();
            }
        });
    }
}
