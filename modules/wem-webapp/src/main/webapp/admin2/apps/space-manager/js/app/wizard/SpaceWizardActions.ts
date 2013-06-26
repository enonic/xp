module app_wizard {

    export class SaveSpaceAction extends api_ui.Action {

        constructor() {
            super("Save");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DuplicateSpaceAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteSpaceAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class CloseSpacePanelAction extends api_ui.Action {

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_event.CloseSpaceWizardPanelEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}
