module app_wizard_action {

    export class PublishAction extends api_ui.Action {

        constructor() {
            super("Publish");
            this.addExecutionListener(() => {
                console.log("Publish action");
            });
        }
    }

}
