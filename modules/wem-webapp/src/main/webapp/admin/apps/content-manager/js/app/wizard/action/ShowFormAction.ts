module app_wizard_action {

    export class ShowFormAction extends api_ui.Action {

        constructor() {
            super("FORM");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new app_wizard_event.ShowContentFormEvent().fire();
            })
        }
    }

}
