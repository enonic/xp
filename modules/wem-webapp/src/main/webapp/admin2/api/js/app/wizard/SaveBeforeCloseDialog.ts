module api_app_wizard {


    export class SaveBeforeCloseDialog extends api_ui_dialog.ModalDialog {

        private wizardPanel:api_app_wizard.WizardPanel;

        private yesAction = new api_ui.Action('Yes', 'mod+y');

        private noAction = new api_ui.Action('No', 'mod+n');

        private cancelAction = new api_ui.Action('Cancel', 'esc');

        constructor(wizardPanel:api_app_wizard.WizardPanel) {
            super({
                idPrefix: "SaveBeforeCloseDialog",
                title: "Close wizard",
                width: 500,
                height: 180
            });

            this.wizardPanel = wizardPanel;

            var message = new api_dom.H6El();
            message.getEl().setInnerHtml("There are unsaved changes, do you want to save them before closing?");
            this.appendChildToContentPanel(message);

            this.setCancelAction(this.cancelAction);
            this.addAction(this.noAction);
            this.addAction(this.yesAction);

            this.cancelAction.addExecutionListener(() => {
                this.close();
            });

            this.yesAction.addExecutionListener(() => {
                this.doSaveAndClose();
            });

            this.noAction.addExecutionListener(() => {
                this.doCloseWithoutSaveCheck();
            });
        }

        show() {
            api_dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            this.remove();
            super.close();
        }

        private doSaveAndClose() {

            this.close();
            this.wizardPanel.saveChanges(() => {
                this.wizardPanel.close(true);
            });
        }

        private doCloseWithoutSaveCheck() {

            this.close();
            this.wizardPanel.close();
        }

    }

}