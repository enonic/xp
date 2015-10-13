module app.view {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;

    export class ContentItemVersionsPanel extends api.ui.panel.Panel {

        private item: ViewItem<ContentSummary>;
        private allGrid: ContentVersionsTreeGrid;

        constructor() {
            super("content-item-versions-panel");

            this.allGrid = new AllContentVersionsTreeGrid();
            this.appendChild(this.allGrid);
        }

        public setItem(item: ViewItem<ContentSummary>) {
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

        public getItem(): ViewItem<ContentSummary> {
            return this.item;
        }
    }

}