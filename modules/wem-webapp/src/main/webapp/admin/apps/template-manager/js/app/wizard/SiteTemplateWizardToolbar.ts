module app_wizard {

    export interface ContentWizardToolbarParams {
        saveAction:api_ui.Action;
        duplicateAction:api_ui.Action;
        moveAction: api_ui.Action;
        deleteAction:api_ui.Action;
        closeAction:api_ui.Action;
    }

    export class SiteTemplateWizardToolbar extends api_ui_toolbar.Toolbar {

        constructor(params:ContentWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addAction(params.moveAction);
            super.addGreedySpacer();

            super.addAction(params.closeAction);

        }
    }
}
