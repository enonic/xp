module app_ui {

    export class ActionMenu extends api_ui_menu.ActionMenu {

        constructor() {
            super(
                app.ContentActions.NEW_CONTENT,
                app.ContentActions.EDIT_CONTENT,
                app.ContentActions.OPEN_CONTENT,
                app.ContentActions.DELETE_CONTENT,
                app.ContentActions.DUPLICATE_CONTENT,
                app.ContentActions.MOVE_CONTENT
            );
        }
    }

}