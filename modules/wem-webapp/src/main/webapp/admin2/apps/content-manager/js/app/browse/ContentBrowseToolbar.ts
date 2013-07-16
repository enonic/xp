module app_browse {

    export class ContentBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor() {
            super();

            super.addActions(ContentBrowseActions.ACTIONS);
            super.addGreedySpacer();
            super.addAction(app_browse.ContentBrowseActions.BROWSE_CONTENT_SETTINGS);

            var displayModeToggle = new api_ui.ToggleSlide({
                turnOnAction: ContentBrowseActions.SHOW_PREVIEW,
                turnOffAction: ContentBrowseActions.SHOW_DETAILS
            }, false);
            super.addElement(displayModeToggle);
        }
    }
}
