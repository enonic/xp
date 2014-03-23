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

        constructor(params: ContentWizardToolbarParams) {
            super();
            super.addAction(params.saveAction);
            super.addAction(params.duplicateAction);
            super.addAction(params.deleteAction);
            super.addAction(params.publishAction);
            super.addAction(params.previewAction);
            super.addAction(params.closeAction);
            super.addGreedySpacer();

            var liveEditFormToggler = new api.ui.ToggleSlide({
                turnOnAction: params.showLiveEditAction,
                turnOffAction: params.showFormAction
            }, false);

            super.addElement(liveEditFormToggler);
            var contextWindowToggler = new ContextWindowToggler();
            super.addElement(contextWindowToggler);

        }
    }

    export class ContextWindowToggler extends api.ui.Button {

        constructor() {
            super("");
            this.addClass("icon-menu6 icon-large");
            this.setActive(true);
            this.setEnabled(false);
            this.onClicked((event: MouseEvent) => {
                new ToggleContextWindowEvent().fire();
            });

            ToggleContextWindowEvent.on(() => {
                this.setActive(!this.isActive());
            });

            ShowLiveEditEvent.on(() => {
                this.setEnabled(true);
            });

            ShowContentFormEvent.on(() => {
                this.setEnabled(false);
            });
        }

        setActive(value: boolean) {
            if (value) {
                this.addClass("active");
            }
            else {
                this.removeClass("active");
            }
        }

        isActive() {
            return this.hasClass("active");
        }
    }
}
