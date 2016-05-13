import "../../api.ts";

import {DeleteAction} from "../view/DeleteAction";

export interface ConfirmContentDeleteDialogConfig {

    totalItemsToDelete: number;

    deleteRequest: api.content.DeleteContentRequest;

    yesCallback: (exclude?: api.content.CompareStatus[]) => void;
}

export class ConfirmContentDeleteDialog extends api.ui.dialog.ModalDialog {

    private confirmDeleteButton: api.ui.dialog.DialogButton;

    private confirmDeleteAction: api.ui.Action;

    private input: api.ui.text.TextInput;

    private deleteConfig: ConfirmContentDeleteDialogConfig;

    constructor(deleteConfig: ConfirmContentDeleteDialogConfig) {
        super({
            title: new api.ui.dialog.ModalDialogHeader("Confirm delete")
        });

        this.deleteConfig = deleteConfig;

        this.getEl().addClass("confirm-delete-dialog");

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
        this.appendChildToTitle(new api.dom.H6El("confirm-delete-subtitle").setHtml(
            "You are about to delete important content. This action cannot be undone."));
    }

    private initConfirmDeleteAction() {
        this.confirmDeleteAction = new api.ui.Action("Confirm");

        this.confirmDeleteAction.setIconClass("confirm-delete-action");
        this.confirmDeleteAction.setEnabled(false);
        this.confirmDeleteAction.onExecuted(() => {

            if (!!this.deleteConfig.yesCallback) {
                !!this.deleteConfig.deleteRequest.getParams()["deleteOnline"]
                    ? this.deleteConfig.yesCallback([])
                    : this.deleteConfig.yesCallback();
            }

            this.deleteConfig.deleteRequest.sendAndParse().then((result: api.content.DeleteContentResult) => {
                this.close();
                DeleteAction.showDeleteResult(result);
            }).catch((reason: any) => {
                if (reason && reason.message) {
                    api.notify.showError(reason.message);
                } else {
                    api.notify.showError('Content could not be deleted.');
                }
            }).finally(() => {

            }).done();
        });

        this.confirmDeleteButton = this.addAction(this.confirmDeleteAction, true, true);
    }

    private initConfirmationInput() {
        this.input = api.ui.text.TextInput.middle("text").setForbiddenCharsRe(/[^0-9]/);
        this.input.onValueChanged((event: api.ValueChangedEvent) => {
            if (this.isInputEmpty()) {
                this.input.removeClass("invalid valid");
                this.confirmDeleteAction.setEnabled(false);
                return;
            }

            if (this.isCorrectNumberEntered()) {
                this.input.removeClass("invalid").addClass("valid");
                this.confirmDeleteAction.setEnabled(true);
                setTimeout(()=> {
                    this.confirmDeleteButton.giveFocus();
                }, 0);
            }
            else {
                this.input.removeClass("valid").addClass("invalid");
                this.confirmDeleteAction.setEnabled(false);
            }

        });
    }

    private initConfirmationBlock() {
        var confirmationDiv = new api.dom.DivEl("confirm-delete-block");

        confirmationDiv.appendChildren(
            new api.dom.SpanEl("confirm-delete-text").setHtml("Enter "),
            new api.dom.SpanEl("confirm-delete-text-number").setHtml("" + this.deleteConfig.totalItemsToDelete),
            new api.dom.SpanEl("confirm-delete-text").setHtml(" in the field and click Confirm: "),
            this.input);

        this.appendChildToContentPanel(confirmationDiv);
    }

    private isInputEmpty(): boolean {
        return this.input.getValue() == "";
    }

    private isCorrectNumberEntered(): boolean {
        return this.input.getValue() == this.deleteConfig.totalItemsToDelete.toString();
    }
}