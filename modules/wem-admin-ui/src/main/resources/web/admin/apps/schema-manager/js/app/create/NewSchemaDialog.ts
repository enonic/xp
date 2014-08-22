module app.create {

    export class NewSchemaDialog extends api.ui.dialog.ModalDialog {

        private schemaTypesList: SchemaTypesList;

        private schemaTypeListItems: SchemaTypeListItem[] = [
            {
                type: api.schema.SchemaKind.CONTENT_TYPE,
                displayName: 'Content Type',
                iconUrl: api.util.getRestUri('schema/image/ContentType:structured')
            },
            {
                type: api.schema.SchemaKind.RELATIONSHIP_TYPE,
                displayName: 'Relationship Type',
                iconUrl: api.util.getRestUri('schema/image/RelationshipType:_') // default icon for RelationshipType
            },
            {
                type: api.schema.SchemaKind.MIXIN,
                displayName: 'Mixin',
                iconUrl: api.util.getRestUri('schema/image/Mixin:_') // default icon for Mixin
            }
        ];

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Select Kind")
            });

            this.addClass("new-schema-dialog");

            this.schemaTypesList = new SchemaTypesList(this.schemaTypeListItems);
            this.appendChildToContentPanel(this.schemaTypesList);

            this.setCancelAction(new CancelNewDialogAction());
            this.getCancelAction().onExecuted(()=> {
                this.close();
            });

            this.schemaTypesList.onSelected((event: app.create.ItemSelectedEvent) => {
                this.close();
                new NewSchemaEvent(event.getSchemaType().type).fire();
            });

            api.dom.Body.get().appendChild(this);
        }

    }

}