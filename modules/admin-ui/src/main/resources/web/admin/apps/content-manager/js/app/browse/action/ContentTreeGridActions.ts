module app.browse.action {

    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentSummary = api.content.ContentSummary;

    export class ContentTreeGridActions implements TreeGridActions<ContentSummary> {

        public SHOW_NEW_CONTENT_DIALOG_ACTION: Action;
        public PREVIEW_CONTENT: Action;
        public EDIT_CONTENT: Action;
        public DELETE_CONTENT: Action;
        public DUPLICATE_CONTENT: Action;
        public MOVE_CONTENT: Action;
        public SORT_CONTENT: Action;
        public PUBLISH_CONTENT: Action;
        public TOGGLE_SEARCH_PANEL: Action;

        private actions: api.ui.Action[] = [];

        constructor(grid: ContentTreeGrid) {
            this.TOGGLE_SEARCH_PANEL = new ToggleSearchPanelAction();

            this.SHOW_NEW_CONTENT_DIALOG_ACTION = new ShowNewContentDialogAction(grid);
            this.PREVIEW_CONTENT = new PreviewContentAction(grid);
            this.EDIT_CONTENT = new EditContentAction(grid);
            this.DELETE_CONTENT = new DeleteContentAction(grid);
            this.DUPLICATE_CONTENT = new DuplicateContentAction(grid);
            this.MOVE_CONTENT = new MoveContentAction(grid);
            this.SORT_CONTENT = new SortContentAction(grid);
            this.PUBLISH_CONTENT = new PublishContentAction(grid);

            this.actions.push(
                this.SHOW_NEW_CONTENT_DIALOG_ACTION,
                this.PREVIEW_CONTENT, this.EDIT_CONTENT,
                this.DELETE_CONTENT, this.DUPLICATE_CONTENT,
                this.MOVE_CONTENT, this.SORT_CONTENT,
                this.PUBLISH_CONTENT
            );

        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }

        updateActionsEnabledState(contentBrowseItems: BrowseItem<ContentSummary>[]): wemQ.Promise<BrowseItem<ContentSummary>[]> {
            this.TOGGLE_SEARCH_PANEL.setVisible(false);

            var contentSummaries: ContentSummary[] = contentBrowseItems.map((elem: BrowseItem<ContentSummary>) => {
                return elem.getModel();
            });

            var deferred = wemQ.defer<BrowseItem<ContentSummary>[]>();

            switch (contentBrowseItems.length) {
            case 0:
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(false);
                this.DELETE_CONTENT.setEnabled(false);
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(false);
                this.SORT_CONTENT.setEnabled(false);
                this.PREVIEW_CONTENT.setEnabled(false);
                this.PUBLISH_CONTENT.setEnabled(false);
                deferred.resolve(contentBrowseItems);
                break;
            case 1:
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(!!contentSummaries[0]);
                this.EDIT_CONTENT.setEnabled(!contentSummaries[0] ? false : contentSummaries[0].isEditable());
                this.DELETE_CONTENT.setEnabled(!contentSummaries[0] ? false : contentSummaries[0].isDeletable());
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
                this.SORT_CONTENT.setEnabled(true);
                this.PUBLISH_CONTENT.setEnabled(true);
                new api.content.page.IsRenderableRequest(contentSummaries[0].getContentId()).sendAndParse().
                    then((renderable: boolean) => {
                        this.PREVIEW_CONTENT.setEnabled(renderable);
                        contentBrowseItems[0].setRenderable(renderable);
                        deferred.resolve(contentBrowseItems);
                    });
                break;
            default:
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.PREVIEW_CONTENT.setEnabled(false);
                this.EDIT_CONTENT.setEnabled(this.anyEditable(contentSummaries));
                this.DELETE_CONTENT.setEnabled(this.anyDeletable(contentSummaries));
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(false);
                this.SORT_CONTENT.setEnabled(false);
                this.PUBLISH_CONTENT.setEnabled(true);
                deferred.resolve(contentBrowseItems);
            }
            return deferred.promise;
        }

        private anyEditable(contentSummaries: api.content.ContentSummary[]): boolean {
            for (var i = 0; i < contentSummaries.length; i++) {
                var content: api.content.ContentSummary = contentSummaries[i];
                if (!!content && content.isEditable()) {
                    return true;
                }
            }
            return false;
        }

        private anyDeletable(contentSummaries: api.content.ContentSummary[]): boolean {
            for (var i = 0; i < contentSummaries.length; i++) {
                var content: api.content.ContentSummary = contentSummaries[i];
                if (!!content && content.isDeletable()) {
                    return true;
                }
            }
            return false;
        }
    }
}
