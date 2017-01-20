import WizardPanel = api.app.wizard.WizardPanel;
import ModalDialog = api.ui.dialog.ModalDialog;
import Action = api.ui.Action;

export class SaveBeforeCloseDialog extends ModalDialog {

    private wizardPanel: WizardPanel<any>;

    private yesAction: Action = new Action('Yes', 'y');

    private noAction: Action = new Action('No', 'n');

    constructor(wizardPanel: WizardPanel<any>) {
        super('Close wizard');

        this.wizardPanel = wizardPanel;

        let message = new api.dom.H6El();
        message.getEl().setInnerHtml('There are unsaved changes, do you want to save them before closing?');
        this.appendChildToContentPanel(message);

        this.yesAction.setMnemonic('y');
        this.yesAction.onExecuted(() => {
            this.doSaveAndClose();
        });
        this.addAction(this.yesAction, true);

        this.noAction.setMnemonic('n');
        this.noAction.onExecuted(() => {
            this.doCloseWithoutSaveCheck();
        });
        this.addAction(this.noAction);

        this.getCancelAction().setMnemonic('c');
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
        this.wizardPanel.saveChanges().then(() => this.wizardPanel.close(true)).catch(
            (reason: any) => api.DefaultErrorHandler.handle(reason)).done();
    }

    private doCloseWithoutSaveCheck() {

        this.close();
        this.wizardPanel.close();
    }

}
