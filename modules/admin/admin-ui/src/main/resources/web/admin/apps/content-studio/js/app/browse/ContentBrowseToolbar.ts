module app.browse {

    export class ContentBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: app.browse.action.ContentTreeGridActions) {
            super();
            this.addClass("content-browse-toolbar")
            this.addActions(actions.getAllActions());
        }
    }
}
