module app.wizard.action {

    export class MoveSiteTemplateAction extends api.ui.Action {

        constructor() {
            super("Move");

            this.setEnabled(true);
        }
    }
}