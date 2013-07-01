module app_browse {

    export class SchemaBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor() {
            super();
            this.addActions(SchemaBrowseActions.ACTIONS);
        }
    }
}