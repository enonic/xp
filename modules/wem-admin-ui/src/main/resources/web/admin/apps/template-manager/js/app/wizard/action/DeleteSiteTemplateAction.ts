module app.wizard.action {

    export class DeleteSiteTemplateAction extends api.ui.Action {

        constructor(wizardPanel:api.app.wizard.WizardPanel<any>) {
            super("Delete", "mod+del");

        }
    }
}