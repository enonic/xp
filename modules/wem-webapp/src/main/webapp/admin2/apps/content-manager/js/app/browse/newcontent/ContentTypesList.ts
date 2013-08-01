module app_browse_newcontent {

    export class ContentTypesList extends api_dom.DivEl {

        private ul:api_dom.UlEl;

        private nodes:api_remote_contenttype.ContentTypeListNode[];

        constructor(nodes?:api_remote_contenttype.ContentTypeListNode[]) {
            super("ContentTypesList", "node-list");

            this.ul = new api_dom.UlEl("ContentTypesList");
            this.appendChild(this.ul);

            if (nodes) {
                this.setNodes(nodes);
            }
        }

        setNodes(nodes:api_remote_contenttype.ContentTypeListNode[]):ContentTypesList {
            this.nodes = nodes;
            return this.layoutNodes(nodes);
        }

        getNodes():api_remote_contenttype.ContentTypeListNode[] {
            return this.nodes;
        }

        filter(property:string, value:any):ContentTypesList {
            if (!value || value.length == 0) {
                this.clearFilter();
            }
            var filteredNodes:api_remote_contenttype.ContentTypeListNode[] = [];
            var regexp = new RegExp(value, 'i');

            for (var i = 0; i < this.nodes.length; i++) {
                var node = this.nodes[i];
                if (regexp.test(node[property])) {
                    filteredNodes.push(node);
                }
            }
            return this.layoutNodes(filteredNodes);
        }

        clearFilter():ContentTypesList {
            this.layoutNodes(this.nodes);
            return this;
        }

        private layoutNodes(nodes:api_remote_contenttype.ContentTypeListNode[]):ContentTypesList {
            this.ul.removeChildren();
            for (var i = 0; i < nodes.length; i++) {
                this.ul.appendChild(this.renderListItem(nodes[i]));
            }
            return this;
        }

        private renderListItem(node:api_remote_contenttype.ContentTypeListNode):api_dom.LiEl {
            var item = new api_dom.LiEl("ContentTypesListItem", "node-list-item");
            var img = new api_dom.ImgEl(node.iconUrl);
            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(node.displayName);
            var p = new api_dom.PEl();
            p.getEl().setInnerHtml(node.name);
            item.appendChild(img);
            item.appendChild(h6);
            item.appendChild(p);
            item.getEl().addEventListener("click", function (event:Event) => {
                new NewContentEvent(node).fire();
            });
            return item;
        }

    }


}