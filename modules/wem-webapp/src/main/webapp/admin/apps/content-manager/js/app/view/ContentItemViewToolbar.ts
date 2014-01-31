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


            var displayModeToggle = new api.ui.ToggleSlide({
                turnOnAction: params.showPreviewAction,
                turnOffAction: params.showDetailsAction
            }, false);
            displayModeToggle.setEnabled(params.showPreviewAction.isEnabled());
            params.showPreviewAction.addPropertyChangeListener((action: api.ui.Action) => {
                displayModeToggle.setEnabled(action.isEnabled());
            });
            super.addElement(displayModeToggle);
            super.addAction(params.closeAction);
        }
    }
}
