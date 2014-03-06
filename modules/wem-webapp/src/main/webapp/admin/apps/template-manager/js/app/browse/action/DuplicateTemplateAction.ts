module app.browse.action {
    export class DuplicateTemplateAction extends api.ui.Action {

        constructor() {
            super("Duplicate");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                console.log("duplicate template action");
            });
        }

    }
}