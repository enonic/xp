module app_view {

    export class EditSpaceAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel) {
            super("Edit");
            this.addExecutionListener(() => {
                new app_browse.EditSpaceEvent([panel.getItem().getModel()]).fire();
            });
        }

    }

    export class DeleteSpaceAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel) {
            super("Delete", "mod+del");
            this.addExecutionListener(() => {
                new app_browse.SpaceDeletePromptEvent([panel.getItem().getModel()]).fire();
            });
        }

    }

    export class CloseSpaceAction extends api_ui.Action {

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_browse.CloseSpaceEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }

}
