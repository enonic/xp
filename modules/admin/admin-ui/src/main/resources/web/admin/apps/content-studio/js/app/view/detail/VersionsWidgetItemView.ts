module app.view.detail {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class VersionsWidgetItemView extends WidgetItemView {

        private allContentVersionsView: app.view.AllContentVersionsView;

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
                this.allContentVersionsView = new app.view.AllContentVersionsView();
                this.allContentVersionsView.onLoaded(() => {
                    if (this.gridLoadDeferred) {
                        this.gridLoadDeferred.resolve(null);
                        this.gridLoadDeferred = null;
                    }
                });

                this.appendChild(this.allContentVersionsView);
            });
        }

        public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
            if (VersionsWidgetItemView.debug) {
                console.debug('VersionsWidgetItemView.setItem: ', item);
            }

            if (this.allContentVersionsView) {
                this.allContentVersionsView.setContentData(item);
                return this.reloadActivePanel();
            }
            return wemQ<any>(null);
        }

        public reloadActivePanel(): wemQ.Promise<any> {
            if (VersionsWidgetItemView.debug) {
                console.debug('VersionsWidgetItemView.reloadActivePanel');
            }

            this.gridLoadDeferred = wemQ.defer<any>();
            if (this.allContentVersionsView) {
                this.allContentVersionsView.reload();
            } else {
                this.gridLoadDeferred.resolve(null);
            }
            return this.gridLoadDeferred.promise;
        }

    }

}