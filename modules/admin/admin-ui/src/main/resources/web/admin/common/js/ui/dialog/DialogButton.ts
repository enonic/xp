module api.ui.dialog {

    export class DialogButton extends api.ui.button.ActionButton {

        constructor(action: api.ui.Action) {
            super(action, false);
            this.addClass("dialog-button");
        }
    }
}
