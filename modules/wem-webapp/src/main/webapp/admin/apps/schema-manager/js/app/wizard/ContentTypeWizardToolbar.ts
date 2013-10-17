module app_wizard {

    export interface ContentTypeWizardToolbarParams {
        saveAction: api_ui.Action;
        duplicateAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class ContentTypeWizardToolbar extends api_ui_toolbar.Toolbar {

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