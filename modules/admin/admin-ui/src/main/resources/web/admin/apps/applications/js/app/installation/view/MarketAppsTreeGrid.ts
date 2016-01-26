module app.installation.view {

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

    import ContentResponse = api.content.ContentResponse;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
    import ChildOrder = api.content.ChildOrder;

    import CompareStatus = api.content.CompareStatus;

    import MarketApplication = api.application.MarketApplication;
    import Application = api.application.Application;
    import MarketAppStatus = api.application.MarketAppStatus;
    import MarketAppStatusFormatter = api.application.MarketAppStatusFormatter;

    export class MarketAppsTreeGrid extends TreeGrid<MarketApplication> {

        constructor() {

            var nameColumn = new GridColumnBuilder<TreeNode<MarketApplication>>().
                setName("Name").
                setId("displayName").
                setField("displayName").
                setCssClass("app-name-and-icon").
                setMinWidth(170).
                setFormatter(this.nameFormatter).
                build();
            var versionColumn = new GridColumnBuilder<TreeNode<MarketApplication>>().
                setName("Version").
                setId("version").
                setField("latestVersion").
                setCssClass("version").
                setMinWidth(40).
                build();
            var appStatusColumns = new GridColumnBuilder<TreeNode<MarketApplication>>().
                setName("AppStatus").
                setId("appStatus").
                setField("status").
                setCssClass("status").
                setMinWidth(50).
                setFormatter(this.appStatusFormatter).
                setCssClass("app-status").
                build();

            super(new TreeGridBuilder<MarketApplication>().
                    setColumns([
                        nameColumn,
                        versionColumn,
                        appStatusColumns
                    ]).
                    setPartialLoadEnabled(false).
                    setCheckableRows(false).
                    setShowToolbar(false).
                    setRowHeight(70).
                    disableMultipleSelection(true).
                    prependClasses("market-app-tree-grid").
                    setSelectedCellCssClass("selected-sort-row")
            );

            this.subsribeAndManageInstallClick();
        }

        private subsribeAndManageInstallClick() {
            this.getGrid().subscribeOnClick((event, data) => {
                var node = this.getItem(data.row),
                    app = <MarketApplication>node.getData(),
                    elem = new ElementHelper(event.target),
                    status = app.getStatus();

                if ((elem.hasClass(MarketAppStatusFormatter.statusInstallCssClass) ||
                     elem.hasClass(MarketAppStatusFormatter.statusUpdateCssClass))) {
                    elem.setInnerHtml("");
                    elem.addClass("spinner");
                    new api.application.InstallUrlApplicationRequest(node.getData().getLatestVersionDownloadUrl()).sendAndParse().then((application: api.application.Application)=> {
                        elem.removeClass("spinner " + MarketAppStatusFormatter.statusInstallCssClass + " " +
                                         MarketAppStatusFormatter.statusUpdateCssClass);
                        elem.addClass(MarketAppStatusFormatter.getStatusCssClass(MarketAppStatus.INSTALLED));
                        elem.setInnerHtml(MarketAppStatusFormatter.formatStatus(MarketAppStatus.INSTALLED));
                        app.setStatus(MarketAppStatus.INSTALLED);
                        api.notify.showFeedback("Application " + app.getDisplayName() + " " +
                                                MarketAppStatusFormatter.formatPerformedAction(status) + " successfully");
                    }).catch((reason: any) => {
                        elem.removeClass("spinner");
                        elem.setInnerHtml(MarketAppStatusFormatter.formatStatus(status));
                        api.DefaultErrorHandler.handle(reason);
                    });
                }
            });
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<MarketApplication>) {

            var viewer: MarketAppViewer = <MarketAppViewer>node.getViewer("name");
            if (!viewer) {
                viewer = new MarketAppViewer();
                viewer.setObject(node.getData(), node.calcLevel() > 1);
                node.setViewer("name", viewer);
            }
            return viewer.toString();
        }

        private appStatusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<MarketApplication>) {
            var status = node.getData().getStatus(),
                statusWrapper = new api.dom.DivEl()

            statusWrapper.setHtml(MarketAppStatusFormatter.formatStatus(status));
            statusWrapper.addClass(MarketAppStatusFormatter.getStatusCssClass(status));

            return statusWrapper.toString();
        }

        sortNodeChildren(node: TreeNode<MarketApplication>) {
            this.initData(this.getRoot().getCurrentRoot().treeToList());
        }


        fetchChildren(): wemQ.Promise<MarketApplication[]> {
            return new api.application.ListMarketApplicationsRequest().sendAndParse().then((applications: MarketApplication[])=> {
                return this.setMarketAppsStatuses(applications);
            });
        }

        private setMarketAppsStatuses(marketApplications: MarketApplication[]): wemQ.Promise<MarketApplication[]> {
            return new api.application.ListApplicationsRequest().sendAndParse().then((installedApplications: Application[]) => {
                marketApplications.forEach((marketApp) => {
                    for (var i = 0; i < installedApplications.length; i++) {
                        if (marketApp.getAppKey().equals(installedApplications[i].getApplicationKey())) {
                            if (this.installedAppCanBeUpdated(marketApp, installedApplications[i])) {
                                marketApp.setStatus(MarketAppStatus.OLDER_VERSION_INSTALLED);
                            } else {
                                marketApp.setStatus(MarketAppStatus.INSTALLED);
                            }
                            break;
                        }
                    }
                });
                return marketApplications;
            });
        }

        private installedAppCanBeUpdated(marketApp: MarketApplication, installedApp: Application): boolean {
            return this.compareVersionNumbers(marketApp.getLatestVersion(), installedApp.getVersion()) > 0;
        }

        compareVersionNumbers(v1: string, v2: string): number {
            var v1parts = v1.split('.');
            var v2parts = v2.split('.');

            for (var i = 0; i < v1parts.length; ++i) {
                if (v2parts.length === i) {
                    return 1;
                }

                if (v1parts[i] === v2parts[i]) {
                    continue;
                }
                if (v1parts[i] > v2parts[i]) {
                    return 1;
                }
                return -1;
            }

            if (v1parts.length != v2parts.length) {
                return -1;
            }

            return 0;
        }


        getDataId(data: MarketApplication): string {
            return data.getApplicationUrl();
        }
    }
}