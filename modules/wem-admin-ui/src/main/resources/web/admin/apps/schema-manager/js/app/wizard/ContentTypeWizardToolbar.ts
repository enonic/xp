module app.wizard {

    export interface ContentTypeWizardToolbarParams {
        saveAction: api.ui.Action;
        duplicateAction: api.ui.Action;
        deleteAction: api.ui.Action;
        closeAction: api.ui.Action;
    }

    export class ContentTypeWizardToolbar extends api.ui.toolbar.Toolbar {

        constructor(params: ContentTypeWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction);
        }
    }
}