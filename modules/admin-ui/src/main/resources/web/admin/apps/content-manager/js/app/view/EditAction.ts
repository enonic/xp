module app.view {

    export class EditAction extends api.ui.Action {

        constructor(panel: api.app.view.ItemViewPanel<api.content.ContentSummary>) {
            super("Edit");
            this.onExecuted(() => {
                new api.content.EditContentEvent([panel.getItem().getModel()]).fire();
            });
        }
    }
}
