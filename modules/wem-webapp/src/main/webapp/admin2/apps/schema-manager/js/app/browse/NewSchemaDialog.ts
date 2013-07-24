module app_browse {

    export class NewSchemaDialog extends api_ui_dialog.ModalDialog {

        private cancelAction:api_ui.Action = new CancelNewDialogAction();
        private selectAction:SelectContentTypeKindAction;

        private kindList;

        constructor() {
            super({
                title: "Select Kind",
                width: 400,
                height: 300
            });

            this.addClass("new-dialog");

            this.kindList = new ContentTypeKindList();
            this.appendChildToContentPanel(this.kindList);

            this.setCancelAction(this.cancelAction);
            this.cancelAction.addExecutionListener(()=> {
                this.close();
            });

            this.setSelectAction(new SelectContentTypeKindAction());

            api_dom.Body.get().appendChild(this);
        }

        setSelectAction(action:SelectContentTypeKindAction) {

            this.kindList.setSelectAction(action);
            this.selectAction = action;
            this.selectAction.addExecutionListener(()=> {
                this.close();
            });
        }

        getSelectAction():SelectContentTypeKindAction {
            return this.selectAction;
        }
    }

    export class CancelNewDialogAction extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }

    }

    export class SelectContentTypeKindAction extends api_ui.Action {

        private contentTypeKind;

        constructor(contentTypeKind?) {
            super("SelectContentTypeKind");
            this.contentTypeKind = contentTypeKind;
        }

        setContentTypeKind(contentTypeKind):SelectContentTypeKindAction {
            this.contentTypeKind = contentTypeKind;
            return this;
        }

        getContentTypeKind() {
            return this.contentTypeKind;
        }

    }


    interface ContentTypeKindListNode {
        displayName: string;
        iconUrl: string;
    }

    class ContentTypeKindList extends api_dom.DivEl {

        private ul:api_dom.UlEl;
        private nodes:ContentTypeKindListNode[];
        private selectAction:SelectContentTypeKindAction;

        constructor() {
            super("ContentTypeKindList", "node-list one-line-list");

            this.ul = new api_dom.UlEl("ContentTypeKindList");
            this.appendChild(this.ul);


            this.setNodes(this.createNodes());

        }

        setNodes(nodes:ContentTypeKindListNode[]):ContentTypeKindList {
            this.nodes = nodes;
            return this.layoutNodes(nodes);
        }

        getNodes():ContentTypeKindListNode[] {
            return this.nodes;
        }

        setSelectAction(selectAction:SelectContentTypeKindAction):ContentTypeKindList {
            this.selectAction = selectAction;
            return this;
        }

        private createNodes():ContentTypeKindListNode[] {
            return [
                {
                    displayName: 'ContentType',
                    iconUrl: api_util.getAbsoluteUri('admin/rest/schema/image/ContentType:system:structured')
                },
                {
                    displayName: 'RelationshipType',
                    iconUrl: api_util.getAbsoluteUri('admin/rest/schema/image/RelationshipType:_:_') // default icon for RelationshipType
                },
                {
                    displayName: 'Mixin',
                    iconUrl: api_util.getAbsoluteUri('admin/rest/schema/image/Mixin:_:_') // default icon for Mixin
                }
            ]
        }

        private layoutNodes(nodes:ContentTypeKindListNode[]):ContentTypeKindList {
            this.ul.removeChildren();
            for (var i = 0; i < nodes.length; i++) {
                this.ul.appendChild(this.renderListItem(nodes[i]));
            }
            return this;
        }

        private renderListItem(node:ContentTypeKindListNode):api_dom.LiEl {
            var item = new api_dom.LiEl("ContentTypeKindListItem", "node-list-item");
            var img = new api_dom.ImgEl(node.iconUrl);
            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(node.displayName);

            item.appendChild(img);
            item.appendChild(h6);

            item.getEl().addEventListener("click", function (event:Event) => {
                if (this.selectAction) {
                    this.selectAction.setContentTypeKind(node).execute();
                }
            });
            return item;
        }

    }


}