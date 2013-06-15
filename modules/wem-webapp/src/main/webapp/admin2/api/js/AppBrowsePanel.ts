module api{

    export class AppBrowsePanel extends api_ui.Panel {

        ext;

        private browseToolbar:api_ui_toolbar.Toolbar;

        private grid:any;

        private detailPanel:api_ui_detailpanel.DetailPanel;

        private filterPanel:any;

        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:any, detailPanel:api_ui_detailpanel.DetailPanel, filterPanel:any) {

            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.detailPanel = detailPanel;
            this.filterPanel = filterPanel;

            super("AppBrowsePanel");

            this.initExt();
        }

        private initExt() {

            var center = new Ext.container.Container({
                region: 'center',
                layout: 'border'
            });

            center.add(this.browseToolbar.ext);
            center.add(this.grid.ext);
            center.add(this.detailPanel.ext);

            this.ext = new Ext.panel.Panel({
                id: 'tab-browse',
                title: 'Browse',
                closable: false,
                border: false,
                layout: 'border',
                tabConfig: { hidden: true }
            });

            this.ext.add(center);
            this.ext.add(this.filterPanel);
        }
    }
}
