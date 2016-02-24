module app.wizard {

    export interface PathGuardWizardToolbarParams {
        saveAction:api.ui.Action;
        deleteAction:api.ui.Action;
    }

    export class PathGuardWizardToolbar extends api.ui.toolbar.Toolbar {

        constructor(params: PathGuardWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.deleteAction);
        }
    }
}
