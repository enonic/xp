module api.app.browse {

    export class BrowseItemPanel<M> extends api.ui.DeckPanel {

        private itemStatisticsPanel:api.app.view.ItemStatisticsPanel<M>;

        private itemsSelectionPanel:ItemsSelectionPanel<M>;

        constructor() {
            super("browse-item-panel");

            this.itemsSelectionPanel = new ItemsSelectionPanel<M>();
            this.itemStatisticsPanel = this.createItemStatisticsPanel();

            this.addPanel(this.itemsSelectionPanel);
            this.addPanel(this.itemStatisticsPanel);
            this.showPanel(0);
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<M> {
            return new api.app.view.ItemStatisticsPanel<M>();
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
