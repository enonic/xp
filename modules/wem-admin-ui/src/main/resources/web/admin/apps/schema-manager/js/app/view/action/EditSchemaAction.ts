module app.view.action {

    export class EditSchemaAction extends api.ui.Action {

        constructor(panel: api.app.view.ItemViewPanel<api.schema.Schema>) {
            super("Edit");

            this.onExecuted(() => {
                new app.browse.EditSchemaEvent([panel.getItem().getModel()]).fire();
            });
        }
    }
}