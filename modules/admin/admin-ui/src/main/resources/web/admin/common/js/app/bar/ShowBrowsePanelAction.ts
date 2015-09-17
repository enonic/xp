module api.app.bar {

    export class ShowBrowsePanelAction extends api.ui.Action {

        constructor() {
            super('Browse');

            this.onExecuted(() => {
                new api.app.ShowBrowsePanelEvent().fire();
            });
        }
    }
}