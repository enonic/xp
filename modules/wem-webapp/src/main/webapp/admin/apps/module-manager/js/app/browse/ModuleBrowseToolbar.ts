module app.browse {

    export class ModuleBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions:ModuleBrowseActions) {
            super();
            super.addAction(actions.IMPORT_MODULE);
            super.addAction(actions.EXPORT_MODULE);
            super.addAction(actions.DELETE_MODULE);
        }
    }
}