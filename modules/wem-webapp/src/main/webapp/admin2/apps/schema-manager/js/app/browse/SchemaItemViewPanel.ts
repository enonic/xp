module app_browse {

    export class SchemaItemViewPanel extends api_app_browse.ItemViewPanel {

        private id:string;
        private editAction:api_ui.Action;
        private deleteAction:api_ui.Action;
        private closeAction:api_ui.Action;

        constructor(id:string) {

            this.id = id;
            this.editAction = new app_view.EditSchemaAction(this);
            this.deleteAction = new app_view.DeleteSchemaAction(this);
            this.closeAction = new app_view.CloseSchemaAction(this, true);

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
