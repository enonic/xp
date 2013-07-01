module app_browse {

    export class NewSpaceAction extends api_ui.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new NewSpaceEvent().fire();
            });
        }
    }

    export class OpenSpaceAction extends api_ui.Action {

        constructor() {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new OpenSpaceEvent(app.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class EditSpaceAction extends api_ui.Action {

        constructor() {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new EditSpaceEvent(app.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class DeleteSpaceAction extends api_ui.Action {

        constructor() {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new DeletePromptEvent(app.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
    }

    export class SpaceBrowseActions {

        static NEW_SPACE:api_ui.Action = new NewSpaceAction();
        static OPEN_SPACE:api_ui.Action = new OpenSpaceAction();
        static EDIT_SPACE:api_ui.Action = new EditSpaceAction();
        static DELETE_SPACE:api_ui.Action = new DeleteSpaceAction();

        static ACTIONS:api_ui.Action[] = [];

        static init() {

            ACTIONS.push(NEW_SPACE, OPEN_SPACE, EDIT_SPACE, DELETE_SPACE);

            GridSelectionChangeEvent.on((event) => {

                var spaces:api_model.SpaceModel[] = event.getModels();

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

        private static anyEditable(spaces:api_model.SpaceModel[]):bool {
            for (var i in spaces) {
                var space:api_model.SpaceModel = spaces[i];
                if (space.data.editable) {
                    return true;
                }
            }
            return false;
        }

        private static anyDeleteable(spaces:api_model.SpaceModel[]):bool {
            for (var i in spaces) {
                var space:api_model.SpaceModel = spaces[i];
                if (space.data.deletable) {
                    return true;
                }
            }
            return false;
        }
    }
}
