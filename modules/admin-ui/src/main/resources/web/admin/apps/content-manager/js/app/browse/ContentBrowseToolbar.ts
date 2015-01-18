module app.browse {

    export class ContentBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: app.browse.action.ContentTreeGridActions) {
            super();

            this.addActions(actions.getAllActions());
        }
    }
}
