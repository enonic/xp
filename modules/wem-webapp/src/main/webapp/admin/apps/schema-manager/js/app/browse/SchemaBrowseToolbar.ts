module app_browse {

    export class SchemaBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor(actions:SchemaBrowseActions) {
            super();
            super.addAction(actions.NEW_SCHEMA);
            super.addAction(actions.EDIT_SCHEMA);
            super.addAction(actions.OPEN_SCHEMA);
            super.addAction(actions.DELETE_SCHEMA);
            super.addAction(actions.REINDEX_SCHEMA);
            super.addAction(actions.EXPORT_SCHEMA);
        }
    }
}