module app.browse {

    export class SchemaTreeGridContextMenu extends api.ui.menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions:app.browse.action.SchemaBrowseActions) {
            this.addAction(actions.NEW_SCHEMA);
            this.addAction(actions.EDIT_SCHEMA);
            this.addAction(actions.OPEN_SCHEMA);
            this.addAction(actions.DELETE_SCHEMA);
        }
    }
}
