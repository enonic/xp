module app.create {

    export interface SchemaTypeListItem {
        type: api.schema.SchemaKind;
        displayName: string;
        iconUrl: string;
    }

    export class SchemaTypesList extends api.dom.DivEl {

        private ul: api.dom.UlEl;

        private selectedListeners: {(event: ItemSelectedEvent):void}[] = [];

        constructor(items: SchemaTypeListItem[]) {
            super("schema-type-list");

            this.ul = new api.dom.UlEl();
            this.appendChild(this.ul);

            this.layoutItems(items);
        }

        onSelected(listener: (event: ItemSelectedEvent)=>void) {
            this.selectedListeners.push(listener);
        }

        unSelected(listener: (event: ItemSelectedEvent)=>void) {
            this.selectedListeners = this.selectedListeners.filter((currentListener: (event: ItemSelectedEvent)=>void) => {
                return currentListener != listener;
            });
        }

        private notifySelected(schemaType: SchemaTypeListItem) {
            this.selectedListeners.forEach((listener: (event: ItemSelectedEvent)=>void)=> {
                listener.call(this, new ItemSelectedEvent(schemaType));
            })
        }

        private layoutItems(items: SchemaTypeListItem[]) {
            this.ul.removeChildren();
            for (var i = 0; i < items.length; i++) {
                this.ul.appendChild(this.renderListItem(items[i]));
            }
            return this;
        }

        private renderListItem(item: SchemaTypeListItem): api.dom.LiEl {
            var li = new api.dom.LiEl("schema-type-list-item");
            var img = new api.dom.ImgEl(item.iconUrl);
            var h6 = new api.dom.H6El();
            h6.getEl().setInnerHtml(item.displayName);

            li.appendChild(img);
            li.appendChild(h6);

            li.getEl().addEventListener("click", (event: Event) => {
                this.notifySelected(item);
            });
            return li;
        }
    }
}