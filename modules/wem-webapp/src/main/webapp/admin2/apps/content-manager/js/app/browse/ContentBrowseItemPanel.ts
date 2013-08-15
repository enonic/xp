module app_browse {

    export class ContentBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        private actionMenu:ContentActionMenu;
        private previewPanel;
        private previewMode:bool;
        private items:api_app_browse.BrowseItem[];

        constructor() {
            this.actionMenu = new ContentActionMenu();
            super({
                actionMenu: this.actionMenu,
                fireGridDeselectEvent: this.fireGridDeselectEvent
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

        public setPreviewMode(enabled:bool) {
            this.previewMode = enabled;
            // refresh the view
            this.setItems(this.items);
        }

        public isPreviewMode():bool {
            return this.previewMode;
        }

        private fireGridDeselectEvent(model:api_model.ContentExtModel) {
            var models:api_model.ContentExtModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
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
