module app.browse {

    export class CloseContentEvent extends api.event.Event {

        private panel:api.ui.Panel;

        private checkCanRemovePanel:boolean;

        constructor(panel:api.ui.Panel, checkCanRemovePanel:boolean = true) {
            super('closeContentEvent');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api.ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:CloseContentEvent) => void) {
            api.event.onEvent('closeContentEvent', handler);
        }
    }

}
