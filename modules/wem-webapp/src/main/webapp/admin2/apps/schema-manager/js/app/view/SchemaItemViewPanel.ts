module app_view {

    export class SchemaItemViewPanel extends api_app_view.ItemViewPanel {

        private editAction:api_ui.Action;
        private deleteAction:api_ui.Action;
        private closeAction:api_ui.Action;
        private statisticsPanel:api_app_view.ItemStatisticsPanel;

        constructor() {

            this.editAction = new EditSchemaAction(this);
            this.deleteAction = new DeleteSchemaAction(this);
            this.closeAction = new CloseSchemaAction(this, true);

            var toolbar = new SchemaItemViewToolbar({
                editAction: this.editAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction
            });

            this.statisticsPanel = new SchemaItemStatisticsPanel({
                editAction: this.editAction,
                deleteAction: this.deleteAction
            });

            super(toolbar, this.statisticsPanel);

        }

        setItem(item:api_app_view.ViewItem) {
            super.setItem(item);
            this.statisticsPanel.setItem(item);
        }

    }

}
