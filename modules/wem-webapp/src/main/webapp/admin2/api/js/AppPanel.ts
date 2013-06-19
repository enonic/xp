module api{

    export class AppPanel extends api_ui.DeckPanel {

        private browsePanel:AppBrowsePanel;

        private deckPanel:api.AppDeckPanel;

        constructor(browsePanel:AppBrowsePanel, deckPanel:api.AppDeckPanel) {
            super("AppPanel");

            this.browsePanel = browsePanel;
            this.deckPanel = deckPanel;
            deckPanel.setAppPanel(this);

            this.addPanel(this.browsePanel);
            this.addPanel(this.deckPanel);
            this.showPanel(0);

        }

        showBrowsePanel() {
            this.showPanel(0);
        }

        showDeckPanel() {
            this.showPanel(1);
        }
    }
}
