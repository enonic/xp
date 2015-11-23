module app.view.detail {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class VersionsWidgetItemView extends WidgetItemView {

        private allGrid: ContentVersionsTreeGrid;

        private gridLoadDeferred: wemQ.Deferred<any>;

        public static debug = false;

        constructor() {
            super("version-widget-item-view");
        }

        public layout(): wemQ.Promise<any> {
            if (VersionsWidgetItemView.debug) {
                console.debug('VersionsWidgetItemView.layout');
            }
            this.removeChildren();

            return super.layout().then(() => {
                this.allGrid = new AllContentVersionsTreeGrid();
                this.allGrid.onLoaded(() => {
                    if (this.gridLoadDeferred) {
                        this.gridLoadDeferred.resolve(null);
                        this.gridLoadDeferred = null;
                    }
                });

                this.appendChild(this.allGrid);
            });
        }

        public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
            if (VersionsWidgetItemView.debug) {
                console.debug('VersionsWidgetItemView.setItem: ', item);
            }

            if (this.allGrid) {
                this.allGrid.setItem(item);
                return this.reloadActivePanel();
            }
            return wemQ<any>(null);
        }


        public invalidateActivePanel() {
            if (this.allGrid) {
                this.allGrid.getGrid().invalidate();
            }
        }

        public reloadActivePanel(): wemQ.Promise<any> {
            if (VersionsWidgetItemView.debug) {
                console.debug('VersionsWidgetItemView.reloadActivePanel');
            }

            this.gridLoadDeferred = wemQ.defer<any>();
            if (this.allGrid) {
                this.allGrid.reload();
            } else {
                this.gridLoadDeferred.resolve(null);
            }
            return this.gridLoadDeferred.promise;
        }

    }

}