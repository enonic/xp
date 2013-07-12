module app_wizard {

    export class SpaceCloseWizardPanelEvent extends api_event.Event {

        private panel:api_ui.Panel;

        private checkCanRemovePanel:bool;

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super('spaceCloseWizardPanel');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:app_wizard.SpaceCloseWizardPanelEvent) => void) {
            api_event.onEvent('spaceCloseWizardPanel', handler);
        }
    }
}
