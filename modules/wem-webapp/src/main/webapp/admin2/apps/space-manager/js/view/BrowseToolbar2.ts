module APP.ui {

    /**
     * TODO: The upcoming successor of BrowseToolbar, when the Toolbar code is working....
     */
    export class BrowseToolbar2 extends API_ui_toolbar.Toolbar {

        constructor() {
            super();
            super.addAction(APP.SpaceActions.NEW_SPACE);
            super.addAction(APP.SpaceActions.EDIT_SPACE);
            super.addAction(APP.SpaceActions.OPEN_SPACE);
            super.addAction(APP.SpaceActions.DELETE_SPACE);
        }
    }
}
