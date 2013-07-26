module app_wizard {

    export class SaveContentTypeAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

    export class CloseContentTypeAction extends api_ui.Action {

        constructor(panel: api_ui.Panel, checkCanRemovePanel?: bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_browse.CloseSchemaEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}