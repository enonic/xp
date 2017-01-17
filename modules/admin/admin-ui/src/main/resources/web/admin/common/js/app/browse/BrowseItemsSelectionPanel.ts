module api.app.browse {

    export class BrowseItemsSelectionPanel<M extends api.Equitable> extends api.ui.panel.Panel {

        private deselectedListeners: {(event: ItemDeselectedEvent<M>): void}[] = [];
        private items: BrowseItem<M>[] = [];
        private selectionItems: SelectionItem<M>[] = [];
        private messageForNoSelection: string = 'You are wasting this space - select something!';
        private mobileView: boolean = false;
        private itemsContainer: api.dom.DivEl;
        private itemsLimit: number;

        constructor(itemsLimit?: number) {
            super('items-selection-panel');
            this.getEl().addClass('no-selection');

            this.itemsContainer = new api.dom.DivEl('items-container');
            this.appendChild(this.itemsContainer);

            this.itemsContainer.setHtml(this.messageForNoSelection);

            this.resetLimit();
        }

        getItemsLimit(): number {
            return this.itemsLimit;
        }

        setItemsLimit(limit: number) {
            this.itemsLimit = limit;
        }

        getDefaultLimit(): number {
            return Number.MAX_VALUE;
        }

        resetLimit() {
            this.setItemsLimit(this.getDefaultLimit());
        }

        isLimitReached(): boolean {
            return this.itemsLimit <= this.selectionItems.length;
        }

        updateDisplayedSelection() {
            this.itemsContainer.appendChildren(...this.selectionItems.slice(0, this.itemsLimit));
        }

        setMobileView(mobileView: boolean) {
            this.mobileView = mobileView;
            if (mobileView) {
                this.itemsContainer.removeChildren();
            } else {
                this.updateDisplayedSelection();
            }
        }

        private addItem(item: BrowseItem<M>) {
            const index = this.indexOf(item);
            if (index >= 0) {
                // item already exist
                const currentItem = this.items[index];
                if (!this.compareItems(currentItem, item)) {
                    // update current item
                    this.items[index] = item;
                    this.selectionItems[index].setBrowseItem(item);
                }
                return;
            }

            if (this.items.length === 0) {
                this.removeClass('no-selection');
                this.itemsContainer.removeChildren();
            }

            const selectionItem = new SelectionItem(this.createItemViewer(item), item);
            selectionItem.onRemoveClicked((e: MouseEvent) => {
                this.removeItem(item);
                this.notifyDeselected(item);
            });

            if (!this.mobileView && !this.isLimitReached()) {
                this.itemsContainer.appendChild(selectionItem);
            }
            this.selectionItems.push(selectionItem);
            this.items.push(item);
        }

        private removeItem(item: BrowseItem<M>) {
            const index = this.indexOf(item);
            if (index < 0) {
                return;
            }

            this.selectionItems[index].remove();
            this.selectionItems.splice(index, 1);
            this.items.splice(index, 1);

            const limitReachedChanged = this.items.length === this.getDefaultLimit();
            if (limitReachedChanged) {
                this.resetLimit();
            }

            if (this.items.length === 0) {
                this.addClass('no-selection');
                this.itemsContainer.setHtml(this.messageForNoSelection);
            }
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

        createItemViewer(item: BrowseItem<M>): api.ui.Viewer<M> {
            let viewer = new api.ui.Viewer<M>();
            viewer.setObject(item.getModel());
            return viewer;
        }

        updateItemViewers(items: BrowseItem<M>[]) {
            items.forEach((item) => {
                let index = this.indexOf(item);
                if (index >= 0) {
                    this.items[index] = item;
                    this.selectionItems[index].setBrowseItem(item);
                }
            });
        }

        private indexOf(item: BrowseItem<M>): number {
            for (let i = 0; i < this.items.length; i++) {
                if (item.getPath() && item.getPath() == this.items[i].getPath() ||
                    item.getId() == this.items[i].getId()) {
                    return i;
                }
            }
            return -1;
        }

        onDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.deselectedListeners.push(listener);
        }

        protected compareItems(currentItem: BrowseItem<M>, updatedItem: BrowseItem<M>): boolean {
            return updatedItem.equals(currentItem);
        }

        private notifyDeselected(item: BrowseItem<M>) {
            this.deselectedListeners.forEach((listener: (event: ItemDeselectedEvent<M>)=>void) => {
                listener.call(this, new ItemDeselectedEvent(item));
            });
        }
    }
}
