module app {

    export class SpaceAppPanel extends api.AppPanel {

        private appBrowsePanel:SpaceAppBrowsePanel;

        private formDeckPanel:api.FormDeckPanel;

        constructor() {

            this.appBrowsePanel = new SpaceAppBrowsePanel();
            this.formDeckPanel = new api.FormDeckPanel();

            super(this.appBrowsePanel, this.formDeckPanel);
        }
    }

}
