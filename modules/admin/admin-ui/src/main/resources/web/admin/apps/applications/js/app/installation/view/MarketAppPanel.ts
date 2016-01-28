module app.installation.view {


    export class MarketAppPanel extends api.ui.panel.Panel {

        private marketAppsTreeGrid: MarketAppsTreeGrid;
        private gridInitialized: boolean = false;
        public mask: api.ui.mask.LoadMask;

        constructor(className?: string) {
            super(className);

            this.mask = new api.ui.mask.LoadMask(this);
            this.appendChild(this.mask);

            this.onShown(() => {
                if (!this.gridInitialized) {
                    this.mask.show();
                    this.marketAppsTreeGrid = new MarketAppsTreeGrid();
                    this.appendChild(this.marketAppsTreeGrid);
                    var loadListener: () => void = () => {
                        this.gridInitialized = true;
                        this.mask.hide();
                        this.marketAppsTreeGrid.unLoaded(loadListener);
                        setTimeout(() => {
                            this.marketAppsTreeGrid.refresh();// this helps to show default app icon if one provided in json fails to upload
                        }, 500);

                    };
                    this.marketAppsTreeGrid.onLoaded(loadListener);
                }
            });
        }
    }

}