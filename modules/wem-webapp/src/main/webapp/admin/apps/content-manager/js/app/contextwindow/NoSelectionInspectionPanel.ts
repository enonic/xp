module app.contextwindow {

    export class NoSelectionInspectionPanel extends api.ui.Panel {

        private header: api.app.NamesView;

        constructor() {
            super("inspection-panel");

            this.header = new api.app.NamesView().setMainName("No item selected");

            this.appendChild(this.header);
        }
    }
}