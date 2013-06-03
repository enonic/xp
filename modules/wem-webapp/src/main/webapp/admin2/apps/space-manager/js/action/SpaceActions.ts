module APP_action {

    export class NewSpaceAction extends API_action.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new APP.event.NewSpaceEvent().fire();
            });
        }
    }

    export class OpenSpaceAction extends API_action.Action {

        constructor() {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new APP.event.OpenSpaceEvent(APP_context.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class EditSpaceAction extends API_action.Action {

        constructor() {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new APP.event.EditSpaceEvent(APP_context.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class DeleteSpaceAction extends API_action.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new APP.event.DeletePromptEvent(APP_context.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class SpaceActions {

        static NEW_SPACE:API_action.Action = new NewSpaceAction();
        static OPEN_SPACE:API_action.Action = new OpenSpaceAction;
        static EDIT_SPACE:API_action.Action = new EditSpaceAction();
        static DELETE_SPACE:API_action.Action = new DeleteSpaceAction();

        static init() {

            APP.event.GridSelectionChangeEvent.on((event) => {

                var spaces:APP.model.SpaceModel[] = event.getModel();

                if (spaces.length <= 0) {
                    console.log("no spaces selected");

                    NEW_SPACE.setEnabled(true);
                    OPEN_SPACE.setEnabled(false);
                    EDIT_SPACE.setEnabled(false);
                    DELETE_SPACE.setEnabled(false);
                }
                else if (spaces.length == 1) {

                    console.log("one spaces selected");

                    NEW_SPACE.setEnabled(false);
                    OPEN_SPACE.setEnabled(true);
                    EDIT_SPACE.setEnabled(spaces[0].data.editable);
                    DELETE_SPACE.setEnabled(spaces[0].data.deletable);
                }
                else {
                    console.log(spaces.length + "spaces selected");

                    NEW_SPACE.setEnabled(false);
                    OPEN_SPACE.setEnabled(true);
                    EDIT_SPACE.setEnabled(anyEditable(spaces));
                    DELETE_SPACE.setEnabled(anyDeleteable(spaces));
                }
            });
        }

        static anyEditable(spaces:APP.model.SpaceModel[]):bool {
            for (var i in spaces) {
                var space:APP.model.SpaceModel = spaces[i];
                if (space.data.editable) {
                    return true;
                }
            }
            return false;
        }

        static anyDeleteable(spaces:APP.model.SpaceModel[]):bool {
            for (var i in spaces) {
                var space:APP.model.SpaceModel = spaces[i];
                if (space.data.deletable) {
                    return true;
                }
            }
            return false;
        }
    }
}
