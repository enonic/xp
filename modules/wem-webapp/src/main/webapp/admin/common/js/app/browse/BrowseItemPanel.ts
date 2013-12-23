module api_app_browse {

    export interface BrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class BrowseItemPanel<M> extends api_ui.DeckPanel {

        private actionMenu:api_ui_menu.ActionMenu;

        private itemStatisticsPanel:api_app_view.ItemStatisticsPanel<M>;

        private itemsSelectionPanel:ItemsSelectionPanel<M>;

        constructor(params:BrowseItemPanelParams) {
            super("BrowseItemPanel");
            this.getEl().addClass("browse-item-panel");

            this.actionMenu = new api_ui_menu.ActionMenu(params.actionMenuActions);
            this.itemsSelectionPanel = new ItemsSelectionPanel<M>();

            this.itemStatisticsPanel = new api_app_view.ItemStatisticsPanel<M>({actionMenu: this.actionMenu});

            this.addPanel(this.itemsSelectionPanel);
            this.addPanel(this.itemStatisticsPanel);
            this.showPanel(0);

        }

        setItems(items:any) {
            this.itemsSelectionPanel.setItems(items);
            this.updateDisplayedPanel();
        }

        getItems():BrowseItem<M>[] {
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

        addListener(listener:BrowseItemPanelListener<M>) {
            super.addListener(listener);
            this.itemsSelectionPanel.addListener(listener);
        }

        removeListener(listener:BrowseItemPanelListener<M>) {
            super.removeListener(listener);
            this.itemsSelectionPanel.removeListener(listener);
        }
    }
}
