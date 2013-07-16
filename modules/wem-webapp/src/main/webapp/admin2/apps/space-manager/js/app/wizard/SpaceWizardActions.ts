module app_wizard {

    export class SaveSpaceAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

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
