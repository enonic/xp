module app_view {

    export class ContentItemViewPanel extends api_app_view.ItemViewPanel {

        private id:string;
        private editAction:api_ui.Action;
        private deleteAction:api_ui.Action;
        private closeAction:api_ui.Action;

        constructor(id:string) {

            this.id = id;
            this.editAction = new EditContentAction(this);
            this.deleteAction = new DeleteContentAction(this);
            this.closeAction = new CloseContentAction(this, true);

            var toolbar = new ContentItemViewToolbar({
                editAction: this.editAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction
            });

            var stats = new ContentItemStatisticsPanel({
                editAction: this.editAction,
                deleteAction: this.deleteAction
            });

            super(toolbar, stats);

        }

    }

}
