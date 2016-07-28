import "../../../api.ts";
import {MarketAppsTreeGrid} from "./MarketAppsTreeGrid";

import Application = api.application.Application;

export class MarketAppPanel extends api.ui.panel.Panel {

    private marketAppsTreeGrid: MarketAppsTreeGrid;

    private gridDataLoaded: boolean = false;

    private isGridLoadingData: boolean = false;

    private installApplications: Application[] = [];

    constructor(className?: string) {
        super(className);
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered) => {

            this.marketAppsTreeGrid = new MarketAppsTreeGrid();
            this.marketAppsTreeGrid.updateInstallApplications(this.installApplications);
            this.appendChild(this.marketAppsTreeGrid);

            this.initDataLoadListener();

            this.initAvailableSizeChangeListener();

            return rendered;
        });
    }

    private initDataLoadListener() {
        var firstLoadListener = () => {
            if (this.marketAppsTreeGrid.getGrid().getDataView().getLength() > 0) {
                this.marketAppsTreeGrid.unLoaded(firstLoadListener);
                setTimeout(() => {
                    if (!this.gridDataLoaded) {
                        this.gridDataLoaded = true;
                        this.marketAppsTreeGrid.refresh();// this helps to show default app icon if one provided in json fails to upload
                    }
                }, 500);
            }
        };

        this.marketAppsTreeGrid.onLoaded(firstLoadListener);
    }

    public updateInstallApplications(installApplications: api.application.Application[]) {
        this.installApplications = installApplications;
    }

    private initAvailableSizeChangeListener() {
        api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
            this.marketAppsTreeGrid.getGrid().resizeCanvas();
        });
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

    public getMarketAppsTreeGrid(): MarketAppsTreeGrid {
        return this.marketAppsTreeGrid;
    }
}
