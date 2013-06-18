module api{

    export class AppPanel extends api_ui.DeckPanel {

        ext;

        private appBrowsePanel:AppBrowsePanel;

        private formDeckPanel:FormDeckPanel;

        constructor(appMainPanel:AppBrowsePanel, formDeckPanel:FormDeckPanel) {
            super("AppPanel");

            this.appBrowsePanel = appMainPanel;
            this.formDeckPanel = formDeckPanel;

            this.addPanel(this.appBrowsePanel);
            this.addPanel(this.formDeckPanel);
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

            this.ext.add(this.appBrowsePanel);
            this.ext.add(this.formDeckPanel);
        }

    }
}
