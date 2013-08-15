module app_view {

    export interface ContentItemViewToolbarParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class ContentItemViewToolbar extends api_ui_toolbar.Toolbar {

        constructor(params:ContentItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction);
            var displayModeToggle = new api_ui.ToggleSlide({
                turnOnAction: app_browse.ContentBrowseActions.SHOW_PREVIEW,
                turnOffAction: app_browse.ContentBrowseActions.SHOW_DETAILS
            }, false);
            super.addElement(displayModeToggle);

        }
    }
}
