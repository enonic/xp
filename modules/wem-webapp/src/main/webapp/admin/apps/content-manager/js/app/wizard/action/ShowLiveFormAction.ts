module app_wizard_action {

    export class ShowLiveFormAction extends api_ui.Action {

        constructor() {
            super("LIVE");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new app_wizard_event.ShowContentLiveEvent().fire();
            });
        }
    }

}
