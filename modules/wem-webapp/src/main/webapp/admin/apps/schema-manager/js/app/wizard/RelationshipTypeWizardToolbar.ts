module app.wizard {

    export interface RelationshipTypeWizardToolbarParams {
        saveAction: api.ui.Action;
        duplicateAction: api.ui.Action;
        deleteAction: api.ui.Action;
        closeAction: api.ui.Action;
    }

    export class RelationshipTypeWizardToolbar extends api.ui.toolbar.Toolbar {

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