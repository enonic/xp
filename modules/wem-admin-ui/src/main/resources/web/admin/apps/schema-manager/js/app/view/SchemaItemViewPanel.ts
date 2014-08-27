module app.view {

    export class SchemaItemViewPanel extends api.app.view.ItemViewPanel<api.schema.Schema> {

        private editAction:api.ui.Action;
        private deleteAction:api.ui.Action;
        private closeAction:api.ui.Action;
        private statisticsPanel:SchemaItemStatisticsPanel;

        constructor() {

            this.editAction = new app.view.action.EditSchemaAction(this);
            this.deleteAction = new app.view.action.DeleteSchemaAction(this);
            this.closeAction = new app.view.action.CloseSchemaAction(this, true);

            var toolbar = new SchemaItemViewToolbar({
                editAction: this.editAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction
            });

            this.statisticsPanel = new SchemaItemStatisticsPanel();

            super(toolbar, this.statisticsPanel);

        }

        setItem(item:api.app.view.ViewItem<api.schema.Schema>) {
            super.setItem(item);
            this.statisticsPanel.setItem(item);
        }

    }

}
