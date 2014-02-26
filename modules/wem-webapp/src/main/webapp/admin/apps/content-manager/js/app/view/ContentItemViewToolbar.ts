module app.view {

    export interface ContentItemViewToolbarParams {
        editAction: api.ui.Action;
        deleteAction: api.ui.Action;
        closeAction: api.ui.Action;
        showPreviewAction: api.ui.Action;
        showDetailsAction: api.ui.Action;
    }

    export class ContentItemViewToolbar extends api.ui.toolbar.Toolbar {

        constructor(params: ContentItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction);
        }
    }
}
