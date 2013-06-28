module app_browse {

    export class ContentActionMenu extends api_ui_menu.ActionMenu {

        constructor() {
            super(
                ContentBrowseActions.NEW_CONTENT,
                ContentBrowseActions.EDIT_CONTENT,
                ContentBrowseActions.OPEN_CONTENT,
                ContentBrowseActions.DELETE_CONTENT,
                ContentBrowseActions.DUPLICATE_CONTENT,
                ContentBrowseActions.MOVE_CONTENT
            );
        }
    }

}