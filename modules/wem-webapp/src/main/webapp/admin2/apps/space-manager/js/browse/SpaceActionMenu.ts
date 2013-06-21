module app_browse {

    export class SpaceActionMenu extends api_ui_menu.ActionMenu {

        constructor() {
            super(
                app.SpaceActions.OPEN_SPACE,
                app.SpaceActions.EDIT_SPACE
            );
        }
    }
}
