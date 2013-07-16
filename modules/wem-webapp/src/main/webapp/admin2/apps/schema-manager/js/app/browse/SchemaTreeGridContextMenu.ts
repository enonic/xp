module app_browse {

    export class SpaceTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super();
            super.addAction(SchemaBrowseActions.EDIT_SCHEMA);
            super.addAction(SchemaBrowseActions.OPEN_SCHEMA);
            super.addAction(SchemaBrowseActions.DELETE_SCHEMA);
        }

    }
}
