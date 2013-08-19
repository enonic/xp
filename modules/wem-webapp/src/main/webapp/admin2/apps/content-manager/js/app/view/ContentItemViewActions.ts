module app_view {

    export class EditContentAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel) {
            super("Edit");
            this.addExecutionListener(() => {
                new app_browse.EditContentEvent([panel.getItem().getModel()]).fire();
            });
        }
    }

    export class DeleteContentAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel) {
            super("Delete", "mod+del");
            this.addExecutionListener(() => {
                new app_browse.ContentDeletePromptEvent([panel.getItem().getModel()]).fire();
            });
        }

    }

    export class CloseContentAction extends api_ui.Action {

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_browse.CloseContentEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}
