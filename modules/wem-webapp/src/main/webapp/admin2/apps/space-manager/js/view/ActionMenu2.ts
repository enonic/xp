module app_ui {

    export class ActionMenu2 extends api_ui_menu.ActionMenu {

        constructor() {
            super(
                app.SpaceActions.OPEN_SPACE,
                app.SpaceActions.EDIT_SPACE
            );
        }
    }
}
