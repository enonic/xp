module app_wizard {

    export class SaveRelationshipTypeAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

    export class CloseRelationshipTypeAction extends api_ui.Action {

        constructor(panel: api_ui.Panel, checkCanRemovePanel?: bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_browse.CloseSchemaEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}