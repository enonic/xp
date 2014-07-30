module app.browse {
    
    import Action = api.ui.Action;

    export class ContentBrowseActions {

        public SHOW_NEW_CONTENT_DIALOG_ACTION: Action;
        public OPEN_CONTENT: Action;
        public EDIT_CONTENT: Action;
        public DELETE_CONTENT: Action;
        public DUPLICATE_CONTENT: Action;
        public MOVE_CONTENT: Action;
        public SHOW_PREVIEW: Action;
        public SHOW_DETAILS: Action;
        public SHOW_NEW_CONTENT_GRID: Action;
        public TOGGLE_SEARCH_PANEL: Action;

        private allActions: Action[] = [];

        private static INSTANCE: ContentBrowseActions;

        static init(treeGridPanel: api.app.browse.grid.TreeGridPanel): ContentBrowseActions {
            new ContentBrowseActions(treeGridPanel);
            return ContentBrowseActions.INSTANCE;
        }

        static get(): ContentBrowseActions {
            return ContentBrowseActions.INSTANCE;
        }

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {

            this.SHOW_NEW_CONTENT_DIALOG_ACTION = new app.browse.ShowNewContentDialogAction(treeGridPanel);
            this.OPEN_CONTENT = new OpenContentAction(treeGridPanel);
            this.EDIT_CONTENT = new EditContentAction(treeGridPanel);
            this.DELETE_CONTENT = new DeleteContentAction(treeGridPanel);
            this.DUPLICATE_CONTENT = new DuplicateContentAction(treeGridPanel);
            this.MOVE_CONTENT = new MoveContentAction(treeGridPanel);
            this.SHOW_PREVIEW = new ShowPreviewAction(treeGridPanel);
            this.SHOW_DETAILS = new ShowDetailsAction(treeGridPanel);
            this.SHOW_NEW_CONTENT_GRID = new ShowNewGridAction();
            this.TOGGLE_SEARCH_PANEL = new ToggleSearchPanelAction();

            this.allActions.push(this.SHOW_NEW_CONTENT_DIALOG_ACTION, this.OPEN_CONTENT, this.EDIT_CONTENT, this.DELETE_CONTENT,
                this.DUPLICATE_CONTENT, this.MOVE_CONTENT, this.SHOW_PREVIEW, this.SHOW_DETAILS,
                this.SHOW_NEW_CONTENT_GRID, this.TOGGLE_SEARCH_PANEL);

            ContentBrowseActions.INSTANCE = this;
        }

        getAllActions(): Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(models: api.content.ContentSummary[]) {
            this.TOGGLE_SEARCH_PANEL.setVisible(false);

            if (models.length <= 0) {
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                this.OPEN_CONTENT.setEnabled(false);
                this.EDIT_CONTENT.setEnabled(false);
                this.DELETE_CONTENT.setEnabled(false);
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(false);
                this.SHOW_PREVIEW.setEnabled(false);
            }
            else if (models.length == 1) {
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                this.OPEN_CONTENT.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(models[0].isEditable());
                this.DELETE_CONTENT.setEnabled(models[0].isDeletable());
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
                this.SHOW_PREVIEW.setEnabled(models[0].isPage());
            }
            else {
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.OPEN_CONTENT.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(this.anyEditable(models));
                this.DELETE_CONTENT.setEnabled(this.anyDeletable(models));
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
                this.SHOW_PREVIEW.setEnabled(false);
            }

        }

        private anyEditable(contents: api.content.ContentSummary[]): boolean {
            for (var i in contents) {
                var content: api.content.ContentSummary = contents[i];
                if (content.isEditable()) {
                    return true;
                }
            }
            return false;
        }

        private anyDeletable(contents: api.content.ContentSummary[]): boolean {
            for (var i in contents) {
                var content: api.content.ContentSummary = contents[i];
                if (content.isDeletable()) {
                    return true;
                }
            }
            return false;
        }
    }
}
