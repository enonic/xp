import '../../api.ts';
import i18n = api.util.i18n;

export interface ConfirmContentDeleteDialogConfig {

    totalItemsToDelete: number;

    deleteRequest: api.content.resource.DeleteContentRequest;

    yesCallback: (exclude?: api.content.CompareStatus[]) => void;
}

export class ConfirmContentDeleteDialog
    extends api.ui.dialog.ModalDialog {

    private confirmDeleteButton: api.ui.dialog.DialogButton;

    private confirmDeleteAction: api.ui.Action;

    private input: api.ui.text.TextInput;

    private deleteConfig: ConfirmContentDeleteDialogConfig;

    constructor(deleteConfig: ConfirmContentDeleteDialogConfig) {
        super(<api.ui.dialog.ModalDialogConfig>{title: i18n('dialog.confirmDelete')});

        this.deleteConfig = deleteConfig;

        this.getEl().addClass('confirm-delete-dialog');

        this.addSubtitle();

        this.initConfirmDeleteAction();

        this.initConfirmationInput();

        this.initConfirmationBlock();

        this.addCancelButtonToBottom();
    }

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
        this.input.giveFocus();
    }

    close() {
        super.close();
        this.remove();
    }

    private addSubtitle() {
        this.appendChildToHeader(new api.dom.H6El('confirm-delete-subtitle').setHtml(i18n('dialog.confirmDelete.subname')));
    }

    private initConfirmDeleteAction() {
        this.confirmDeleteAction = new api.ui.Action(i18n('action.confirm'));

        this.confirmDeleteAction.setIconClass('confirm-delete-action');
        this.confirmDeleteAction.setEnabled(false);
        this.confirmDeleteAction.onExecuted(() => {
            this.close();
            this.deleteConfig.yesCallback();
        });

        this.confirmDeleteButton = this.addAction(this.confirmDeleteAction, true, true);
    }

    private initConfirmationInput() {
        this.input = api.ui.text.TextInput.middle('text').setForbiddenCharsRe(/[^0-9]/);
        this.input.onValueChanged((event: api.ValueChangedEvent) => {
            if (this.isInputEmpty()) {
                this.input.removeClass('invalid valid');
                this.confirmDeleteAction.setEnabled(false);
                return;
            }

            if (this.isCorrectNumberEntered()) {
                this.input.removeClass('invalid').addClass('valid');
                this.confirmDeleteAction.setEnabled(true);
                setTimeout(() => {
                    this.confirmDeleteButton.giveFocus();
                }, 0);
            } else {
                this.input.removeClass('valid').addClass('invalid');
                this.confirmDeleteAction.setEnabled(false);
            }

        });
    }

    private initConfirmationBlock() {
        let confirmationText = new api.dom.PEl('confirm-delete-text')
            .setHtml(i18n('dialog.confirmDelete.enterAmount', this.deleteConfig.totalItemsToDelete), false);

        let confirmationDiv = new api.dom.DivEl('confirm-delete-block')
            .appendChildren(confirmationText, this.input);

        this.appendChildToContentPanel(confirmationDiv);
    }

    private isInputEmpty(): boolean {
        return this.input.getValue() === '';
    }

    private isCorrectNumberEntered(): boolean {
        return this.input.getValue() === this.deleteConfig.totalItemsToDelete.toString();
    }

    private enableActions() {
        this.confirmDeleteAction.setEnabled(true);
        this.getCancelAction().setEnabled(true);
    }

    private disableActions() {
        this.confirmDeleteAction.setEnabled(false);
        this.getCancelAction().setEnabled(false);
    }
}
