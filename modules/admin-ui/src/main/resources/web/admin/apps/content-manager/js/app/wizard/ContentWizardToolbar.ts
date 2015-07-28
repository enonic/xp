module app.wizard {

    import CycleButton = api.ui.button.CycleButton;
    import ContextWindowToggler = app.wizard.page.contextwindow.ContextWindowToggler;

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

        private contextWindowToggler: ContextWindowToggler;
        private cycleViewModeButton: CycleButton;
        private contentWizardToolbarPublishControls: ContentWizardToolbarPublishControls;

        constructor(params: ContentWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.deleteAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.previewAction);
            super.addGreedySpacer();

            this.cycleViewModeButton = new CycleButton(params.showSplitEditAction, params.showLiveEditAction, params.showFormAction);
            this.contextWindowToggler = new ContextWindowToggler();
            this.contentWizardToolbarPublishControls = new ContentWizardToolbarPublishControls(params.publishAction);

            super.addElement(this.contentWizardToolbarPublishControls);
            super.addElement(this.cycleViewModeButton);
            super.addElement(this.contextWindowToggler);

        }

        getCycleViewModeButton(): CycleButton {
            return this.cycleViewModeButton;
        }

        getContextWindowToggler() {
            return this.contextWindowToggler;
        }

        getContentWizardToolbarPublishControls() {
            return this.contentWizardToolbarPublishControls;
        }

    }
}
