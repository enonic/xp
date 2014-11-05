module app.browse {

    export class UserBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: UserBrowseActions) {
            super();
            this.addActions(actions.getAllActions());
        }
    }
}