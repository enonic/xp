module api.ui.dialog{

    export class DialogButton extends api.ui.ActionButton {

        constructor(action:api.ui.Action) {
            super(true, action, false);
        }
    }
}
