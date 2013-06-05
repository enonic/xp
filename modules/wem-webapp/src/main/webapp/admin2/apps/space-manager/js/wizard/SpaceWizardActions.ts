module app_wizard {

    export class SaveSpaceAction extends api_action.Action {

        constructor() {
            super("Save");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DuplicateSpaceAction extends api_action.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteSpaceAction extends api_action.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class CloseSpaceAction extends api_action.Action {

        constructor() {
            super("Close");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class SpaceWizardActions {

        SAVE_SPACE:api_action.Action = new SaveSpaceAction();
        DUPLICATE_SPACE:api_action.Action = new DuplicateSpaceAction();
        DELETE_SPACE:api_action.Action = new DeleteSpaceAction();
        CLOSE_SPACE:api_action.Action = new CloseSpaceAction();
    }
}
