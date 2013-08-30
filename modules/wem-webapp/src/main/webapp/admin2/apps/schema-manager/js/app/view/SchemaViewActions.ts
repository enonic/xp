module app_view {

    export class EditSchemaAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel) {
            super("Edit");

            this.addExecutionListener(() => {
                new app_browse.EditSchemaEvent([panel.getItem().getModel()]).fire();
            });
        }
    }

    export class DeleteSchemaAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel) {
            super("Delete");

            this.addExecutionListener(() => {
                new app_browse.DeleteSchemaPromptEvent([panel.getItem().getModel()]).fire();
            });
        }
    }

    export class CloseSchemaAction extends api_ui.Action {

        constructor(panel:api_ui.Panel, checkCanRemovePanel:boolean = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_browse.CloseSchemaEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }

}
