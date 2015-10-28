module app.view {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class ContentItemVersionsPanel extends api.ui.panel.Panel {

        private item: ViewItem<ContentSummaryAndCompareStatus>;
        private allGrid: ContentVersionsTreeGrid;

        constructor() {
            super("content-item-versions-panel");

            this.allGrid = new AllContentVersionsTreeGrid();
            this.appendChild(this.allGrid);
        }

        public setItem(item: ViewItem<ContentSummaryAndCompareStatus>) {
            this.item = item;
            if (this.item) {
                if (this.allGrid.getContentId() != this.item.getModel().getContentId()) {
                    this.allGrid.setContentId(item.getModel().getContentId());
                }
            }
        }

        public setStatus(status: api.content.CompareStatus) {
            this.allGrid.setStatus(status);
        }

        public reRenderActivePanel() {
            if (this.item) {
                this.allGrid.getGrid().invalidate();
                this.allGrid.render();
            }
        }

        public reloadActivePanel() {
            this.allGrid.reload();
        }

        public getItem(): ViewItem<ContentSummaryAndCompareStatus> {
            return this.item;
        }
    }

}