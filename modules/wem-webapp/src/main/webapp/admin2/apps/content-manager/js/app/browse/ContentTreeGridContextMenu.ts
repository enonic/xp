module app_browse {

    export class ContentTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super(
                ContentBrowseActions.SHOW_NEW_CONTENT_DIALOG_ACTION,
                ContentBrowseActions.EDIT_CONTENT,
                ContentBrowseActions.OPEN_CONTENT,
                ContentBrowseActions.DELETE_CONTENT,
                ContentBrowseActions.DUPLICATE_CONTENT,
                ContentBrowseActions.MOVE_CONTENT
            );
        }
    }

}

