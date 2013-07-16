module app_browse {

    export class SpaceBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor() {
            super();
            super.addActions(SpaceBrowseActions.ACTIONS);
        }
    }
}
