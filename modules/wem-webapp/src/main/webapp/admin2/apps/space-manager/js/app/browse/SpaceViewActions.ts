module app_view {

    export class EditSpaceAction extends api_ui.Action {

        constructor(panel:api_app_browse.ItemViewPanel) {
            super("Edit");
            this.addExecutionListener(() => {
                new app_browse.EditSpaceEvent([panel.getItem().getModel()]).fire();
            });
        }

    }

    export class DeleteSpaceAction extends api_ui.Action {

        constructor(panel:api_app_browse.ItemViewPanel) {
            super("Delete", "mod+del");
            this.addExecutionListener(() => {
                new app_browse.DeletePromptEvent([panel.getItem().getModel()]).fire();
            });
        }

    }

}
