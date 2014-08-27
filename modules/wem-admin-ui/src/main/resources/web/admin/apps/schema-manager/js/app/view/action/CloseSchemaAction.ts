module app.view.action {

    export class CloseSchemaAction extends api.ui.Action {

        constructor(panel: api.ui.panel.Panel, checkCanRemovePanel: boolean = true) {
            super("Close");

            this.onExecuted(() => {
                new app.browse.CloseSchemaEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}