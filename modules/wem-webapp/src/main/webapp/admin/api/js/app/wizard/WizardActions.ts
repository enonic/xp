module api_app_wizard {

    export interface WizardActions<T> {

        enableActionsForNew();

        enableActionsForExisting(existing:T);
    }
}