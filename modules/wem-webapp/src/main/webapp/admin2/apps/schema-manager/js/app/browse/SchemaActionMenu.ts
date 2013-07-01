module app_browse {

    export class SchemaActionMenu extends api_ui_menu.ActionMenu {

        constructor() {
            super(
                SchemaBrowseActions.OPEN_SCHEMA,
                SchemaBrowseActions.EDIT_SCHEMA,
                SchemaBrowseActions.DELETE_SCHEMA
            );
        }
    }
}
