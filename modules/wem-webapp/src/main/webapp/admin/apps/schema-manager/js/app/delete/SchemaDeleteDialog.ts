module app_delete {

    export class SchemaDeleteDialog extends api_app_delete.DeleteDialog {

        private schemaToDelete:api_schema.Schema[];

        private nameToSchemaMap:Object = [];

        constructor() {
            super("Schema");

            this.setDeleteAction(new SchemaDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var contentTypesToDelete:string[] = [],
                    mixinsToDelete:string[] = [],
                    relationshipTypesToDelete:string[] = [];

                this.schemaToDelete.forEach((schema:api_schema.Schema) => {
                    this.nameToSchemaMap[schema.getName()] = schema;
                    if( api_schema.SchemaKind.CONTENT_TYPE.equals(schema.getSchemaKind()) ) {
                        contentTypesToDelete.push(schema.getName());
                    }
                    else if( api_schema.SchemaKind.MIXIN.equals(schema.getSchemaKind()) ) {
                        mixinsToDelete.push(schema.getName());
                    }
                    else if( api_schema.SchemaKind.RELATIONSHIP_TYPE.equals(schema.getSchemaKind()) ) {
                        relationshipTypesToDelete.push(schema.getName());
                    }
                });

                if ( contentTypesToDelete.length > 0 ) {
                    new api_schema_content.DeleteContentTypeRequest(contentTypesToDelete).sendAndParse()
                        .done((result:api_schema.SchemaDeleteResult) => {
                            this.processSchemaDeleteResponse(result);
                        });
                }

                if ( mixinsToDelete.length > 0 ) {
                    new api_schema_mixin.DeleteMixinRequest(mixinsToDelete).sendAndParse()
                        .done((result:api_schema.SchemaDeleteResult) => {
                            this.processSchemaDeleteResponse(result);
                        });
                }

                if ( relationshipTypesToDelete.length > 0 ) {
                    new api_schema_relationshiptype.DeleteRelationshipTypeRequest(relationshipTypesToDelete).sendAndParse()
                        .done((result:api_schema.SchemaDeleteResult) => {
                            this.processSchemaDeleteResponse(result);
                        });
                }
            });
        }

        setSchemaToDelete(schemas:api_schema.Schema[]):SchemaDeleteDialog {
            this.schemaToDelete = schemas;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i = 0; i < schemas.length; i++) {
                var schema = schemas[i];
                deleteItems.push(new api_app_delete.DeleteItem(schema.getIconUrl(), schema.getDisplayName()));
            }
            this.setDeleteItems(deleteItems);
            return this;
        }

        private processSchemaDeleteResponse(result:api_schema.SchemaDeleteResult) {
            if ( result.getSuccesses().length > 0 ) {
                var deletedSchemas:api_schema.Schema[] = [];
                var names:string[] = [];

                result.getSuccesses().forEach((success:api_schema.SuccessResult) => {
                    names.push(success.getName());
                    var schema = this.nameToSchemaMap[success.getName()];
                    if ( schema ) {
                        deletedSchemas.push(schema);
                    }
                });

                if (deletedSchemas.length > 0) {
                    api_notify.showFeedback(deletedSchemas[0].getSchemaKind().toString()
                                                + (deletedSchemas.length > 1 ? "s" : "") + " ['" + names.join(', ') + "'] deleted!");
                    new api_schema.SchemaDeletedEvent(deletedSchemas).fire();
                }

            }

            if ( result.getFailures().length > 0) {
                result.getFailures().forEach((failure:api_schema.FailureResult) => {
                    api_notify.showWarning(failure.getReason());
                });
            }

            this.close();
        }
    }

    export class SchemaDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}