module app.wizard.action {

    export class ShowLiveFormAction extends api.ui.Action {

        constructor() {
            super("LIVE");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new app.wizard.event.ShowContentLiveEvent().fire();
            });
        }
    }

}
