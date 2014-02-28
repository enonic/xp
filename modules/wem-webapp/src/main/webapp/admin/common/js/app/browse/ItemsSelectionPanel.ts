module api.app.browse {

    export class ItemsSelectionPanel<M> extends api.ui.Panel {

        private deselectedListeners: {(event: ItemDeselectedEvent<M>):void}[] = [];
        private items: BrowseItem<M>[] = [];
        private selectionItems: SelectionItem<M>[] = [];

        constructor() {
            super();
            this.getEl().setInnerHtml("Nothing selected");
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

        addItem(item: BrowseItem<M>) {
            if (this.indexOf(item) >= 0) {
                return;
            }

            if (this.items.length == 0) {
                this.removeChildren();
            }

            var removeCallback = () => {
                this.removeItem(item);
            };
            var selectionItem = new SelectionItem(item, removeCallback);

            this.appendChild(selectionItem);
            this.selectionItems.push(selectionItem);
            this.items.push(item);
        }

        removeItem(item: BrowseItem<M>) {
            var index = this.indexOf(item);
            if (index < 0) {
                return;
            }

            this.selectionItems[index].remove();
            this.selectionItems.splice(index, 1);
            this.items.splice(index, 1);

            if (this.items.length == 0) {
                this.getEl().setInnerHtml("Nothing selected");
            }

            this.notifyDeselected(item);
        }

        getItems(): BrowseItem<M>[] {
            return this.items;
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

    export class SelectionItem<M> extends api.dom.DivEl {

        private browseItem: api.app.browse.BrowseItem<M>;

        constructor(browseItem: BrowseItem<M>, removeCallback?: () => void) {
            super("browse-selection-item");
            this.browseItem = browseItem;
            this.setIcon(this.browseItem.getIconUrl(), 32);
            this.setData(this.browseItem.getDisplayName(), this.browseItem.getPath());
            this.addRemoveButton(removeCallback);
        }

        private addRemoveButton(callback?: () => void) {
            var removeEl = document.createElement("div");
            removeEl.className = "remove";
            removeEl.innerHTML = "&times;";
            removeEl.addEventListener("click", (event) => {
                if (callback) {
                    callback();
                }
            });
            this.getEl().appendChild(removeEl);
        }

        private setIcon(iconUrl: string, size: number) {
            this.getEl().appendChild(api.util.loader.ImageLoader.get(iconUrl + "?size=" + size, 32, 32));
        }

        private setData(title: string, subtitle: string) {
            var titleEl = document.createElement("h6");
            titleEl.innerHTML = title;

            var subtitleEl = document.createElement("small");
            subtitleEl.innerHTML = subtitle;
            titleEl.appendChild(subtitleEl);

            this.getEl().appendChild(titleEl);
            return titleEl;
        }

        getBrowseItem(): BrowseItem<M> {
            return this.browseItem;
        }
    }

}
