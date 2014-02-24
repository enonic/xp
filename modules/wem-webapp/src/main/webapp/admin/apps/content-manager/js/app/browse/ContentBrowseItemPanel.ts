module app.browse {

    export class ContentBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.content.ContentSummary> {

        private previewPanel;

        private previewMode: boolean;

        constructor() {
            super();

            this.previewPanel = new ContentItemPreviewPanel();
            this.addPanel(this.previewPanel);
        }

        createItemStatisticsPanel(): app.view.ContentItemStatisticsPanel {
            return new app.view.ContentItemStatisticsPanel();
        }

        setPreviewMode(enabled: boolean) {
            this.previewMode = enabled;
            this.updateDisplayedPanel();
        }

        updateDisplayedPanel() {
            var selectedItems = this.getItems();
            if (this.previewMode && selectedItems.length == 1) {
                this.previewPanel.setItem(selectedItems[0]);
                this.showPanel(this.getSize() - 1);
            } else {
                super.updateDisplayedPanel();
            }
        }

        public isPreviewMode(): boolean {
            return this.previewMode;
        }
    }

    export class ContentItemPreviewPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        constructor() {
            super("item-preview-panel");
            this.frame = new api.dom.IFrameEl();
            this.appendChild(this.frame);
        }

        public setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            var escapedPath = item.getPath();
            if (escapedPath.charAt(0) == '/') {
                escapedPath = escapedPath.substring(1);
            }
            this.frame.setSrc(api.util.getUri("portal/live/" + escapedPath));
        }

    }
}
