module app.remove {

    export class SchemaDeleteDialog extends api.app.remove.DeleteDialog {

        private schemaToDelete:api.schema.Schema[];

        private nameToSchemaMap:Object = [];

        constructor() {
            super("Schema");

            this.setDeleteAction(new SchemaDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var contentTypesToDelete:string[] = [],
                    mixinsToDelete:string[] = [],
                    relationshipTypesToDelete:string[] = [];

                this.schemaToDelete.forEach((schema:api.schema.Schema) => {
                    this.nameToSchemaMap[schema.getName()] = schema;
                    if( api.schema.SchemaKind.CONTENT_TYPE.equals(schema.getSchemaKind()) ) {
                        contentTypesToDelete.push(schema.getName());
                    }
                    else if( api.schema.SchemaKind.MIXIN.equals(schema.getSchemaKind()) ) {
                        mixinsToDelete.push(schema.getName());
                    }
                    else if( api.schema.SchemaKind.RELATIONSHIP_TYPE.equals(schema.getSchemaKind()) ) {
                        relationshipTypesToDelete.push(schema.getName());
                    }
                });

                if ( contentTypesToDelete.length > 0 ) {
                    new api.schema.content.DeleteContentTypeRequest(contentTypesToDelete).sendAndParse()
                        .done((result:api.schema.SchemaDeleteResult) => {
                            this.processSchemaDeleteResponse(result);
                        });
                }

                if ( mixinsToDelete.length > 0 ) {
                    new api.schema.mixin.DeleteMixinRequest(mixinsToDelete).sendAndParse()
                        .done((result:api.schema.SchemaDeleteResult) => {
                            this.processSchemaDeleteResponse(result);
                        });
                }

                if ( relationshipTypesToDelete.length > 0 ) {
                    new api.schema.relationshiptype.DeleteRelationshipTypeRequest(relationshipTypesToDelete).sendAndParse()
                        .done((result:api.schema.SchemaDeleteResult) => {
                            this.processSchemaDeleteResponse(result);
                        });
                }
            });
        }

        setSchemaToDelete(schemas:api.schema.Schema[]):SchemaDeleteDialog {
            this.schemaToDelete = schemas;

            var deleteItems:api.app.remove.DeleteItem[] = [];
            for (var i = 0; i < schemas.length; i++) {
                var schema = schemas[i];
                deleteItems.push(new api.app.remove.DeleteItem(schema.getIconUrl(), schema.getDisplayName()));
            }
            this.setDeleteItems(deleteItems);
            return this;
        }

        private processSchemaDeleteResponse(result:api.schema.SchemaDeleteResult) {
            if ( result.getSuccesses().length > 0 ) {
                var deletedSchemas:api.schema.Schema[] = [];
                var names:string[] = [];

                result.getSuccesses().forEach((success:api.schema.SuccessResult) => {
                    names.push(success.getName());
                    var schema = this.nameToSchemaMap[success.getName()];
                    if ( schema ) {
                        deletedSchemas.push(schema);
                    }
                });

                if (deletedSchemas.length > 0) {
                    api.notify.showFeedback(deletedSchemas[0].getSchemaKind().toString()
                                                + (deletedSchemas.length > 1 ? "s" : "") + " ['" + names.join(', ') + "'] deleted!");
                    new api.schema.SchemaDeletedEvent(deletedSchemas).fire();
                }

            }

            if ( result.getFailures().length > 0) {
                result.getFailures().forEach((failure:api.schema.FailureResult) => {
                    api.notify.showWarning(failure.getReason());
                });
            }

            this.close();
        }
    }

    export class SchemaDeleteDialogAction extends api.ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}