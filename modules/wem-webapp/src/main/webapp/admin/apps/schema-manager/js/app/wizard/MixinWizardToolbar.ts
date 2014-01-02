module app.wizard {

    export interface MixinWizardToolbarParams {
        saveAction: api.ui.Action;
        duplicateAction: api.ui.Action;
        deleteAction: api.ui.Action;
        closeAction: api.ui.Action;
    }

    export class MixinWizardToolbar extends api.ui.toolbar.Toolbar {

        constructor(params: MixinWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction);
        }
    }
}