module app.installation.view {


    export class MarketAppPanel extends api.ui.panel.Panel {

        private marketAppsTreeGrid: MarketAppsTreeGrid;

        constructor(className?: string) {
            super(className);

            this.marketAppsTreeGrid = new MarketAppsTreeGrid();

            this.appendChild(this.marketAppsTreeGrid);
        }
    }

}