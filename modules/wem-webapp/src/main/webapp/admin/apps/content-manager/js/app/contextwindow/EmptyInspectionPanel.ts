module app.contextwindow {

    export class EmptyInspectionPanel extends api.ui.Panel {

        private header: api.app.NamesView;

        constructor() {
            super("inspection-panel");

            this.header = new api.app.NamesView().setMainName("No component selected");

            this.appendChild(this.header);
        }
    }
}