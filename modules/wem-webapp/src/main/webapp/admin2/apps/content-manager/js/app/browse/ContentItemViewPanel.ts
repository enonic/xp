module app_browse {

    export class ContentItemViewPanel extends api_app_browse.ItemViewPanel {

        private id:string;
        private editAction: app_view.EditContentAction;
        private deleteAction:app_view.DeleteContentAction;
        private closeAction:api_ui.Action;

        constructor(id:string) {

            this.id = id;
            this.editAction = new app_view.EditContentAction(this);
            this.deleteAction = new app_view.DeleteContentAction(this);
            this.closeAction = new app_view.CloseContentAction(this, true);

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
