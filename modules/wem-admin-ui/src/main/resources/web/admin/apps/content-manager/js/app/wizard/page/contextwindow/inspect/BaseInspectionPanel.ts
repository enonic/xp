module app.wizard.page.contextwindow.inspect {

    export class BaseInspectionPanel extends api.ui.panel.Panel {

        constructor() {
            super("inspection-panel");

            this.onRendered((event) => {
                wemjq(this.getHTMLElement()).slimScroll({
                    height: '100%'
                });
            })
        }
    }
}