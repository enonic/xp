module api.ui.selector.list {

    export class ListBox<I> extends api.ui.panel.Panel {

        private ul: api.dom.UlEl;

        private items: I[] = [];

        private itemViews: {[key: string]: api.dom.Element} = {};

        private itemsAddedListeners: {(items: I[]): void}[] = [];
        private itemsRemovedListeners: {(items: I[]): void}[] = [];

        constructor(className?: string) {
            super(className);

            this.ul = new api.dom.UlEl();
            this.appendChild(this.ul);
        }

        setItems(items: I[]) {
            if (this.items.length > 0) {
                this.notifyItemsRemoved(this.items);
            }
            this.items = items;
            if (items.length > 0) {
                this.layoutList(items);
                this.notifyItemsAdded(items);
            }
        }

        clearItems() {
            if (this.items.length > 0) {
                this.notifyItemsRemoved(this.items);
            }
            // correct way to empty array
            this.items.length = 0;
            this.layoutList(this.items);
        }

        addItem(...items: I[]) {
            this.items = this.items.concat(items);
            items.forEach((item) => {
                this.addItemView(item);
            });
            if (items.length > 0) {
                this.notifyItemsAdded(items);
            }
        }

        removeItem(...items: I[]) {
            var itemsRemoved: I[] = [];
            this.items = this.items.filter((item) => {
                var i = items.indexOf(item);
                if (i > -1) {
                    this.removeItemView(item);
                    itemsRemoved.push(item);
                    return false;
                }
                return true;
            });
            if (itemsRemoved.length > 0) {
                this.notifyItemsRemoved(itemsRemoved);
            }
        }

        layoutList(items: I[]) {
            this.ul.removeChildren();
            for (var i = 0; i < items.length; i++) {
                this.addItemView(items[i]);
            }
        }

        createItemView(item: I): api.dom.Element {
            throw new Error("You must override createListItem to create views for list items");
        }

        getItemId(item: I): string {
            throw new Error("You must override getItemId to find item views by items");
        }

        private removeItemView(item: I) {
            var itemView = this.itemViews[this.getItemId(item)];
            if (itemView) {
                this.ul.removeChild(itemView);
            }
        }

        private addItemView(item: I) {
            var itemView = this.createItemView(item);
            this.itemViews[this.getItemId(item)] = itemView;
            this.ul.appendChild(itemView);
        }

        public onItemsAdded(listener: (items: I[]) => void) {
            this.itemsAddedListeners.push(listener);
        }

        public unItemsAdded(listener: (items: I[]) => void) {
            this.itemsAddedListeners = this.itemsAddedListeners.filter((current) => {
                return current !== listener;
            })
        }

        private notifyItemsAdded(items: I[]) {
            this.itemsAddedListeners.forEach((listener) => {
                listener(items);
            })
        }

        public onItemsRemoved(listener: (items: I[]) => void) {
            this.itemsRemovedListeners.push(listener);
        }

        public unItemsRemoved(listener: (items: I[]) => void) {
            this.itemsRemovedListeners = this.itemsRemovedListeners.filter((current) => {
                return current !== listener;
            })
        }

        private notifyItemsRemoved(items: I[]) {
            this.itemsRemovedListeners.forEach((listener) => {
                listener(items);
            })
        }

    }

}