module app.browse.action {

    export class SaveSortedContentAction extends api.ui.Action {

        constructor(dialog: SortContentDialog) {
            super("Save");
            this.setEnabled(true);

            this.onExecuted(() => {
                new SaveSortedContentEvent(dialog.getContent().getContentSummary()).fire();
            });
        }
    }
}