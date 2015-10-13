module app.view.detail.button {

    export class InfoWidgetToggleButton extends api.dom.DivEl {

        private detailsPanel: DetailsPanel;

        constructor(detailsPanel: DetailsPanel) {
            super("info-widget-toggle-button");

            this.detailsPanel = detailsPanel;

            this.onClicked((event) => {
                this.addClass("active");
                detailsPanel.activateDefaultWidget();
            });
        }

        setActive() {
            this.addClass("active");
        }

        setInactive() {
            this.removeClass("active");
        }
    }
}