module app_browse {

    export class ShowNewContentDialogAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("New");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ShowNewContentDialogEvent(treeGridPanel.getSelection()[0]).fire();
            });
        }
    }

    export class OpenContentAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new OpenContentEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class EditContentAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new EditContentEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class DeleteContentAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ContentDeletePromptEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class DuplicateContentAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Duplicate");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new DuplicateContentEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class MoveContentAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Move");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new MoveContentEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class ShowPreviewAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("PREVIEW");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowPreviewEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class ShowDetailsAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("DETAILS");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowDetailsEvent(treeGridPanel.getSelection()).fire();
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

        private allActions:api_ui.Action[] = [];

        private static INSTANCE:ContentBrowseActions;

        static init(treeGridPanel:api_app_browse_grid.TreeGridPanel):ContentBrowseActions {
            new ContentBrowseActions(treeGridPanel);
            return INSTANCE;
        }

        static get():ContentBrowseActions {
            return INSTANCE;
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

            this.allActions.push(this.SHOW_NEW_CONTENT_DIALOG_ACTION, this.OPEN_CONTENT, this.EDIT_CONTENT, this.DELETE_CONTENT,
                this.DUPLICATE_CONTENT, this.MOVE_CONTENT, this.SHOW_PREVIEW, this.SHOW_DETAILS, this.BROWSE_CONTENT_SETTINGS);

            ContentBrowseActions.INSTANCE = this;
        }

        getAllActions():api_ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(models:api_model.ContentExtModel[]) {

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
                this.EDIT_CONTENT.setEnabled(models[0].data.editable);
                this.DELETE_CONTENT.setEnabled(models[0].data.deletable);
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
            }
            else {
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.OPEN_CONTENT.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(this.anyEditable(models));
                this.DELETE_CONTENT.setEnabled(this.anyDeleteable(models));
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
            }

        }

        private anyEditable(contents:api_model.ContentExtModel[]):bool {
            for (var i in contents) {
                var content:api_model.ContentExtModel = contents[i];
                if (content.data.editable) {
                    return true;
                }
            }
            return false;
        }

        private anyDeleteable(contents:api_model.ContentExtModel[]):bool {
            for (var i in contents) {
                var content:api_model.ContentExtModel = contents[i];
                if (content.data.deletable) {
                    return true;
                }
            }
            return false;
        }
    }
}
