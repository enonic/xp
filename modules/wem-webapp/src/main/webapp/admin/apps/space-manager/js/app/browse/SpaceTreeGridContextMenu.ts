module app_browse {

    export class SpaceTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions:SpaceBrowseActions) {
            this.addAction(actions.NEW_SPACE);
            this.addAction(actions.EDIT_SPACE);
            this.addAction(actions.OPEN_SPACE);
            this.addAction(actions.DELETE_SPACE);
        }

    }
}
