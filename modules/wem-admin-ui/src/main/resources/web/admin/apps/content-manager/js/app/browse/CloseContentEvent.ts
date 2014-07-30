module app.browse {

    import Panel = api.ui.panel.Panel;

    export class CloseContentEvent extends api.event.Event {

        private panel: Panel;

        private checkCanRemovePanel: boolean;

        constructor(panel: Panel, checkCanRemovePanel: boolean = true) {
            super('closeContentEvent');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel(): Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler: (event: CloseContentEvent) => void) {
            api.event.onEvent('closeContentEvent', handler);
        }
    }

}
