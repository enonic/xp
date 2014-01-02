module api.app.wizard {


    export class SaveBeforeCloseDialog extends api.ui.dialog.ModalDialog {

        private wizardPanel:api.app.wizard.WizardPanel<any>;

        private yesAction = new api.ui.Action('Yes');

        private noAction = new api.ui.Action('No');

        constructor(wizardPanel:api.app.wizard.WizardPanel<any>) {
            super({
                idPrefix: "SaveBeforeCloseDialog",
                title: "Close wizard",
                width: 500,
                height: 180
            });

            this.setCancelAction(new api.ui.Action('Cancel', 'esc'));

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


            var message = new api.dom.H6El();
            message.getEl().setInnerHtml("There are unsaved changes, do you want to save them before closing?");
            this.appendChildToContentPanel(message);

            this.addAction(this.noAction);
            this.addAction(this.yesAction);
        }

        show() {
            api.dom.Body.get().appendChild(this);
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