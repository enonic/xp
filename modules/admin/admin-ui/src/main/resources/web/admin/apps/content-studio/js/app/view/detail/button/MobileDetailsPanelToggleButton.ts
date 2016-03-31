module app.view.detail.button {

    export class MobileDetailsPanelToggleButton extends api.dom.DivEl {

        private detailsPanel: DetailsPanel;

        public static EXPANDED_CLASS: string = "expanded";

        constructor(detailsPanel: DetailsPanel, slideInCallback?: () => void) {
            super("mobile-details-panel-toggle-button");

            this.detailsPanel = detailsPanel;

            this.onClicked(() => {
                this.toggleClass(MobileDetailsPanelToggleButton.EXPANDED_CLASS);
                if (this.hasClass(MobileDetailsPanelToggleButton.EXPANDED_CLASS)) {
                    this.detailsPanel.slideIn();
                    if (!!slideInCallback) {
                        slideInCallback();
                    }
                } else {
                    this.detailsPanel.slideOut();
                }
            });
        }
    }
}