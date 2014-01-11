module app.browse {

    export interface ContentBrowseItemPanelParams {

        actionMenuActions:api.ui.Action[];
    }

    export class ContentBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.content.ContentSummary> {

        private previewPanel;

        private previewMode:boolean;

        constructor(params:ContentBrowseItemPanelParams) {
            super(<api.app.browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });

            this.previewPanel = new ContentItemPreviewPanel();
            this.addPanel(this.previewPanel);
        }

        public setPreviewMode(enabled:boolean) {
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

        public isPreviewMode():boolean {
            return this.previewMode;
        }
    }

    export class ContentItemPreviewPanel extends api.ui.Panel {

        private frame:api.dom.IFrameEl;

        constructor() {
            super(true, "item-preview-panel");
            this.frame = new api.dom.IFrameEl();
            this.appendChild(this.frame);
        }

        public setItem(item:api.app.browse.BrowseItem<api.content.ContentSummary>) {
            //TODO: use real item path here
            this.frame.setSrc(api.util.getUri("portal/live/" + item.getPath()));
        }

    }
}
