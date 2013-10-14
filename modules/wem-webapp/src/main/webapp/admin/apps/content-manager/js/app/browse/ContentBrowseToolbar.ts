module app_browse {

    export class ContentBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor(actions:ContentBrowseActions) {
            super();

            this.addAction(actions.SHOW_NEW_CONTENT_DIALOG_ACTION);
            this.addAction(actions.EDIT_CONTENT);
            this.addAction(actions.OPEN_CONTENT);
            this.addAction(actions.DELETE_CONTENT);
            this.addAction(actions.DUPLICATE_CONTENT);
            this.addAction(actions.MOVE_CONTENT);
            this.addAction(actions.SHOW_NEW_CONTENT_GRID);
            this.addGreedySpacer();
            this.addAction(actions.BROWSE_CONTENT_SETTINGS);

            var previewDetailsToggler = new api_ui.ToggleSlide({
                turnOnAction: actions.SHOW_PREVIEW,
                turnOffAction: actions.SHOW_DETAILS
            }, false);
            super.addElement(previewDetailsToggler);
        }
    }
}
