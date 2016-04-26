module app.installation.view {


    export class MarketAppPanel extends api.ui.panel.Panel {

        private marketAppsTreeGrid: MarketAppsTreeGrid;

        private gridDataLoaded: boolean = false;

        private isGridLoadingData: boolean = false;

        private installApplications: api.application.Application[] = [];

        constructor(className?: string) {
            super(className);

            var gridInitialized = false;

            this.onShown(() => {
                if (gridInitialized) {
                    return;
                }

                this.marketAppsTreeGrid = new MarketAppsTreeGrid();
                this.marketAppsTreeGrid.updateInstallApplications(this.installApplications);
                this.marketAppsTreeGrid.onLoaded(this.dataLoadListener.bind(this));
                this.appendChild(this.marketAppsTreeGrid);
                gridInitialized = true;
            });
        }

        public updateInstallApplications(installApplications: api.application.Application[]) {
            this.installApplications = installApplications;
        }

        public loadGrid() {
            if (this.isGridLoadingData) {
                return;
            }
            this.isGridLoadingData = true;
            this.marketAppsTreeGrid.reload().then(() => {
                this.isGridLoadingData = false;
                this.marketAppsTreeGrid.getGrid().resizeCanvas();
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
        }

        public getMarketAppsTreeGrid(): MarketAppsTreeGrid {
            return this.marketAppsTreeGrid;
        }
    }

}