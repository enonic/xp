module app_browse {

    export interface ContentBrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class ContentBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        private previewPanel;

        private previewMode:boolean;

        private items:api_app_browse.BrowseItem[];

        constructor(params:ContentBrowseItemPanelParams) {
            super(<api_app_browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });

            this.previewPanel = new ContentItemPreviewPanel();
            this.addPanel(this.previewPanel);
        }

        public setItems(items:api_app_browse.BrowseItem[]) {
            this.items = items;
            if (this.previewMode && items.length == 1) {
                this.previewPanel.setItem(items[0]);
                // preview panel is the last one
                this.showPanel(this.getSize() - 1);
            } else {
                super.setItems(items);
            }
        }

        public setPreviewMode(enabled:boolean) {
            this.previewMode = enabled;
            // refresh the view
            this.setItems(this.items);
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

        public setItem(item:api_app_browse.BrowseItem) {
            //TODO: use real item path here
            this.frame.setSrc("../../../dev/live-edit-page/bootstrap.jsp?edit=false");
        }

    }
}
