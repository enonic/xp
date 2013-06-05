module APP_wizard {

    export class SaveSpaceAction extends API_action.Action {

        constructor() {
            super("Save");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DuplicateSpaceAction extends API_action.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteSpaceAction extends API_action.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class CloseSpaceAction extends API_action.Action {

        constructor() {
            super("Close");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class SpaceWizardActions {

        SAVE_SPACE:API_action.Action = new SaveSpaceAction();
        DUPLICATE_SPACE:API_action.Action = new DuplicateSpaceAction();
        DELETE_SPACE:API_action.Action = new DeleteSpaceAction();
        CLOSE_SPACE:API_action.Action = new CloseSpaceAction();
    }
}
