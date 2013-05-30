module API.toolbar {

    /**
     * TODO: The upcoming successor of BrowseToolbar, when the Toolbar code is working....
     */
    export class BrowseToolbar2 extends API_ui_toolbar.Toolbar {

        constructor() {
            super([APP_action.SpaceActions.NEW_SPACE, APP_action.SpaceActions.OPEN_SPACE, APP_action.SpaceActions.EDIT_SPACE,
                APP_action.SpaceActions.DELETE_SPACE]);
        }
    }
}
