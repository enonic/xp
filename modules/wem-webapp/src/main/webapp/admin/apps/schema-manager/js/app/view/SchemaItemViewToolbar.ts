module app_view {

    export interface SchemaItemViewToolbarParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class SchemaItemViewToolbar extends api_ui_toolbar.Toolbar {

        constructor(params:SchemaItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction)

        }
    }
}
