module app_event {

    export class CloseOpenSpacePanelEvent extends api_event.Event {

        private panel:api_ui.Panel;

        constructor(panel:api_ui.Panel) {
            super('closeActiveSpacePanel');
            this.panel = panel;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }

        static on(handler:(event:app_event.CloseOpenSpacePanelEvent) => void) {
            api_event.onEvent('closeActiveSpacePanel', handler);
        }
    }
}
