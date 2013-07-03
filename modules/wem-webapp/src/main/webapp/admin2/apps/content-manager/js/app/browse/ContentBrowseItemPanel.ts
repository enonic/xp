module app_browse {

    export class ContentBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        constructor() {
            super( {actionMenuActions: [ContentBrowseActions.NEW_CONTENT, ContentBrowseActions.EDIT_CONTENT,
                ContentBrowseActions.OPEN_CONTENT, ContentBrowseActions.DELETE_CONTENT,
                ContentBrowseActions.DUPLICATE_CONTENT, ContentBrowseActions.MOVE_CONTENT]});
        }

        fireGridDeselectEvent(model:api_model.ContentModel) {
            var models:api_model.ContentModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
