module app.browse {

    export class ContentBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: ContentBrowseActions) {
            super();

            this.addAction(actions.SHOW_NEW_CONTENT_DIALOG_ACTION);
            this.addAction(actions.EDIT_CONTENT);
            this.addAction(actions.OPEN_CONTENT);
            this.addAction(actions.DELETE_CONTENT);
            this.addAction(actions.DUPLICATE_CONTENT);
            this.addAction(actions.MOVE_CONTENT);
        }
    }
}
