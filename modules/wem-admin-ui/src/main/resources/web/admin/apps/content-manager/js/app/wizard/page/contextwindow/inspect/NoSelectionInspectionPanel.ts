module app.wizard.page.contextwindow.inspect {

    export class NoSelectionInspectionPanel extends api.ui.panel.Panel {

        private header: api.app.NamesView;

        constructor() {
            super("inspection-panel");

            this.header = new api.app.NamesView().setMainName("No item selected");

            this.appendChild(this.header);
        }
    }
}