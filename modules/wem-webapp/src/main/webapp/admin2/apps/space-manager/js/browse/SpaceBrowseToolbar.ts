module app_browse {

    export class BrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor() {
            super();
            super.addAction(SpaceBrowseActions.NEW_SPACE);
            super.addAction(SpaceBrowseActions.EDIT_SPACE);
            super.addAction(SpaceBrowseActions.OPEN_SPACE);
            super.addAction(SpaceBrowseActions.DELETE_SPACE);
        }
    }
}
