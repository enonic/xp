module app_browse {

    export interface ContentBrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class ContentBrowseItemPanel extends api_app_browse.BrowseItemPanel<api_content.ContentSummary> {

        private previewPanel;

        private previewMode:boolean;

        constructor(params:ContentBrowseItemPanelParams) {
            super(<api_app_browse.BrowseItemPanelParams>{
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

    export class ContentItemPreviewPanel extends api_ui.Panel {

        private frame:api_dom.IFrameEl;

        constructor() {
            super("ItemPreviewPanel");
            this.addClass("item-preview-panel");
            this.frame = new api_dom.IFrameEl();
            this.appendChild(this.frame);
        }

        public setItem(item:api_app_browse.BrowseItem<api_content.ContentSummary>) {
            //TODO: use real item path here
            this.frame.setSrc(api_util.getUri("dev/live-edit-page/bootstrap.jsp?edit=false"));
        }

    }
}
