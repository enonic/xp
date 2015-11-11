module app.view.detail {

    import CompareStatus = api.content.CompareStatus;
    import CompareStatusFormatter = api.content.CompareStatusFormatter;

    export class StatusWidgetItemView extends WidgetItemView {

        private status: CompareStatus;

        public static debug = false;

        constructor() {
            super("status-widget-item-view");
        }

        public setStatus(status: CompareStatus) {
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
            this.removeChildren();

            return super.layout().then(() => {
                if (this.status != undefined) {
                    var statusEl = new api.dom.SpanEl().setHtml(CompareStatusFormatter.formatStatus(this.status).toLocaleUpperCase());
                    statusEl.addClass(CompareStatus[this.status].toLowerCase().replace("_", "-") || "unknown");
                    this.appendChild(statusEl);
                }
            });
        }
    }
}