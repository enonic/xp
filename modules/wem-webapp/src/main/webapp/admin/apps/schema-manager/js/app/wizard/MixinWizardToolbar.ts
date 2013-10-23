module app_wizard {

    export interface MixinWizardToolbarParams {
        saveAction: api_ui.Action;
        duplicateAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class MixinWizardToolbar extends api_ui_toolbar.Toolbar {

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