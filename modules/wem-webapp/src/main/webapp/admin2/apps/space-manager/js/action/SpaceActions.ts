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
        }
    }

    export class SpaceActions {

        static NEW_SPACE:API_action.Action = new NewSpaceAction();
        static OPEN_SPACE:API_action.Action = new OpenSpaceAction;
        static EDIT_SPACE:API_action.Action = new EditSpaceAction();
        static DELETE_SPACE:API_action.Action = new DeleteSpaceAction();
    }
}
