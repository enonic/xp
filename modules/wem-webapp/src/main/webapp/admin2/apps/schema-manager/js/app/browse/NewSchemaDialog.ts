module app_browse {

    export class NewSchemaDialog extends api_ui_dialog.ModalDialog {

        private cancelAction:api_ui.Action = new CancelNewDialogAction();
        private selectAction:SelectSchemaTypeAction;

        private kindList;

        constructor() {
            super({
                title: "Select Kind",
                width: 400,
                height: 300
            });

            this.addClass("new-schema-dialog");

            this.kindList = new SchemaTypeList();
            this.appendChildToContentPanel(this.kindList);

            this.setCancelAction(this.cancelAction);
            this.cancelAction.addExecutionListener(()=> {
                this.close();
            });

            this.setSelectAction(new SelectSchemaTypeAction());

            api_dom.Body.get().appendChild(this);
        }

        setSelectAction(action:SelectSchemaTypeAction) {

            this.kindList.setSelectAction(action);
            this.selectAction = action;
            this.selectAction.addExecutionListener(()=> {
                this.close();
            });
        }

        getSelectAction():SelectSchemaTypeAction {
            return this.selectAction;
        }
    }

    export class CancelNewDialogAction extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }

    }

    export class SelectSchemaTypeAction extends api_ui.Action {

        private schemaType: string;

        constructor() {
            super("SelectSchemaType");

            this.addExecutionListener(() => {
                new NewSchemaEvent(this.schemaType).fire();
            });
        }

        setSchemaType(schemaType: string):SelectSchemaTypeAction {
            this.schemaType = schemaType;
            return this;
        }

        getSchemaType(): string {
            return this.schemaType;
        }

    }

    interface SchemaTypeListNode {
        type: string;
        displayName: string;
        iconUrl: string;
    }

    class SchemaTypeList extends api_dom.DivEl {

        private ul:api_dom.UlEl;
        private nodes:SchemaTypeListNode[];
        private selectAction:SelectSchemaTypeAction;

        constructor() {
            super("SchemaTypeList", "schema-type-list");

            this.ul = new api_dom.UlEl("SchemaTypeList");
            this.appendChild(this.ul);

            this.setNodes(this.createNodes());
        }

        setNodes(nodes:SchemaTypeListNode[]):SchemaTypeList {
            this.nodes = nodes;
            return this.layoutNodes(nodes);
        }

        getNodes():SchemaTypeListNode[] {
            return this.nodes;
        }

        setSelectAction(selectAction:SelectSchemaTypeAction):SchemaTypeList {
            this.selectAction = selectAction;
            return this;
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

        private layoutNodes(nodes:SchemaTypeListNode[]):SchemaTypeList {
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
                if (this.selectAction) {
                    this.selectAction.setSchemaType(node.type).execute();
                }
            });
            return item;
        }

    }


}