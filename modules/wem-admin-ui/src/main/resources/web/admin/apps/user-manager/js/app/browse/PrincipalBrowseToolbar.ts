module app.browse {

    export class PrincipalBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: PrincipalBrowseActions) {
            super();
            this.addActions(actions.getAllActions());
        }
    }
}