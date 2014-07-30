module app.wizard {

    import CycleButton = api.ui.button.CycleButton;

    export interface ContentWizardToolbarParams {
        saveAction:api.ui.Action;
        duplicateAction:api.ui.Action;
        deleteAction:api.ui.Action;
        closeAction:api.ui.Action;
        publishAction:api.ui.Action;
        previewAction:api.ui.Action;
        showLiveEditAction:api.ui.Action;
        showFormAction:api.ui.Action;
        showSplitEditAction:api.ui.Action;
    }

    export class ContentWizardToolbar extends api.ui.toolbar.Toolbar {

        private contextWindowToggler: ContextWindowToggler;
        private cycleViewModeButton: CycleButton;

        constructor(params: ContentWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addAction(params.publishAction);
            super.addAction(params.previewAction);
            super.addAction(params.closeAction);
            super.addGreedySpacer();

            this.cycleViewModeButton = new CycleButton(params.showSplitEditAction, params.showFormAction, params.showLiveEditAction);

            super.addElement(this.cycleViewModeButton);
            this.contextWindowToggler = new ContextWindowToggler();
            super.addElement(this.contextWindowToggler);

        }

        getCycleViewModeButton(): CycleButton {
            return this.cycleViewModeButton;
        }

        getContextWindowToggler() {
            return this.contextWindowToggler;
        }

    }

    export class ContextWindowToggler extends api.ui.button.Button {

        constructor() {
            super("");
            this.addClass("icon-menu6 icon-large toggler");
            this.setActive(true);
            this.onClicked((event: MouseEvent) => {
                new ToggleContextWindowEvent(!this.isActive()).fire();
            });

            ToggleContextWindowEvent.on((event: ToggleContextWindowEvent) => {
                this.setActive(event.isFixed());
            });

            ShowLiveEditEvent.on(() => {
                this.setEnabled(true);
            });

            ShowSplitEditEvent.on(() => {
                this.setEnabled(true);
            });

            ShowContentFormEvent.on(() => {
                this.setEnabled(false);
            });
        }
    }
}
