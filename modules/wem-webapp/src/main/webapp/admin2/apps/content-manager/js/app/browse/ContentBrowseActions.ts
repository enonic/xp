module app_browse {

    export class ShowNewContentDialogAction extends api_ui.Action {

        constructor() {
            super("New");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ShowNewContentDialogEvent(app.ContentContext.get().getSelectedContents()[0]).fire();
            });
        }
    }

    export class OpenContentAction extends api_ui.Action {

        constructor() {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new OpenContentEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }
    }

    export class EditContentAction extends api_ui.Action {

        constructor() {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new EditContentEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }
    }

    export class DeleteContentAction extends api_ui.Action {

        constructor() {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new ContentDeletePromptEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }
    }

    export class DuplicateContentAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new DuplicateContentEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }
    }

    export class MoveContentAction extends api_ui.Action {

        constructor() {
            super("Move");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new MoveContentEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }
    }

    export class ShowPreviewAction extends api_ui.Action {

        constructor() {
            super("PREVIEW");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowPreviewEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }
    }

    export class ShowDetailsAction extends api_ui.Action {

        constructor() {
            super("DETAILS");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowDetailsEvent(app.ContentContext.get().getSelectedContents()).fire();
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

        static SHOW_NEW_CONTENT_DIALOG_ACTION:api_ui.Action = new app_browse.ShowNewContentDialogAction();
        static OPEN_CONTENT:api_ui.Action = new OpenContentAction;
        static EDIT_CONTENT:api_ui.Action = new EditContentAction();
        static DELETE_CONTENT:api_ui.Action = new DeleteContentAction();
        static DUPLICATE_CONTENT:api_ui.Action = new DuplicateContentAction();
        static MOVE_CONTENT:api_ui.Action = new MoveContentAction();
        static SHOW_PREVIEW:api_ui.Action = new ShowPreviewAction();
        static SHOW_DETAILS:api_ui.Action = new ShowDetailsAction();
        static BROWSE_CONTENT_SETTINGS:api_ui.Action = new BrowseContentSettingsAction();

        static ACTIONS:api_ui.Action[] = [];

        static init() {

            ACTIONS.push(SHOW_NEW_CONTENT_DIALOG_ACTION, OPEN_CONTENT, EDIT_CONTENT, DELETE_CONTENT, DUPLICATE_CONTENT, MOVE_CONTENT);

            GridSelectionChangeEvent.on((event) => {

                var contents:api_model.ContentExtModel[] = event.getModels();

                if (contents.length <= 0) {
                    SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                    OPEN_CONTENT.setEnabled(false);
                    EDIT_CONTENT.setEnabled(false);
                    DELETE_CONTENT.setEnabled(false);
                    DUPLICATE_CONTENT.setEnabled(false);
                    MOVE_CONTENT.setEnabled(false);
                }
                else if (contents.length == 1) {
                    SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                    OPEN_CONTENT.setEnabled(true);
                    EDIT_CONTENT.setEnabled(contents[0].data.editable);
                    DELETE_CONTENT.setEnabled(contents[0].data.deletable);
                    DUPLICATE_CONTENT.setEnabled(true);
                    MOVE_CONTENT.setEnabled(true);
                }
                else {
                    SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                    OPEN_CONTENT.setEnabled(true);
                    EDIT_CONTENT.setEnabled(anyEditable(contents));
                    DELETE_CONTENT.setEnabled(anyDeleteable(contents));
                    DUPLICATE_CONTENT.setEnabled(true);
                    MOVE_CONTENT.setEnabled(true);
                }
            });
        }

        static anyEditable(contents:api_model.ContentExtModel[]):bool {
            for (var i in contents) {
                var content:api_model.ContentExtModel = contents[i];
                if (content.data.editable) {
                    return true;
                }
            }
            return false;
        }

        static anyDeleteable(contents:api_model.ContentExtModel[]):bool {
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
