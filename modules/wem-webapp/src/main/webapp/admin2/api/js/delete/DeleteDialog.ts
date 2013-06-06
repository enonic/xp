module api_delete{

    export class DeleteDialog extends api_ui_dialog.ModalDialog {

        private deleteButton:api_ui_dialog.DialogButton;

        private cancelButton:api_ui_dialog.DialogButton;

        constructor(title:string, deleteAction:api_action.Action, cancelAction:api_action.Action) {
            super(title);
            this.deleteButton = new api_ui_dialog.DialogButton(deleteAction);
            this.cancelButton = new api_ui_dialog.DialogButton(cancelAction);
        }
    }
}
