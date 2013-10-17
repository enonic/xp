module app_wizard {

    export interface RelationshipTypeWizardToolbarParams {
        saveAction: api_ui.Action;
        duplicateAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class RelationshipTypeWizardToolbar extends api_ui_toolbar.Toolbar {

        constructor(params: RelationshipTypeWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction);
        }
    }
}