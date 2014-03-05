module api.ui.selector.list {

    export class ListBox<T> extends api.ui.Panel {

        private h4: api.dom.H4El;
        private ul: api.dom.UlEl;

        private items: T[] = [];

        constructor(className?: string, title?: string) {
            super(className);

            this.setTitle(title);

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

        filter(field: string, value: string) {
            if (!value || value.length == 0) {
                this.clearFilter();
            }
            var filteredItems: T[] = [];
            var regexp = new RegExp(value, 'i');
            var item;
            for (var i = 0; i < this.items.length; i++) {
                item = this.items[i];
                if (regexp.test(item[field])) {
                    filteredItems.push(item);
                }
            }
            this.layoutList(filteredItems);
        }

        clearFilter() {
            this.layoutList(this.items);
        }

        layoutList(items: T[]) {
            this.ul.removeChildren();
            for (var i = 0; i < items.length; i++) {
                var listItemView = this.createListItem(items[i]);
                this.ul.appendChild(listItemView);
            }
        }

        setTitle(title: string) {
            if (title) {
                if (!this.h4) {
                    this.h4 = new api.dom.H4El();
                    this.appendChild(this.h4);
                }
                this.h4.getEl().setInnerHtml(title);
            } else if (this.h4) {
                this.removeChild(this.h4);
                this.h4 = undefined;
            }
        }

        createListItem(item: T): api.dom.Element {
            throw new Error("You must override createListItem to create views for list items");
        }

    }

}