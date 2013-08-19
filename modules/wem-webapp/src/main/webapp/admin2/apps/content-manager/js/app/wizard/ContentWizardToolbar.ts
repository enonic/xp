module app_wizard {

    export interface ContentWizardToolbarParams {
        saveAction:api_ui.Action;
        duplicateAction:api_ui.Action;
        deleteAction:api_ui.Action;
        closeAction:api_ui.Action;
    }

    export class ContentWizardToolbar extends api_ui_toolbar.Toolbar {

        constructor(params:ContentWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            var displayModeToggle = new api_ui.ToggleSlide({
                turnOnAction: new app_wizard.ShowLiveFormAction(),
                turnOffAction: new app_wizard.ShowFormAction()
            }, false);
            super.addElement(displayModeToggle);
            super.addAction(params.closeAction);

        }
    }
}
