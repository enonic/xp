module app_browse {

    export class SpaceTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super();
            super.addAction(SpaceBrowseActions.EDIT_SPACE);
            super.addAction(SpaceBrowseActions.OPEN_SPACE);
            super.addAction(SpaceBrowseActions.DELETE_SPACE);
        }

    }
}
