module app_contextwindow {
    export class ContextWindow extends api_ui.NavigableFloatingWindow {
        private componentsPanel:ComponentsPanel;
        private inspectorPanel:api_ui.Panel;
        private emulatorPanel:api_ui.Panel;

        constructor() {
            super();
            this.addClass("context-window");

            this.componentsPanel = new ComponentsPanel();
            this.inspectorPanel = new api_ui.Panel();
            this.emulatorPanel = new api_ui.Panel();

            this.addItem("Components", this.componentsPanel);
            this.addItem("Inspector", this.inspectorPanel);
            this.addItem("Emulator", this.emulatorPanel);

        }
    }
}