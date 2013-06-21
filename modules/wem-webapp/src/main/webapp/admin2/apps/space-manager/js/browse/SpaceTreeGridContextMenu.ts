module app_browse {

    export class SpaceTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super();
            super.addAction(app_browse.SpaceBrowseActions.EDIT_SPACE);
            super.addAction(app_browse.SpaceBrowseActions.OPEN_SPACE);
            super.addAction(app_browse.SpaceBrowseActions.DELETE_SPACE);
        }

    }
}
