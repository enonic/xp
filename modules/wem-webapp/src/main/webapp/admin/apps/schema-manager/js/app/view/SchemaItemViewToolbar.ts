module app.view {

    export interface SchemaItemViewToolbarParams {
        editAction: api.ui.Action;
        deleteAction: api.ui.Action;
        closeAction: api.ui.Action;
    }

    export class SchemaItemViewToolbar extends api.ui.toolbar.Toolbar {

        constructor(params:SchemaItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction)

        }
    }
}
