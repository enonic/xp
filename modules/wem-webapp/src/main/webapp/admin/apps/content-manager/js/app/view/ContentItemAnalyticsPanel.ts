module app.view {

    export class ContentItemAnalyticsPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        constructor() {
            super("item-analytics-panel");
            this.frame = new api.dom.IFrameEl();
            this.frame.setSrc(api.util.getUri("dev/detailpanel/analytics.html"));
            this.appendChild(this.frame);
        }
    }
}
