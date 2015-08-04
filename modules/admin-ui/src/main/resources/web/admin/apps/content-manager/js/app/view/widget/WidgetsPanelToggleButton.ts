module app.view.widget {

    export class WidgetsPanelToggleButton extends api.dom.DivEl {

        private widgetsPanel: WidgetsPanel;

        constructor(widgetsPanel: WidgetsPanel) {
            super("widget-panel-toggle-button");

            this.widgetsPanel = widgetsPanel;

            this.onClicked((event) => {
                this.toggleClass("expanded");
                if (this.hasClass("expanded")) {
                    this.widgetsPanel.slideIn();
                } else {
                    this.widgetsPanel.slideOut();
                }
            });
        }

    }
}
