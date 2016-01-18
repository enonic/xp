module app.view.detail.button {

    export class MobileDetailsPanelToggleButton extends api.dom.DivEl {

        private detailsPanel: DetailsPanel;

        constructor(detailsPanel: DetailsPanel) {
            super("mobile-details-panel-toggle-button");

            this.detailsPanel = detailsPanel;

            this.onClicked((event) => {
                this.toggleClass("expanded");
                if (this.hasClass("expanded")) {
                    this.detailsPanel.slideIn();
                } else {
                    this.detailsPanel.slideOut();
                }
            });
        }
    }
}