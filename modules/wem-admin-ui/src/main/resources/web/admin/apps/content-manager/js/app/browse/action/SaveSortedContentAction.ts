module app.browse.action {

    export class SaveSortedContentAction extends api.ui.Action {

        constructor() {
            super("Save");
            this.setEnabled(false);

            this.onExecuted(() => {

            });
        }
    }
}