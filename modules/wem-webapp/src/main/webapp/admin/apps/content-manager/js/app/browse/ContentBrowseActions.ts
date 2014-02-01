module app.browse {

    export class BaseContentBrowseAction extends api.ui.Action {

        constructor(label: string, shortcut?: string) {
            super(label, shortcut);
        }

        extModelsToContentSummaries(models: Ext_data_Model[]): api.content.ContentSummary[] {
            var summaries: api.content.ContentSummary[] = [];
            for (var i = 0; i < models.length; i++) {
                summaries.push(new api.content.ContentSummary(<api.content.json.ContentSummaryJson>models[i].data))
            }
            return summaries;
        }
    }


    export class ShowNewContentDialogAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("New", "mod+alt+n");
            this.setEnabled(true);
            this.addExecutionListener(() => {
                var extModelsToContentSummaries: api.content.ContentSummary[] = this.extModelsToContentSummaries(treeGridPanel.getSelection());
                new ShowNewContentDialogEvent(extModelsToContentSummaries.length > 0 ? extModelsToContentSummaries[0] : null).fire();
            });
        }
    }

    export class OpenContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Open", "mod+o");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ViewContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class EditContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Edit", "f4");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                var content = this.extModelsToContentSummaries(treeGridPanel.getSelection());
                new EditContentEvent(content).fire();
            });
        }
    }

    export class DeleteContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ContentDeletePromptEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class DuplicateContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Duplicate");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new DuplicateContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class MoveContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Move");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new MoveContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ShowPreviewAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("PREVIEW");

            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ShowPreviewEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ShowDetailsAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("DETAILS");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowDetailsEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            })
        }
    }

    export class ShowNewGridAction extends api.ui.Action {

        constructor() {
            super("NG", "mod+i");
            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowNewContentGridEvent().fire();
            });
        }
    }

    export class ContentBrowseActions {

        public SHOW_NEW_CONTENT_DIALOG_ACTION: api.ui.Action;
        public OPEN_CONTENT: api.ui.Action;
        public EDIT_CONTENT: api.ui.Action;
        public DELETE_CONTENT: api.ui.Action;
        public DUPLICATE_CONTENT: api.ui.Action;
        public MOVE_CONTENT: api.ui.Action;
        public SHOW_PREVIEW: api.ui.Action;
        public SHOW_DETAILS: api.ui.Action;
        public SHOW_NEW_CONTENT_GRID: api.ui.Action;

        private allActions: api.ui.Action[] = [];

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

            this.allActions.push(this.SHOW_NEW_CONTENT_DIALOG_ACTION, this.OPEN_CONTENT, this.EDIT_CONTENT, this.DELETE_CONTENT,
                this.DUPLICATE_CONTENT, this.MOVE_CONTENT, this.SHOW_PREVIEW, this.SHOW_DETAILS,
                this.SHOW_NEW_CONTENT_GRID);

            ContentBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(models: api.content.ContentSummary[]) {

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
