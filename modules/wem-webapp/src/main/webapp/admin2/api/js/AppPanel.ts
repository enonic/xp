module api{

    export class AppPanel extends api_ui.DeckPanel {

        ext;

        private browsePanel:AppBrowsePanel;

        private deckPanel:api.AppDeckPanel;

        constructor(browsePanel:AppBrowsePanel, deckPanel:api.AppDeckPanel) {
            super("AppPanel");

            this.browsePanel = browsePanel;
            this.deckPanel = deckPanel;

            this.addPanel(this.browsePanel);
            this.addPanel(this.deckPanel);
            this.showPanel(0);

            this.initExt();
        }

        private initExt() {

            this.ext = new Ext.layout.container.Card({
                id: 'AppPanel',
                title: 'AppPanel',
                closable: false,
                border: false,
                layout: 'card'
            });

            this.ext.add(this.browsePanel);
            this.ext.add(this.deckPanel);
        }

    }
}
