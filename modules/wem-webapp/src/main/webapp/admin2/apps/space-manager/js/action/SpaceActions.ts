module APP_action {

    export class NewSpaceAction extends API_action.Action {

        constructor() {
            super("New");
        }
    }

    export class OpenSpaceAction extends API_action.Action {

        constructor() {
            super("Open");
        }
    }

    export class EditSpaceAction extends API_action.Action {

        constructor() {
            super("Edit");
        }
    }

    export class DeleteSpaceAction extends API_action.Action {

        constructor() {
            super("Delete");

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
                else {
                    console.log(spaces.length + " spaces selected");

                    NEW_SPACE.setEnabled(false);
                    OPEN_SPACE.setEnabled(true);
                    EDIT_SPACE.setEnabled(true);
                    DELETE_SPACE.setEnabled(true);
                }
            });

        }
    }
}
