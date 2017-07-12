module api.app.browse {
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import i18n = api.util.i18n;

    export class BrowseItemPanel<M extends api.Equitable> extends api.ui.panel.DeckPanel {

        protected itemStatisticsPanel: api.app.view.ItemStatisticsPanel<M>;

        private items: BrowseItem<M>[] = [];

        private noSelectionContainer: api.dom.DivEl;

        constructor() {
            super('browse-item-panel no-selection');

            this.itemStatisticsPanel = this.createItemStatisticsPanel();

            this.noSelectionContainer = new api.dom.DivEl('no-selection-container');
            this.noSelectionContainer.setHtml(i18n('panel.noselection'));

            this.addPanel(this.itemStatisticsPanel);
            this.appendChild(this.noSelectionContainer);

            this.showPanelByIndex(0);
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<M> {
            return new api.app.view.ItemStatisticsPanel<M>();
        }

        togglePreviewForItem(item?: BrowseItem<M>) {
            if (item) {
                this.removeClass('no-selection');
                this.setStatisticsItem(item);
            } else {
                this.showNoSelectionMessage();
            }
        }

        updatePreviewPanel() {
            this.togglePreviewForItem(this.items.length > 0 ? this.items[this.items.length - 1] : null);
        }

        private showNoSelectionMessage() {
            this.addClass('no-selection');
        }

        setStatisticsItem(item: BrowseItem<M>) {
            this.itemStatisticsPanel.setItem(item.toViewItem());
        }

        getStatisticsItem(): api.app.view.ViewItem<M> {
            return this.itemStatisticsPanel.getItem();
        }

        private addItem(item: BrowseItem<M>) {
            const index = this.indexOf(item);
            if (index >= 0) {
                // item already exist
                const currentItem = this.items[index];
                if (!this.compareItems(currentItem, item)) {
                    // update current item
                    this.items[index] = item;
                }
                return;
            }

            this.items.push(item);
        }

        private removeItem(item: BrowseItem<M>) {
            const index = this.indexOf(item);
            if (index < 0) {
                return;
            }

            this.items.splice(index, 1);
        }

        updateItems(items: BrowseItem<M>[]) {
            items.forEach((item) => {
                let index = this.indexOf(item);
                if (index >= 0) {
                    this.items[index] = item;
                }
            });
        }

        getItems(): BrowseItem<M>[] {
            return this.items;
        }

        setItems(items: BrowseItem<M>[]): BrowseItemsChanges<M> {
            let changes = new BrowseItemsChanges<M>();

            let doFilter = (valueLeft: BrowseItem<M>, valueRight: BrowseItem<M>) => {
                if (valueLeft.getPath() && valueLeft.getPath() === valueRight.getPath()) {
                    return true;
                } else if (valueLeft.getId() === valueRight.getId()) {
                    return true;
                }

                return false;
            };

            let itemsToRemove = api.util.ArrayHelper.difference(this.items, items, doFilter);

            let itemsToAdd = api.util.ArrayHelper.difference(items, this.items, doFilter);

            let itemsUpdated = api.util.ArrayHelper.intersection(items, this.items, doFilter);

            itemsToRemove.forEach((item: BrowseItem<M>) => {
                this.removeItem(item);
            });

            itemsToAdd.forEach((item: BrowseItem<M>) => {
                this.addItem(item);
            });

            itemsUpdated.forEach((item: BrowseItem<M>) => {
                // addItem() will update the item, if there is a difference between them
                this.addItem(item);
            });

            changes.setAdded(itemsToAdd);
            changes.setRemoved(itemsToRemove);

            return changes;
        }

        private indexOf(item: BrowseItem<M>): number {
            for (let i = 0; i < this.items.length; i++) {
                if (item.getPath() && item.getPath() === this.items[i].getPath() ||
                    item.getId() === this.items[i].getId()) {
                    return i;
                }
            }
            return -1;
        }

        protected compareItems(currentItem: BrowseItem<M>, updatedItem: BrowseItem<M>): boolean {
            return updatedItem.equals(currentItem);
        }
    }
}
