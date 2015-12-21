module api.app.browse {

    export class BrowseItemsSelectionPanel<M extends api.Equitable> extends api.ui.panel.Panel {

        private deselectedListeners: {(event: ItemDeselectedEvent<M>):void}[] = [];
        private items: BrowseItem<M>[] = [];
        private selectionItems: SelectionItem<M>[] = [];
        private messageForNoSelection = "You are wasting this space - select something!";

        constructor() {
            super("items-selection-panel");
            this.getEl().addClass('no-selection').setInnerHtml(this.messageForNoSelection);
        }

        private addItem(item: BrowseItem<M>) {
            var index = this.indexOf(item);
            if (index >= 0) {
                // item already exist
                var currentItem = this.items[index];
                if (!item.equals(currentItem)) {
                    // update current item
                    this.items[index] = item;
                }
                return;
            }

            if (this.items.length === 0) {
                this.removeClass('no-selection');
                this.removeChildren();
            }

            var removeCallback = () => {
                this.removeItem(item);
                this.notifyDeselected(item);
            };
            var selectionItem = new SelectionItem(this.createItemViewer(item), item, removeCallback);

            this.appendChild(selectionItem);
            this.selectionItems.push(selectionItem);
            this.items.push(item);
        }

        private removeItem(item: BrowseItem<M>) {
            var index = this.indexOf(item);
            if (index < 0) {
                return;
            }

            this.selectionItems[index].remove();
            this.selectionItems.splice(index, 1);
            this.items.splice(index, 1);

            if (this.items.length === 0) {
                this.getEl().addClass('no-selection').setInnerHtml(this.messageForNoSelection);
            }
        }

        getItems(): BrowseItem<M>[] {
            return this.items;
        }

        setItems(items: BrowseItem<M>[]): BrowseItemsChanges {
            let changes = new BrowseItemsChanges();

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
            var viewer = new api.ui.Viewer<M>();
            viewer.setObject(item.getModel());
            return viewer;
        }

        updateItemViewers(items: BrowseItem<M>[]) {
            items.forEach((item) => {
                for (var i = 0; i < this.selectionItems.length; i++) {
                    if (this.selectionItems[i].getBrowseItem().getPath() === item.getPath()) {
                        this.selectionItems[i].updateViewer(this.createItemViewer(item));
                        break;
                    }
                }
            });
        }

        private indexOf(item: BrowseItem<M>): number {
            for (var i = 0; i < this.items.length; i++) {
                if (item.getPath()) {
                    if (item.getPath() == this.items[i].getPath()) {
                        return i;
                    }
                } else {
                    if (item.getId() == this.items[i].getId()) {
                        return i;
                    }
                }
            }
            return -1;
        }

        onDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.deselectedListeners.push(listener);
        }

        private notifyDeselected(item: BrowseItem<M>) {
            this.deselectedListeners.forEach((listener: (event: ItemDeselectedEvent<M>)=>void) => {
                listener.call(this, new ItemDeselectedEvent(item));
            });
        }
    }
}
