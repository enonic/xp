module app_wizard {

    export class DuplicateContentAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteContentAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class ShowLiveFormAction extends api_ui.Action {

        constructor() {
            super("LIVE");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowContentLiveEvent().fire();
            });
        }
    }

    export class ShowFormAction extends api_ui.Action {

        constructor() {
            super("FORM");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowContentFormEvent().fire();
            })
        }
    }

}
