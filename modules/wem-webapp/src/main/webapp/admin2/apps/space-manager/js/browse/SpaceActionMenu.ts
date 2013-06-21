module app_browse {

    export class SpaceActionMenu extends api_ui_menu.ActionMenu {

        constructor() {
            super(
                SpaceBrowseActions.OPEN_SPACE,
                SpaceBrowseActions.EDIT_SPACE
            );
        }
    }
}
