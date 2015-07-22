module app.browse {

    export class ModuleBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions:ModuleBrowseActions) {
            super();
            super.addAction(actions.START_MODULE);
            super.addAction(actions.STOP_MODULE);
        }
    }
}