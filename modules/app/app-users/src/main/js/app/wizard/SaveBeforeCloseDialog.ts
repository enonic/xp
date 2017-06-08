import WizardPanel = api.app.wizard.WizardPanel;
import ModalDialog = api.ui.dialog.ModalDialog;
import Action = api.ui.Action;
import i18n = api.util.i18n;

export class SaveBeforeCloseDialog extends ModalDialog {

    private wizardPanel: WizardPanel<any>;

    private yesAction: Action = new Action(i18n('action.yes'), 'y');

    private noAction: Action = new Action(i18n('action.no'), 'n');

    constructor(wizardPanel: WizardPanel<any>) {
        super(i18n('dialog.saveBeforeClose.title'));

        this.wizardPanel = wizardPanel;

        let message = new api.dom.H6El();
        message.getEl().setInnerHtml(i18n('dialog.saveBeforeClose.msg'));
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
