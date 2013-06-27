module app_wizard {

    export class SaveContentAction extends api_ui.Action {

        constructor() {
            super("Save");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DuplicateContentAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteContentAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class CloseContentPanelAction extends api_ui.Action {

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_event.CloseContentWizardPanelEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}
