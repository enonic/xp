module app_wizard {

    export class DuplicateSpaceAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
        }
    }

    export class DeleteSpaceAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
        }
    }
}
