module app.create {

    export interface SchemaTypeListItem {
        type: api.schema.SchemaKind;
        displayName: string;
        iconUrl: string;
    }

    export class SchemaTypesList extends api.dom.DivEl implements api.event.Observable {

        private ul:api.dom.UlEl;

        private listeners:SchemaTypesListListener[] = [];

        constructor(items:SchemaTypeListItem[]) {
            super(true, "schema-type-list");

            this.ul = new api.dom.UlEl(true);
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

        private renderListItem(item:SchemaTypeListItem):api.dom.LiEl {
            var li = new api.dom.LiEl(true, "schema-type-list-item");
            var img = new api.dom.ImgEl(item.iconUrl);
            var h6 = new api.dom.H6El();
            h6.getEl().setInnerHtml(item.displayName);

            li.appendChild(img);
            li.appendChild(h6);

            li.getEl().addEventListener("click", (event:Event) => {
                this.notifySelected(item);
            });
            return li;
        }
    }
}