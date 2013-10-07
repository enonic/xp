module app_wizard {

    export interface RelationshipTypeWizardToolbarParams {
        saveAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class RelationshipTypeWizardToolbar extends api_ui_toolbar.Toolbar {

        constructor(params: RelationshipTypeWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction);
        }
    }
}