module app_view {

    export class SpaceItemViewPanel extends api_app_view.ItemViewPanel {

        private id:string;
        private editAction:api_ui.Action;
        private deleteAction:api_ui.Action;
        private closeAction:api_ui.Action;
        private statisticsPanel:api_app_view.ItemStatisticsPanel;

        constructor(id:string) {

            this.id = id;
            this.editAction = new EditSpaceAction(this);
            this.deleteAction = new DeleteSpaceAction(this);
            this.closeAction = new CloseSpaceAction(this, true);

            var toolbar = new SpaceItemViewToolbar({
                editAction: this.editAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction
            });

            this.statisticsPanel = new SpaceItemStatisticsPanel({
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