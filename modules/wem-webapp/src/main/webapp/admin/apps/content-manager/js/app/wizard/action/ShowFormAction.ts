module app.wizard.action {

    export class ShowFormAction extends api.ui.Action {

        constructor() {
            super("FORM");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new app.wizard.event.ShowContentFormEvent().fire();
            })
        }
    }

}
