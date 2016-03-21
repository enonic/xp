module app.installation.view {


    export class MarketAppPanel extends api.ui.panel.Panel {

        private marketAppsTreeGrid: MarketAppsTreeGrid;
        private gridDataLoaded: boolean = false;
        private isGridLoadingData: boolean = false;

        constructor(className?: string) {
            super(className);

            var gridInitialized = false;

            this.onShown(() => {
                if (!gridInitialized) {
                    this.marketAppsTreeGrid = new MarketAppsTreeGrid();
                    this.marketAppsTreeGrid.onLoaded(this.dataLoadListener.bind(this));
                    this.appendChild(this.marketAppsTreeGrid);
                    this.marketAppsTreeGrid.mask();
                    this.isGridLoadingData = true;
                    gridInitialized = true;
                }
                if (!this.gridDataLoaded && !this.isGridLoadingData) {
                    this.marketAppsTreeGrid.reload();
                    this.isGridLoadingData = true;
                }
            });
        }

        private dataLoadListener() {
            if (this.marketAppsTreeGrid.getGrid().getDataView().getLength() > 0) {
                this.marketAppsTreeGrid.unLoaded(this.dataLoadListener);
                setTimeout(() => {
                    if (!this.gridDataLoaded) {
                        this.gridDataLoaded = true;
                        this.marketAppsTreeGrid.refresh();// this helps to show default app icon if one provided in json fails to upload
                    }
                }, 500);
            }
            this.isGridLoadingData = false;
            this.marketAppsTreeGrid.unmask();
        }

        public getMarketAppsTreeGrid(): MarketAppsTreeGrid {
            return this.marketAppsTreeGrid;
        }
    }

}