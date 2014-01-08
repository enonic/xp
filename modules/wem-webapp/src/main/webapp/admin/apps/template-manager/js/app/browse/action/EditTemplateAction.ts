module app.browse.action {

    export class EditTemplateAction extends api.ui.Action {

        constructor() {
            super("Edit");
            this.addExecutionListener(() => {
                console.log("edit template action");
            });
        }

    }
}