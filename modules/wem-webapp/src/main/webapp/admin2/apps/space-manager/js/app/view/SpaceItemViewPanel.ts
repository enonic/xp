module app_view {

    export class SpaceItemViewPanel extends api_app_view.ItemViewPanel {

        private id:string;
        private editAction:api_ui.Action;
        private deleteAction:api_ui.Action;
        private closeAction:api_ui.Action;

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

            var stats = new SpaceItemStatisticsPanel({
                editAction: this.editAction,
                deleteAction: this.deleteAction
            });

            super(toolbar, stats);

        }

    }

}