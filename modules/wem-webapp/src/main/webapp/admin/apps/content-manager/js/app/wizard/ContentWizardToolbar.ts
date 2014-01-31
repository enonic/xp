module app.wizard {

    export interface ContentWizardToolbarParams {
        saveAction:api.ui.Action;
        duplicateAction:api.ui.Action;
        deleteAction:api.ui.Action;
        closeAction:api.ui.Action;
        publishAction:api.ui.Action;
        previewAction:api.ui.Action;
    }

    export class ContentWizardToolbar extends api.ui.toolbar.Toolbar {

        constructor(params:ContentWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addAction(params.publishAction);
            super.addAction(params.previewAction);
            super.addGreedySpacer();
            var displayModeToggle = new api.ui.ToggleSlide({
                turnOnAction: new app.wizard.action.ShowLiveFormAction(),
                turnOffAction: new app.wizard.action.ShowFormAction()
            }, false);
            displayModeToggle.setEnabled(params.previewAction.isEnabled());
            params.previewAction.addPropertyChangeListener((action:api.ui.Action) => {
                displayModeToggle.setEnabled(action.isEnabled());
            })
            super.addElement(displayModeToggle);
            super.addAction(params.closeAction);

        }
    }
}
