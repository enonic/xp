module api.app.action {

    export class ShowBrowsePanelAction extends api.ui.Action {

        constructor() {
            super('Browse');

            this.onExecuted(() => {
                new ShowBrowsePanelEvent().fire();
            });
        }
    }
}