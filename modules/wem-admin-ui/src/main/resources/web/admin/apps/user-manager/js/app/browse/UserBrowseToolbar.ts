module app.browse {

    export class UserBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: UserTreeGridActions) {
            super();
            this.addActions(actions.getAllActions());
        }
    }
}