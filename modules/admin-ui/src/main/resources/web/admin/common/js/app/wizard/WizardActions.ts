module api.app.wizard {

    export interface WizardActions<T> {

        enableActionsForNew();

        enableActionsForExisting(existing:T);

        getActions(): api.ui.Action[];
    }
}