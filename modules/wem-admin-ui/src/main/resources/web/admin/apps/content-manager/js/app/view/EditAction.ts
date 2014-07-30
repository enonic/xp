module app.view {

    export class EditAction extends api.ui.Action {

        constructor(panel: api.app.view.ItemViewPanel<api.content.ContentSummary>) {
            super("Edit");
            this.onExecuted(() => {
                new app.browse.EditContentEvent([panel.getItem().getModel()]).fire();
            });
        }
    }
}
