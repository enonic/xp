module app {

    export class SpaceAppPanel extends api.AppPanel {

        private appBrowsePanel:SpaceAppBrowsePanel;

        private formDeckPanel:api.AppDeckPanel;

        constructor() {

            this.appBrowsePanel = new SpaceAppBrowsePanel();
            this.formDeckPanel = new api.AppDeckPanel();

            super(this.appBrowsePanel, this.formDeckPanel);
        }
    }

}
