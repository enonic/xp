module app_browse {

    export interface SpaceItemViewToolbarParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class SpaceItemViewToolbar extends api_ui_toolbar.Toolbar {

        constructor(params:SpaceItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction)

        }
    }
}
