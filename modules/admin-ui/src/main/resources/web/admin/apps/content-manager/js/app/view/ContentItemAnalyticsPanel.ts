module app.view {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;

    export class ContentItemAnalyticsPanel extends api.ui.panel.Panel {

        private item: ViewItem<ContentSummary>;

        private frame: api.dom.IFrameEl;

        constructor() {
            super("content-item-analytics-panel");
            this.frame = new api.dom.IFrameEl();
            this.frame.setSrc(api.util.UriHelper.getUri("dev/detailpanel/analytics.html"));
            this.appendChild(this.frame);
        }

        setItem(item: ViewItem<ContentSummary>) {
            this.item = item;
        }

        public getItem(): ViewItem<ContentSummary> {
            return this.item;
        }
    }
}
