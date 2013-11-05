module app_new {

    export class NewSchemaDialog extends api_ui_dialog.ModalDialog {

        private schemaTypesList:SchemaTypesList;

        private schemaTypeListItems:SchemaTypeListItem[] = [
            {
                type: api_schema.SchemaKind.CONTENT_TYPE,
                displayName: 'Content Type',
                iconUrl: api_util.getRestUri('schema/image/ContentType:structured')
            },
            {
                type: api_schema.SchemaKind.RELATIONSHIP_TYPE,
                displayName: 'Relationship Type',
                iconUrl: api_util.getRestUri('schema/image/RelationshipType:_') // default icon for RelationshipType
            },
            {
                type: api_schema.SchemaKind.MIXIN,
                displayName: 'Mixin',
                iconUrl: api_util.getRestUri('schema/image/Mixin:_') // default icon for Mixin
            }
        ];

        constructor() {
            super({
                title: "Select Kind",
                width: 400,
                height: 300
            });

            this.addClass("new-schema-dialog");

            this.schemaTypesList = new SchemaTypesList(this.schemaTypeListItems);
            this.appendChildToContentPanel(this.schemaTypesList);

            this.setCancelAction(new CancelNewDialogAction());
            this.getCancelAction().addExecutionListener(()=> {
                this.close();
            });

            this.schemaTypesList.addListener({
                onSelected: (selectedItem:SchemaTypeListItem) => {
                    this.close();
                    new NewSchemaEvent(selectedItem.type).fire();
                }
            });

            api_dom.Body.get().appendChild(this);
        }

    }

    export class CancelNewDialogAction extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }

    }

}