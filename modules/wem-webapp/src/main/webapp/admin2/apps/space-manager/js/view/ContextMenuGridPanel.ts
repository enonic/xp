module app_ui {

    export class ContextMenuGridPanel extends API_ui_menu.ContextMenu {

        constructor() {
            super();
            super.addAction(app.SpaceActions.EDIT_SPACE);
            super.addAction(app.SpaceActions.OPEN_SPACE);
            super.addAction(app.SpaceActions.DELETE_SPACE);
        }

    }
}
