module api.app.bar.action {

    export class ShowBrowsePanelAction extends api.ui.Action {

        constructor() {
            super('Browse');

            this.onExecuted(() => {
                new api.app.bar.event.ShowBrowsePanelEvent().fire();
            });
        }
    }
}