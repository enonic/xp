module app_view {

    export interface ContentItemViewToolbarParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
        showPreviewAction: api_ui.Action;
        showDetailsAction: api_ui.Action;
    }

    export class ContentItemViewToolbar extends api_ui_toolbar.Toolbar {

        constructor(params:ContentItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();


            var displayModeToggle = new api_ui.ToggleSlide({
                turnOnAction: params.showPreviewAction,
                turnOffAction: params.showDetailsAction
            }, false);
            super.addElement(displayModeToggle);
            super.addAction(params.closeAction);
        }
    }
}
