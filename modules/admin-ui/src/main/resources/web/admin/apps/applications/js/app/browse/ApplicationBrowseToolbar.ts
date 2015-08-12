module app.browse {

    export class ApplicationBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: ApplicationBrowseActions) {
            super();
            super.addAction(actions.START_APPLICATION);
            super.addAction(actions.STOP_APPLICATION);
        }
    }
}