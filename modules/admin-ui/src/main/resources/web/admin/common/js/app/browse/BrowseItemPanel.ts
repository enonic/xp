module api.app.browse {

    export class BrowseItemPanel<M extends api.Equitable> extends api.ui.panel.DeckPanel {

        private itemStatisticsPanel: api.app.view.ItemStatisticsPanel<M>;

        private itemsSelectionPanel: BrowseItemsSelectionPanel<M>;

        constructor() {
            super("browse-item-panel");

            this.itemsSelectionPanel = this.createItemSelectionPanel();
            this.itemStatisticsPanel = this.createItemStatisticsPanel();

            this.addPanel(this.itemsSelectionPanel);
            this.addPanel(this.itemStatisticsPanel);
            this.showPanelByIndex(0);
        }

        createItemSelectionPanel(): BrowseItemsSelectionPanel<M> {
            return new BrowseItemsSelectionPanel<M>();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<M> {
            return new api.app.view.ItemStatisticsPanel<M>();
        }

        setItems(items: api.app.browse.BrowseItem<M>[]) {
            this.itemsSelectionPanel.setItems(items);
            this.updateDisplayedPanel();
        }

        getItems(): BrowseItem<M>[] {
            return this.itemsSelectionPanel.getItems();
        }

        updateItemViewers(items: BrowseItem<M>[]) {
            this.itemsSelectionPanel.updateItemViewers(items);
        }

        updateDisplayedPanel() {
            var selectedItems = this.getItems();
            if (selectedItems.length == 1) {
                this.showPanelByIndex(1);
                this.itemStatisticsPanel.setItem(selectedItems[0].toViewItem());
            } else {
                this.showPanelByIndex(0);
            }
        }

        setStatisticsItem(item: BrowseItem<M>) {
            this.itemStatisticsPanel.setItem(item.toViewItem());
        }

        getStatisticsItemPath(): string {
            return this.itemStatisticsPanel.getItem().getPath();
        }

        onDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.itemsSelectionPanel.onDeselected(listener);
        }

        unDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.itemsSelectionPanel.unDeselected(listener);
        }
    }
}
