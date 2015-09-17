module app.remove {

    export class ContentDeleteDialogAction extends api.ui.Action {
        constructor() {
            super("Delete", "enter");
            this.setIconClass("delete-action");
        }
    }
}