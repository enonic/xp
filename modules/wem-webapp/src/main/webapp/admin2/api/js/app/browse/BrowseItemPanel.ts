module api_app_browse {

    export interface BrowseItemPanelParams {

        actionMenu:api_ui_menu.ActionMenu;

        fireGridDeselectEvent:Function;
    }

    export class BrowseItemPanel extends api_ui.DeckPanel {

        ext;

        private itemStatisticsPanel:api_app_view.ItemStatisticsPanel;

        private itemsSelectionPanel:ItemsSelectionPanel;

        constructor(browseItemPanelParams:BrowseItemPanelParams) {
            super("BrowseItemPanel");
            this.getEl().addClass("browse-item-panel");

            this.itemsSelectionPanel = new ItemsSelectionPanel(browseItemPanelParams.fireGridDeselectEvent);
            this.itemStatisticsPanel = new api_app_view.ItemStatisticsPanel({actionMenu: browseItemPanelParams.actionMenu});

            this.addPanel(this.itemsSelectionPanel);
            this.addPanel(this.itemStatisticsPanel);
            this.showPanel(0);

            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'south',
                split: true
            });
        }

        setItems(items:api_app_browse.BrowseItem[]) {

            if (items.length == 0) {

                this.itemsSelectionPanel.setItems(items);
                this.showPanel(0);
            }
            else if (items.length == 1) {

                this.itemStatisticsPanel.setItem(items[0].toViewItem());
                this.showPanel(1);

            } else if (items.length > 1) {

                this.itemsSelectionPanel.setItems(items);
                this.showPanel(0);
            }
        }
    }
}
