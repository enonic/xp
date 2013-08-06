module app_new {

    export interface SchemaTypeListItem {
        type: string;
        displayName: string;
        iconUrl: string;
    }

    export class SchemaTypesList extends api_dom.DivEl {

        private ul:api_dom.UlEl;
        private items:SchemaTypeListItem[];

        constructor() {
            super("SchemaTypeList", "schema-type-list");

            this.ul = new api_dom.UlEl("SchemaTypeList");
            this.appendChild(this.ul);

            this.setItems(this.createItems());
        }

        private setItems(items:SchemaTypeListItem[]):SchemaTypesList {
            this.items = items;
            return this.layoutItems(items);
        }

        private createItems():SchemaTypeListItem[] {
            return [
                {
                    type: 'ContentType',
                    displayName: 'Content Type',
                    iconUrl: api_util.getAbsoluteUri('admin/rest/schema/image/ContentType:system:structured')
                },
                {
                    type: 'RelationshipType',
                    displayName: 'Relationship Type',
                    iconUrl: api_util.getAbsoluteUri('admin/rest/schema/image/RelationshipType:_:_') // default icon for RelationshipType
                },
                {
                    type: 'Mixin',
                    displayName: 'Mixin',
                    iconUrl: api_util.getAbsoluteUri('admin/rest/schema/image/Mixin:_:_') // default icon for Mixin
                }
            ]
        }

        private layoutItems(items:SchemaTypeListItem[]):SchemaTypesList {
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
                new NewSchemaEvent(item.type).fire();
            });
            return li;
        }

    }

}