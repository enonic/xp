module api_app_browse {

    export interface BrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class BrowseItemPanel extends api_ui.DeckPanel {

        private actionMenu:api_ui_menu.ActionMenu;

        private itemStatisticsPanel:api_app_view.ItemStatisticsPanel;

        private itemsSelectionPanel:ItemsSelectionPanel;

        constructor(params:BrowseItemPanelParams) {
            super("BrowseItemPanel");
            this.getEl().addClass("browse-item-panel");

            this.actionMenu = new api_ui_menu.ActionMenu(params.actionMenuActions);
            this.itemsSelectionPanel = new ItemsSelectionPanel();

            this.itemStatisticsPanel = new api_app_view.ItemStatisticsPanel({actionMenu: this.actionMenu});

            this.addPanel(this.itemsSelectionPanel);
            this.addPanel(this.itemStatisticsPanel);
            this.showPanel(0);

        }

        addItem(item:BrowseItem) {
            this.itemsSelectionPanel.addItem(item);
            this.updateDisplayedPanel();
        }

        removeItem(item:BrowseItem) {
            this.itemsSelectionPanel.removeItem(item);
            this.updateDisplayedPanel();
        }

        getItems():BrowseItem[] {
            return this.itemsSelectionPanel.getItems();
        }

        updateDisplayedPanel() {
            var selectedItems = this.getItems();
            if (selectedItems.length == 1) {
                this.itemStatisticsPanel.setItem(selectedItems[0].toViewItem());
                this.showPanel(1);
            } else {
                this.showPanel(0);
            }
        }

        addListener(listener:BrowseItemPanelListener) {
            super.addListener(listener);
            this.itemsSelectionPanel.addListener(listener);
        }

        removeListener(listener:BrowseItemPanelListener) {
            super.removeListener(listener);
            this.itemsSelectionPanel.removeListener(listener);
        }
    }
}
