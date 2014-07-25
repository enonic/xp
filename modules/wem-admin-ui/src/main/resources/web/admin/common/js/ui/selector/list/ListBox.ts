module api.ui.selector.list {

    export class ListBox<T> extends api.ui.panel.Panel {

        private ul: api.dom.UlEl;

        private items: T[] = [];

        constructor(className?: string) {
            super(className);

            this.ul = new api.dom.UlEl();
            this.appendChild(this.ul);
        }

        setItems(items: T[]) {
            this.items = items;
            this.layoutList(items);
        }

        clearItems() {
            // correct way to empty array
            this.items.length = 0;
            this.layoutList(this.items);
        }

        addItem(item: T) {
            this.items.push(item);
            this.layoutList(this.items);
        }

        removeItem(item: T) {
            var index = this.items.indexOf(item);
            if (index > -1) {
                this.items.splice(index, 1);
                this.layoutList(this.items);
            }
        }

        layoutList(items: T[]) {
            this.ul.removeChildren();
            for (var i = 0; i < items.length; i++) {
                var listItemView = this.createListItem(items[i]);
                this.ul.appendChild(listItemView);
            }
        }

        createListItem(item: T): api.dom.Element {
            throw new Error("You must override createListItem to create views for list items");
        }

    }

}