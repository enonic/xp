module api.app.wizard {


    export class SaveBeforeCloseDialog extends api.ui.dialog.ModalDialog {

        private wizardPanel: api.app.wizard.WizardPanel<any>;

        private yesAction = new api.ui.Action('Yes', 'y');

        private noAction = new api.ui.Action('No', 'n');

        constructor(wizardPanel: api.app.wizard.WizardPanel<any>) {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Close wizard")
            });

            this.wizardPanel = wizardPanel;

            var message = new api.dom.H6El();
            message.getEl().setInnerHtml("There are unsaved changes, do you want to save them before closing?");
            this.appendChildToContentPanel(message);

            this.yesAction.setMnemonic("y");
            this.yesAction.onExecuted(() => {
                this.doSaveAndClose();
            });
            this.addAction(this.yesAction, true);

            this.noAction.setMnemonic("n");
            this.noAction.onExecuted(() => {
                this.doCloseWithoutSaveCheck();
            });
            this.addAction(this.noAction);

            this.getCancelAction().setMnemonic("c");
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
            this.wizardPanel.saveChanges().
                then(() => this.wizardPanel.close(true)).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();
        }

        private doCloseWithoutSaveCheck() {

            this.close();
            this.wizardPanel.close();
        }

    }

}