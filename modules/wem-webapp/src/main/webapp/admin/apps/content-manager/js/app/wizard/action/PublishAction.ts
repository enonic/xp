module app.wizard.action {

    export class PublishAction extends api.ui.Action {

        constructor() {
            super("Publish");
            this.addExecutionListener(() => {
                console.log("Publish action");
            });
        }
    }

}
