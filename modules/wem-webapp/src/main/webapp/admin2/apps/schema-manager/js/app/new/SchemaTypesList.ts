module app_new {

    export interface SchemaTypeListNode {
        type: string;
        displayName: string;
        iconUrl: string;
    }

    export class SchemaTypesList extends api_dom.DivEl {

        private ul:api_dom.UlEl;
        private nodes:SchemaTypeListNode[];

        constructor() {
            super("SchemaTypeList", "schema-type-list");

            this.ul = new api_dom.UlEl("SchemaTypeList");
            this.appendChild(this.ul);

            this.setNodes(this.createNodes());
        }

        setNodes(nodes:SchemaTypeListNode[]):SchemaTypesList {
            this.nodes = nodes;
            return this.layoutNodes(nodes);
        }

        getNodes():SchemaTypeListNode[] {
            return this.nodes;
        }

        private createNodes():SchemaTypeListNode[] {
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

        private layoutNodes(nodes:SchemaTypeListNode[]):SchemaTypesList {
            this.ul.removeChildren();
            for (var i = 0; i < nodes.length; i++) {
                this.ul.appendChild(this.renderListItem(nodes[i]));
            }
            return this;
        }

        private renderListItem(node:SchemaTypeListNode):api_dom.LiEl {
            var item = new api_dom.LiEl("SchemaTypeListItem", "schema-type-list-item");
            var img = new api_dom.ImgEl(node.iconUrl);
            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(node.displayName);

            item.appendChild(img);
            item.appendChild(h6);

            item.getEl().addEventListener("click", function (event:Event) => {
                new NewSchemaEvent(node.type).fire();
            });
            return item;
        }

    }

}