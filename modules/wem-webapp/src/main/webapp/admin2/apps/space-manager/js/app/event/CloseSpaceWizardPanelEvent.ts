module app_event {

    export class CloseSpaceWizardPanelEvent extends api_event.Event {

        private panel:api_ui.Panel;

        private checkCanRemovePanel:bool;

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super('closeActiveSpacePanel');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:app_event.CloseSpaceWizardPanelEvent) => void) {
            api_event.onEvent('closeActiveSpacePanel', handler);
        }
    }
}
