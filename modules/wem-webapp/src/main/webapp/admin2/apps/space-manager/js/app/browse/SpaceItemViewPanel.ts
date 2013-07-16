module app_browse {

    export class SpaceItemViewPanel extends api_app_browse.ItemViewPanel {

        private id:string;
        private editAction: app_view.EditSpaceAction;
        private deleteAction:app_view.DeleteSpaceAction;
        private closeAction:api_ui.Action;

        constructor(id:string) {

            this.id = id;
            this.editAction = new app_view.EditSpaceAction(this);
            this.deleteAction = new app_view.DeleteSpaceAction(this);
            this.closeAction = new app_view.CloseSpaceAction(this, true);

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