module app.view.detail {

    import CompareStatus = api.content.CompareStatus;
    import CompareStatusFormatter = api.content.CompareStatusFormatter;

    export class StatusWidgetItemView extends WidgetItemView {

        private status: CompareStatus;

        constructor() {
            super("status-widget-item-view");
        }

        public setStatus(status: CompareStatus) {
            this.status = status;
        }

        public layout(): wemQ.Promise<any> {
            this.removeChildren();
            if (this.status != undefined) {
                var statusEl = new api.dom.SpanEl().setHtml(CompareStatusFormatter.formatStatus(this.status).toLocaleUpperCase());
                statusEl.addClass(CompareStatus[this.status].toLowerCase().replace("_", "-") || "unknown");
                this.appendChild(statusEl);
            }
            return super.layout();
        }
    }
}