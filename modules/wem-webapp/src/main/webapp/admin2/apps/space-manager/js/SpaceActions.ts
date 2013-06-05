module app {

    export class NewSpaceAction extends api_action.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new app_event.NewSpaceEvent().fire();
            });
        }
    }

    export class OpenSpaceAction extends api_action.Action {

        constructor() {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new app_event.OpenSpaceEvent(SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class EditSpaceAction extends api_action.Action {

        constructor() {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new app_event.EditSpaceEvent(SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class DeleteSpaceAction extends api_action.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new app_event.DeletePromptEvent(SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class SpaceActions {

        static NEW_SPACE:api_action.Action = new NewSpaceAction();
        static OPEN_SPACE:api_action.Action = new OpenSpaceAction;
        static EDIT_SPACE:api_action.Action = new EditSpaceAction();
        static DELETE_SPACE:api_action.Action = new DeleteSpaceAction();

        static init() {

            app_event.GridSelectionChangeEvent.on((event) => {

                var spaces:app_model.SpaceModel[] = event.getModel();

                if (spaces.length <= 0) {
                    NEW_SPACE.setEnabled(true);
                    OPEN_SPACE.setEnabled(false);
                    EDIT_SPACE.setEnabled(false);
                    DELETE_SPACE.setEnabled(false);
                }
                else if (spaces.length == 1) {
                    NEW_SPACE.setEnabled(false);
                    OPEN_SPACE.setEnabled(true);
                    EDIT_SPACE.setEnabled(spaces[0].data.editable);
                    DELETE_SPACE.setEnabled(spaces[0].data.deletable);
                }
                else {
                    NEW_SPACE.setEnabled(false);
                    OPEN_SPACE.setEnabled(true);
                    EDIT_SPACE.setEnabled(anyEditable(spaces));
                    DELETE_SPACE.setEnabled(anyDeleteable(spaces));
                }
            });
        }

        static anyEditable(spaces:app_model.SpaceModel[]):bool {
            for (var i in spaces) {
                var space:app_model.SpaceModel = spaces[i];
                if (space.data.editable) {
                    return true;
                }
            }
            return false;
        }

        static anyDeleteable(spaces:app_model.SpaceModel[]):bool {
            for (var i in spaces) {
                var space:app_model.SpaceModel = spaces[i];
                if (space.data.deletable) {
                    return true;
                }
            }
            return false;
        }
    }
}
