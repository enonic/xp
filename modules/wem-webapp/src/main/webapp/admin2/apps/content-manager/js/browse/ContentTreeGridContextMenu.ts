module app_browse {

    export class ContentTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super(
                app_browse.ContentBrowseActions.NEW_CONTENT,
                app_browse.ContentBrowseActions.EDIT_CONTENT,
                app_browse.ContentBrowseActions.OPEN_CONTENT,
                app_browse.ContentBrowseActions.DELETE_CONTENT,
                app_browse.ContentBrowseActions.DUPLICATE_CONTENT,
                app_browse.ContentBrowseActions.MOVE_CONTENT
            );
        }
    }

}

