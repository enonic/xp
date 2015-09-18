module app.wizard {

    export interface PrincipalWizardToolbarParams {
        saveAction:api.ui.Action;
        duplicateAction:api.ui.Action;
        deleteAction:api.ui.Action;
    }

    export class PrincipalWizardToolbar extends api.ui.toolbar.Toolbar {

        constructor(params: PrincipalWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
        }
    }
}
