module app.browse.action {
    export class DuplicateTemplateAction extends api.ui.Action {

        constructor() {
            super("Duplicate");
            this.setEnabled(false);
            this.onExecuted(() => {
                console.log("duplicate template action");
            });
        }

    }
}