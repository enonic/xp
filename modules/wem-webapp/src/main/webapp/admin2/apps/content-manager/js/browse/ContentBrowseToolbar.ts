module app_browse {

    export class ContentBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor() {
            super();
            super.addAction(ContentBrowseActions.NEW_CONTENT);
            super.addAction(ContentBrowseActions.EDIT_CONTENT);
            super.addAction(ContentBrowseActions.OPEN_CONTENT);
            super.addAction(ContentBrowseActions.DELETE_CONTENT);
            super.addAction(ContentBrowseActions.DUPLICATE_CONTENT);
            super.addAction(ContentBrowseActions.MOVE_CONTENT);
            super.addGreedySpacer();

            super.addAction(app_browse.ContentBrowseActions.BROWSE_CONTENT_SETTINGS);

            var displayModeToggle = new api_ui_toolbar.ToggleSlide({
                turnOnAction: ContentBrowseActions.SHOW_PREVIEW,
                turnOffAction: ContentBrowseActions.SHOW_DETAILS
            }, false);
            super.addElement(displayModeToggle);
        }
    }
}
