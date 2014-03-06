module app.wizard {

    export interface ContentWizardToolbarParams {
        saveAction:api.ui.Action;
        duplicateAction:api.ui.Action;
        deleteAction:api.ui.Action;
        closeAction:api.ui.Action;
        publishAction:api.ui.Action;
        previewAction:api.ui.Action;
        showLiveEditAction:api.ui.Action;
        showFormAction:api.ui.Action;
    }

    export class ContentWizardToolbar extends api.ui.toolbar.Toolbar {

        constructor(params:ContentWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addAction(params.publishAction);
            super.addAction(params.previewAction);
            super.addAction(params.closeAction);
            super.addGreedySpacer();

            var liveFormToggler = new api.ui.ToggleSlide({
                turnOnAction: params.showLiveEditAction,
                turnOffAction: params.showFormAction
            }, false);
            liveFormToggler.setEnabled(params.previewAction.isEnabled());
            params.previewAction.addPropertyChangeListener((action:api.ui.Action) => {
                liveFormToggler.setEnabled(action.isEnabled());
            });

            super.addElement(liveFormToggler);
            var contextWindowToggler = new ContextWindowToggler();
            super.addElement(contextWindowToggler);

        }
    }

    export class ContextWindowToggler extends api.ui.Button {
        constructor() {
            super("Toggle Content Window");
            this.addClass()
            this.getEl().addEventListener('click', () => {
                new ToggleContextWindowEvent().fire();
            });
        }
    }
}
