module api_app_wizard {


    export class SaveBeforeCloseDialog extends api_ui_dialog.ModalDialog {

        private wizardPanel:api_app_wizard.WizardPanel<any>;

        private yesAction = new api_ui.Action('Yes');

        private noAction = new api_ui.Action('No');

        constructor(wizardPanel:api_app_wizard.WizardPanel<any>) {
            super({
                idPrefix: "SaveBeforeCloseDialog",
                title: "Close wizard",
                width: 500,
                height: 180
            });

            this.setCancelAction(new api_ui.Action('Cancel', 'esc'));

            this.wizardPanel = wizardPanel;

            this.getCancelAction().setMnemonic("c");
            this.getCancelAction().addExecutionListener(() => {
                this.close();
            });

            this.yesAction.setMnemonic("y");
            this.yesAction.addExecutionListener(() => {
                this.doSaveAndClose();
            });

            this.noAction.setMnemonic("n");
            this.noAction.addExecutionListener(() => {
                this.doCloseWithoutSaveCheck();
            });


            var message = new api_dom.H6El();
            message.getEl().setInnerHtml("There are unsaved changes, do you want to save them before closing?");
            this.appendChildToContentPanel(message);

            this.addAction(this.noAction);
            this.addAction(this.yesAction);
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