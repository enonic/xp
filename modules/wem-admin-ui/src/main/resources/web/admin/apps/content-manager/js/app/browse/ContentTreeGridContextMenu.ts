module app.browse {

    import ContentTreeGridActions = app.browse.action.ContentTreeGridActions;

    export class ContentTreeGridContextMenu extends api.ui.menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions: ContentTreeGridActions) {
            this.addAction(actions.SHOW_NEW_CONTENT_DIALOG_ACTION);
            this.addAction(actions.EDIT_CONTENT);
            this.addAction(actions.OPEN_CONTENT);
            this.addAction(actions.DELETE_CONTENT);
            this.addAction(actions.DUPLICATE_CONTENT);
            this.addAction(actions.MOVE_CONTENT);
        }
    }

}

