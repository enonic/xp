module api.app.remove {
    export class CancelDeleteDialogAction extends api.ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }
}