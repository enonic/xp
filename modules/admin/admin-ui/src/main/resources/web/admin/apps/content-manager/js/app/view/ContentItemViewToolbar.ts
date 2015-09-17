module app.view {

    export interface ContentItemViewToolbarParams {
        editAction: api.ui.Action;
        deleteAction: api.ui.Action;
    }

    export class ContentItemViewToolbar extends api.ui.toolbar.Toolbar {

        constructor(params: ContentItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
        }
    }
}
