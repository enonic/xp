module app_delete {

    export class SchemaDeleteDialog extends api_app_delete.DeleteDialog {

        private schemaToDelete:api_schema.Schema[];

        constructor() {
            super("Schema");

            this.setDeleteAction(new SchemaDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var CONTENT_TYPE = api_schema.SchemaKind.CONTENT_TYPE.toString(),
                    RELATIONSHIP_TYPE = api_schema.SchemaKind.RELATIONSHIP_TYPE.toString(),
                    MIXIN = api_schema.SchemaKind.MIXIN.toString();

                var names = {};
                names[CONTENT_TYPE] = [];
                names[RELATIONSHIP_TYPE] = [];
                names[MIXIN] = [];

                this.schemaToDelete.forEach((schema:api_schema.Schema) => {
                    names[schema.getSchemaKind().toString()].push(schema.getName());
                });

                var deleteContentTypeRequest = names[CONTENT_TYPE].length == 0 ? null :
                    new api_schema_content.DeleteContentTypeRequest(names[CONTENT_TYPE]).send();
                var deleteRelationshipTypeRequest = names[RELATIONSHIP_TYPE].length == 0 ? null :
                    new api_schema_relationshiptype.DeleteRelationshipTypeRequest(names[RELATIONSHIP_TYPE]).send();
                var deleteMixinRequest = names[MIXIN] ? null :
                    new api_schema_mixin.DeleteMixinRequest(names[MIXIN]).send();

                jQuery.when(deleteContentTypeRequest, deleteRelationshipTypeRequest, deleteMixinRequest)
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