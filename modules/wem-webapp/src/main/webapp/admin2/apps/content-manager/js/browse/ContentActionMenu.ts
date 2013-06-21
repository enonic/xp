module app_browse {

    export class ContentActionMenu extends api_ui_menu.ActionMenu {

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