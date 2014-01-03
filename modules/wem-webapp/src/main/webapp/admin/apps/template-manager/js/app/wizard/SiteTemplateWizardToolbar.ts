module app.wizard {

    export interface ContentWizardToolbarParams {
        saveAction:api.ui.Action;
        duplicateAction:api.ui.Action;
        moveAction: api.ui.Action;
        deleteAction:api.ui.Action;
        closeAction:api.ui.Action;
    }

    export class SiteTemplateWizardToolbar extends api.ui.toolbar.Toolbar {

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
