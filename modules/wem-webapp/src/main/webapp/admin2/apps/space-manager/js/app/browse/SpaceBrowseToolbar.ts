module app_browse {

    export class SpaceBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor(actions:SpaceBrowseActions) {
            super();
            super.addAction(actions.NEW_SPACE);
            super.addAction(actions.EDIT_SPACE);
            super.addAction(actions.OPEN_SPACE);
            super.addAction(actions.DELETE_SPACE);
        }
    }
}
