module app_new {

    export interface SchemaTypeListItem {
        type: string;
        displayName: string;
        iconUrl: string;
    }

    export class SchemaTypesList extends api_dom.DivEl implements api_event.Observable {

        private ul:api_dom.UlEl;

        private listeners:SchemaTypesListListener[] = [];

        constructor(items:SchemaTypeListItem[]) {
            super("SchemaTypeList", "schema-type-list");

            this.ul = new api_dom.UlEl("SchemaTypeList");
            this.appendChild(this.ul);

            this.layoutItems(items);
        }

        addListener(listener:SchemaTypesListListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:SchemaTypesListListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySelected(schemaType:SchemaTypeListItem) {
            this.listeners.forEach((listener:SchemaTypesListListener) => {
                listener.onSelected(schemaType);
            });
        }

        private layoutItems(items:SchemaTypeListItem[]) {
            this.ul.removeChildren();
            for (var i = 0; i < items.length; i++) {
                this.ul.appendChild(this.renderListItem(items[i]));
            }
            return this;
        }

        private renderListItem(item:SchemaTypeListItem):api_dom.LiEl {
            var li = new api_dom.LiEl("SchemaTypeListItem", "schema-type-list-item");
            var img = new api_dom.ImgEl(item.iconUrl);
            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(item.displayName);

            li.appendChild(img);
            li.appendChild(h6);

            li.getEl().addEventListener("click", function (event:Event) => {
                this.notifySelected(item);
            });
            return li;
        }
    }
}