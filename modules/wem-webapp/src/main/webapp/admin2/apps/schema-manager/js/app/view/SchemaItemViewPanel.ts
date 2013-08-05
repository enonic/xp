module app_view {

    export class SchemaItemViewPanel extends api_app_view.ItemViewPanel {

        private id:string;
        private editAction:api_ui.Action;
        private deleteAction:api_ui.Action;
        private closeAction:api_ui.Action;

        constructor(id:string) {

            this.id = id;
            this.editAction = new EditSchemaAction(this);
            this.deleteAction = new DeleteSchemaAction(this);
            this.closeAction = new CloseSchemaAction(this, true);

            var toolbar = new SchemaItemViewToolbar({
                editAction: this.editAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction
            });

            var stats = new SchemaItemStatisticsPanel({
                editAction: this.editAction,
                deleteAction: this.deleteAction
            });

            super(toolbar, stats);

        }

    }

}
