module app.browse.action {
    export class OpenTemplateAction extends api.ui.Action {

        constructor() {
            super("Open");
            this.setEnabled(false);
            this.onExecuted(() => {
                console.log("open template action");
            });
        }

    }
}