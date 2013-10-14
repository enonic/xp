module app_browse {

    export class BaseContentBrowseAction extends api_ui.Action {

        constructor(label:string, shortcut?:string) {
            super(label, shortcut);
        }

        extModelsToContentSummaries(models:Ext_data_Model[]):api_content.ContentSummary[] {
            var summaries:api_content.ContentSummary[] = [];
            for (var i = 0; i < models.length; i++) {
                summaries.push(new api_content.ContentSummary(<api_content_json.ContentSummaryJson>models[i].data))
            }
            return summaries;
        }
    }


    export class ShowNewContentDialogAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("New", "mod+alt+n");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ShowNewContentDialogEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())[0]).fire();
            });
        }
    }

    export class OpenContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Open", "mod+o");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new OpenContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class EditContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Edit", "f4");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new EditContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class DeleteContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ContentDeletePromptEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class DuplicateContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Duplicate");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new DuplicateContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class MoveContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Move");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new MoveContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ShowPreviewAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("PREVIEW");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowPreviewEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ShowDetailsAction extends BaseContentBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("DETAILS");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowDetailsEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            })
        }
    }

    export class BrowseContentSettingsAction extends api_ui.Action {

        constructor() {
            super("");
            this.setEnabled(true);
            this.setIconClass('icon-toolbar-settings');
            this.addExecutionListener(() => {
                console.log('TODO: browse content settings');
            });
        }
    }

    export class ShowNewGridAction extends api_ui.Action {

        constructor() {
            super("NG", "mod+i");
            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowNewContentGridEvent().fire();
            });
        }
    }

    export class ContentBrowseActions {

        public SHOW_NEW_CONTENT_DIALOG_ACTION:api_ui.Action;
        public OPEN_CONTENT:api_ui.Action;
        public EDIT_CONTENT:api_ui.Action;
        public DELETE_CONTENT:api_ui.Action;
        public DUPLICATE_CONTENT:api_ui.Action;
        public MOVE_CONTENT:api_ui.Action;
        public SHOW_PREVIEW:api_ui.Action;
        public SHOW_DETAILS:api_ui.Action;
        public BROWSE_CONTENT_SETTINGS:api_ui.Action;
        public SHOW_NEW_CONTENT_GRID:api_ui.Action;

        private allActions:api_ui.Action[] = [];

        private static INSTANCE:ContentBrowseActions;

        static init(treeGridPanel:api_app_browse_grid.TreeGridPanel):ContentBrowseActions {
            new ContentBrowseActions(treeGridPanel);
            return ContentBrowseActions.INSTANCE;
        }

        static get():ContentBrowseActions {
            return ContentBrowseActions.INSTANCE;
        }

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {

            this.SHOW_NEW_CONTENT_DIALOG_ACTION = new app_browse.ShowNewContentDialogAction(treeGridPanel);
            this.OPEN_CONTENT = new OpenContentAction(treeGridPanel);
            this.EDIT_CONTENT = new EditContentAction(treeGridPanel);
            this.DELETE_CONTENT = new DeleteContentAction(treeGridPanel);
            this.DUPLICATE_CONTENT = new DuplicateContentAction(treeGridPanel);
            this.MOVE_CONTENT = new MoveContentAction(treeGridPanel);
            this.SHOW_PREVIEW = new ShowPreviewAction(treeGridPanel);
            this.SHOW_DETAILS = new ShowDetailsAction(treeGridPanel);
            this.BROWSE_CONTENT_SETTINGS = new BrowseContentSettingsAction();
            this.SHOW_NEW_CONTENT_GRID = new ShowNewGridAction();

            this.allActions.push(this.SHOW_NEW_CONTENT_DIALOG_ACTION, this.OPEN_CONTENT, this.EDIT_CONTENT, this.DELETE_CONTENT,
                this.DUPLICATE_CONTENT, this.MOVE_CONTENT, this.SHOW_PREVIEW, this.SHOW_DETAILS, this.BROWSE_CONTENT_SETTINGS,
                this.SHOW_NEW_CONTENT_GRID);

            ContentBrowseActions.INSTANCE = this;
        }

        getAllActions():api_ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(models:api_content.ContentSummary[]) {

            if (models.length <= 0) {
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.OPEN_CONTENT.setEnabled(false);
                this.EDIT_CONTENT.setEnabled(false);
                this.DELETE_CONTENT.setEnabled(false);
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(false);
            }
            else if (models.length == 1) {
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                this.OPEN_CONTENT.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(models[0].isEditable());
                this.DELETE_CONTENT.setEnabled(models[0].isDeletable());
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
            }
            else {
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.OPEN_CONTENT.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(this.anyEditable(models));
                this.DELETE_CONTENT.setEnabled(this.anyDeletable(models));
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
            }

        }

        private anyEditable(contents:api_content.ContentSummary[]):boolean {
            for (var i in contents) {
                var content:api_content.ContentSummary = contents[i];
                if (content.isEditable()) {
                    return true;
                }
            }
            return false;
        }

        private anyDeletable(contents:api_content.ContentSummary[]):boolean {
            for (var i in contents) {
                var content:api_content.ContentSummary = contents[i];
                if (content.isDeletable()) {
                    return true;
                }
            }
            return false;
        }
    }
}
