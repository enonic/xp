module APP.ui {

    /**
     * TODO: The upcoming successor of BrowseToolbar, when the Toolbar code is working....
     */
    export class BrowseToolbar2 extends API_ui_toolbar.Toolbar {

        constructor() {
            super();
            super.addAction(APP_action.SpaceActions.NEW_SPACE);
            super.addAction(APP_action.SpaceActions.EDIT_SPACE);
            super.addAction(APP_action.SpaceActions.OPEN_SPACE);
            super.addAction(APP_action.SpaceActions.DELETE_SPACE);
        }
    }
}
