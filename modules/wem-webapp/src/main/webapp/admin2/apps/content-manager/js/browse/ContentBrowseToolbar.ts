module app_browse {

    export class ContentBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor() {
            super();
            super.addAction(app_browse.ContentBrowseActions.NEW_CONTENT);
            super.addAction(app_browse.ContentBrowseActions.EDIT_CONTENT);
            super.addAction(app_browse.ContentBrowseActions.OPEN_CONTENT);
            super.addAction(app_browse.ContentBrowseActions.DELETE_CONTENT);
            super.addAction(app_browse.ContentBrowseActions.DUPLICATE_CONTENT);
            super.addAction(app_browse.ContentBrowseActions.MOVE_CONTENT);
            super.addGreedySpacer();

            super.addAction(app_browse.ContentBrowseActions.BROWSE_CONTENT_SETTINGS);

            var displayModeToggle = new api_ui_toolbar.ToggleSlide({
                turnOnAction: app_browse.ContentBrowseActions.SHOW_PREVIEW,
                turnOffAction: app_browse.ContentBrowseActions.SHOW_DETAILS
            }, false);
            super.addElement(displayModeToggle);
        }
    }
}
