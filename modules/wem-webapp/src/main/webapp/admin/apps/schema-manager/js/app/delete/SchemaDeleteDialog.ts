module app_delete {

    export class SchemaDeleteDialog extends api_app_delete.DeleteDialog {

        private schemaToDelete:api_schema.Schema[];

        constructor() {
            super("Schema");

            this.setDeleteAction(new SchemaDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var deleteContentTypeRequest = new api_schema_content.DeleteContentTypeRequest();
                var deleteMixinRequest = new api_schema_mixin.DeleteMixinRequest();
                var deleteRelationshipTypeRequest = new api_schema_relationshiptype.DeleteRelationshipTypeRequest();

                this.schemaToDelete.forEach((schema:api_schema.Schema) => {
                    if( schema.getSchemaKind().toString() == api_schema.SchemaKind.CONTENT_TYPE.toString() ) {
                        deleteContentTypeRequest.addName(new api_schema_content.ContentTypeName(schema.getName()));
                    }
                    else if( schema.getSchemaKind().toString() == api_schema.SchemaKind.MIXIN.toString() ) {
                        deleteMixinRequest.addName(new api_schema_mixin.MixinName(schema.getName()));
                    }
                    else if( schema.getSchemaKind().toString() == api_schema.SchemaKind.RELATIONSHIP_TYPE.toString() ) {
                        deleteRelationshipTypeRequest.addName(new api_schema_relationshiptype.RelationshipTypeName(schema.getName()));
                    }
                });

                jQuery.when(deleteContentTypeRequest.send(), deleteRelationshipTypeRequest.send(), deleteMixinRequest.send())
                    .done((contentTypeResponse:api_rest.JsonResponse<api_schema.SchemaDeleteJson>,
                            relationshipTypeResponse:api_rest.JsonResponse<api_schema.SchemaDeleteJson>,
                            mixinResponse:api_rest.JsonResponse<api_schema.SchemaDeleteJson>) => {

                        var successes = [].concat(contentTypeResponse ? contentTypeResponse.getResult().successes : []);
                        successes = successes.concat(relationshipTypeResponse ? relationshipTypeResponse.getResult().successes : []);
                        successes = successes.concat(mixinResponse ? mixinResponse.getResult().successes : []);

                        var deletedSchemas:api_schema.Schema[] = [];
                        var names:string[] = [];

                        for (var i = 0; i < successes.length; i++) {
                            var name = successes[i].name;
                            names.push(name);

                            this.schemaToDelete.forEach((schema:api_schema.Schema) => {
                                if(schema.getName() == name) {
                                    deletedSchemas.push(schema);
                                }
                            })
                        }

                        this.close();

                        api_notify.showFeedback('Schema [' + names.join(', ') + '] deleted!');

                        new api_schema.SchemaDeletedEvent(deletedSchemas).fire();
                    });
            });
        }

        setSchemaToDelete(schemas:api_schema.Schema[]):SchemaDeleteDialog {
            this.schemaToDelete = schemas;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i = 0; i < schemas.length; i++) {
                deleteItems.push(new api_app_delete.DeleteItem(schemas[i].getIcon(), schemas[i].getDisplayName()));
            }
            this.setDeleteItems(deleteItems);
            return this;
        }
    }

    export class SchemaDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}