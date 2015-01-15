module app.wizard {

    export interface UserStoreWizardToolbarParams {
        saveAction:api.ui.Action;
        deleteAction:api.ui.Action;
    }

    export class UserStoreWizardToolbar extends api.ui.toolbar.Toolbar {

        constructor(params: UserStoreWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.deleteAction);
        }
    }
}
