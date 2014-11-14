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
                if (!currentItem.equals(item)) {
                    // update current item
                    this.items[index] = item;
                }
                return;
            }

            if (this.items.length == 0) {
                this.removeClass('no-selection');
                this.removeChildren();
            }

            var removeCallback = () => {
                this.removeItem(item);
            };
            var selectionItem = new SelectionItem(this.createItemViewer(item), removeCallback);

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

            if (this.items.length == 0) {
                this.getEl().addClass('no-selection').setInnerHtml(this.messageForNoSelection);
            }

            this.notifyDeselected(item);
        }

        getItems(): BrowseItem<M>[] {
            return this.items;
        }

        setItems(items: BrowseItem<M>[]) {
            var itemsToRemove = this.items.filter((item: BrowseItem<M>) => {
                for (var i = 0; i < items.length; i++) {
                    if (item.getPath() == items[i].getPath()) {
                        return false;
                    }
                }
                return true;
            });
            itemsToRemove.forEach((item: BrowseItem<M>) => {
                this.removeItem(item);
            });

            items.forEach((item: BrowseItem<M>) => {
                this.addItem(item);
            });
        }

        createItemViewer(item: BrowseItem<M>): api.ui.Viewer<M>  {
            var viewer = new api.ui.Viewer<M>();
            viewer.setObject(item.getModel());
            return viewer;
        }

        private indexOf(item: BrowseItem<M>): number {
            for (var i = 0; i < this.items.length; i++) {
                if (item.getPath() == this.items[i].getPath()) {
                    return i;
                }
            }
            return -1;
        }

        onDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.deselectedListeners.push(listener);
        }

        unDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.deselectedListeners = this.deselectedListeners.filter((currentListener: (event: ItemDeselectedEvent<M>)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyDeselected(item: BrowseItem<M>) {
            this.deselectedListeners.forEach((listener: (event: ItemDeselectedEvent<M>)=>void) => {
                listener.call(this, new ItemDeselectedEvent(item));
            });
        }

    }
}
