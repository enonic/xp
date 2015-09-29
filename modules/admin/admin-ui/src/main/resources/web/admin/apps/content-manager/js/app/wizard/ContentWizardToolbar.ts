module app.wizard {

    import CycleButton = api.ui.button.CycleButton;
    import TogglerButton = api.ui.button.TogglerButton;

    export interface ContentWizardToolbarParams {
        saveAction:api.ui.Action;
        duplicateAction:api.ui.Action;
        deleteAction:api.ui.Action;
        publishAction:api.ui.Action;
        previewAction:api.ui.Action;
        showLiveEditAction:api.ui.Action;
        showFormAction:api.ui.Action;
        showSplitEditAction:api.ui.Action;
    }

    export class ContentWizardToolbar extends api.ui.toolbar.Toolbar {

        private contextWindowToggler: TogglerButton;
        private componentsViewToggler: TogglerButton;
        private cycleViewModeButton: CycleButton;
        private contentWizardToolbarPublishControls: ContentWizardToolbarPublishControls;

        constructor(params: ContentWizardToolbarParams) {
            super("content-wizard-toolbar");
            super.addAction(params.saveAction);
            super.addAction(params.deleteAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.previewAction);
            super.addGreedySpacer();

            this.cycleViewModeButton = new CycleButton([params.showLiveEditAction, params.showFormAction]);
            this.contextWindowToggler = new TogglerButton("icon-wrench");
            this.componentsViewToggler = new TogglerButton("icon-clipboard");
            this.contentWizardToolbarPublishControls = new ContentWizardToolbarPublishControls(params.publishAction);

            super.addElement(this.contentWizardToolbarPublishControls);
            super.addElement(this.componentsViewToggler);
            super.addElement(this.contextWindowToggler);
            super.addElement(this.cycleViewModeButton);
        }

        getCycleViewModeButton(): CycleButton {
            return this.cycleViewModeButton;
        }

        getContextWindowToggler(): TogglerButton {
            return this.contextWindowToggler;
        }

        getComponentsViewToggler(): TogglerButton {
            return this.componentsViewToggler;
        }

        getContentWizardToolbarPublishControls() {
            return this.contentWizardToolbarPublishControls;
        }

    }
}
