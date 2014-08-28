module app.view.action {

    export class DeleteSchemaAction extends api.ui.Action {

        constructor(panel: api.app.view.ItemViewPanel<api.schema.Schema>) {
            super("Delete", "mod+del");
            this.onExecuted(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this schema?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        panel.close();

                        var schema = panel.getItem().getModel();

                        var request;
                        if (schema.getSchemaKind() == api.schema.SchemaKind.CONTENT_TYPE) {
                            request = new api.schema.content.DeleteContentTypeRequest([schema.getName()]);
                        } else if (schema.getSchemaKind() == api.schema.SchemaKind.RELATIONSHIP_TYPE) {
                            request = new api.schema.relationshiptype.DeleteRelationshipTypeRequest([schema.getName()]);
                        } else if (schema.getSchemaKind() == api.schema.SchemaKind.MIXIN) {
                            request = new api.schema.mixin.DeleteMixinRequest([schema.getName()]);
                        }

                        if (request) {
                            request.send().done((jsonResponse: api.rest.JsonResponse<api.schema.SchemaDeleteJson>) => {
                                var json = jsonResponse.getResult();

                                if (json.successes.length > 0) {
                                    api.notify.showFeedback('Content [' + json.successes[0].name + '] deleted!');
                                    new api.schema.SchemaDeletedEvent([schema]).fire();
                                }
                            });
                        }

                    }).open();
            });
        }
    }
}