module api.app.browse {
    import TreeGrid = api.ui.treegrid.TreeGrid;

    export class BrowseItemPanel<M extends api.Equitable> extends api.ui.panel.DeckPanel {

        private itemStatisticsPanel: api.app.view.ItemStatisticsPanel<M>;

        private itemsSelectionPanel: BrowseItemsSelectionPanel<M>;

        constructor(grid?: TreeGrid<M>) {
            super("browse-item-panel");

            this.itemsSelectionPanel = this.createItemSelectionPanel(grid);
            this.itemStatisticsPanel = this.createItemStatisticsPanel();

            this.addPanel(this.itemsSelectionPanel);
            this.addPanel(this.itemStatisticsPanel);
            this.showPanelByIndex(0);
        }

        createItemSelectionPanel(grid?: TreeGrid<M>): BrowseItemsSelectionPanel<M> {
            return new BrowseItemsSelectionPanel<M>();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<M> {
            return new api.app.view.ItemStatisticsPanel<M>();
        }

        setMobileView(mobileView: boolean) {
            this.itemsSelectionPanel.setMobileView(mobileView);
        }

        setItems(items: api.app.browse.BrowseItem<M>[]): BrowseItemsChanges<M> {
            let changes = this.itemsSelectionPanel.setItems(items);
            this.updateDisplayedPanel();

            return changes;
        }

        getItems(): BrowseItem<M>[] {
            return this.itemsSelectionPanel.getItems();
        }

        updateItemViewers(items: BrowseItem<M>[]) {
            this.itemsSelectionPanel.updateItemViewers(items);
        }

        updateDisplayedPanel() {
            let selectedItems = this.getItems();
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

        getStatisticsItem(): api.app.view.ViewItem<M> {
            return this.itemStatisticsPanel.getItem();
        }

        onDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.itemsSelectionPanel.onDeselected(listener);
        }
    }
}
