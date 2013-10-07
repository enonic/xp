module app_browse {

    export class SchemaTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions:SchemaBrowseActions) {
            this.addAction(actions.NEW_SCHEMA);
            this.addAction(actions.EDIT_SCHEMA);
            this.addAction(actions.OPEN_SCHEMA);
            this.addAction(actions.DELETE_SCHEMA);
        }
    }
}
