module app.installation.view {

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;
    import ElementFromHelperBuilder = api.dom.ElementFromHelperBuilder;

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

    import ApplicationEvent = api.application.ApplicationEvent;
    import ApplicationEventType = api.application.ApplicationEventType;

    import MarketApplicationsFetcher = api.application.MarketApplicationsFetcher;
    import MarketApplicationResponse = api.application.MarketApplicationResponse;
    import MarketApplicationBuilder = api.application.MarketApplicationBuilder;

    export class MarketAppsTreeGrid extends TreeGrid<MarketApplication> {

        static MAX_FETCH_SIZE: number = 10;

        private installApplications: api.application.Application[];

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
                ]).setPartialLoadEnabled(true).setLoadBufferSize(2).
                    setCheckableRows(false).
                    setShowToolbar(false).
                    setRowHeight(70).
                    disableMultipleSelection(true).
                    prependClasses("market-app-tree-grid").setSelectedCellCssClass("selected-sort-row").setQuietErrorHandling(
                true).setAutoLoad(false)
            );

            this.installApplications = [];

            this.subscribeAndManageInstallClick();
            this.subscribeOnUninstallEvent();
            this.subscribeOnInstallEvent();
        }

        private subscribeOnUninstallEvent() { // set status of market app to NOT_INSTALLED if it was uninstalled
            api.application.ApplicationEvent.on((event: ApplicationEvent) => {
                if (ApplicationEventType.UNINSTALLED == event.getEventType()) {
                    var nodeToUpdate = this.getRoot().getCurrentRoot().findNode(event.getApplicationKey().toString());
                    if (!!nodeToUpdate) {
                        (<MarketApplication>nodeToUpdate.getData()).setStatus(MarketAppStatus.NOT_INSTALLED);
                        this.refresh();
                    }
                }
            });
        }

        private subscribeOnInstallEvent() { // update status of market app
            api.application.ApplicationEvent.on((event: ApplicationEvent) => {
                if (ApplicationEventType.INSTALLED == event.getEventType()) {
                    var nodeToUpdate = this.getRoot().getCurrentRoot().findNode(event.getApplicationKey().toString());
                    if (!!nodeToUpdate) {
                        new api.application.GetApplicationRequest(event.getApplicationKey(),
                            true).sendAndParse().then((application: api.application.Application)=> {
                                if (!!application) {
                                    var marketApplication: MarketApplication = <MarketApplication>nodeToUpdate.getData();
                                    if (MarketApplicationsFetcher.installedAppCanBeUpdated(marketApplication, application)) {
                                        marketApplication.setStatus(MarketAppStatus.OLDER_VERSION_INSTALLED);
                                    } else {
                                        marketApplication.setStatus(MarketAppStatus.INSTALLED);
                                    }
                                    this.refresh();
                                }
                            });
                    }
                }
            });
        }

        private subscribeAndManageInstallClick() {
            this.getGrid().subscribeOnClick((event, data) => {
                var node = this.getItem(data.row),
                    app = <MarketApplication>node.getData(),
                    url = app.getLatestVersionDownloadUrl(),
                    elem = new Element(new ElementFromHelperBuilder().setHelper(new ElementHelper(event.target))),
                    status = app.getStatus();

                if ((elem.hasClass(MarketAppStatusFormatter.statusInstallCssClass) ||
                     elem.hasClass(MarketAppStatusFormatter.statusUpdateCssClass))) {

                    var progressBar = new api.ui.ProgressBar(0);
                    var progressHandler = (event) => {
                        if (event.getApplicationUrl() == url &&
                            event.getEventType() == api.application.ApplicationEventType.PROGRESS) {

                            progressBar.setValue(event.getProgress());
                        }
                    };

                    api.application.ApplicationEvent.on(progressHandler);
                    elem.removeChildren().appendChild(progressBar);

                    new api.application.InstallUrlApplicationRequest(url)
                        .sendAndParse().then((result: api.application.ApplicationInstallResult)=> {
                        api.application.ApplicationEvent.un(progressHandler);
                        if (!result.getFailure()) {

                            elem.removeClass(MarketAppStatusFormatter.statusInstallCssClass + " " +
                                             MarketAppStatusFormatter.statusUpdateCssClass);
                            elem.addClass(MarketAppStatusFormatter.getStatusCssClass(MarketAppStatus.INSTALLED));

                            elem.setHtml(MarketAppStatusFormatter.formatStatus(MarketAppStatus.INSTALLED));
                            app.setStatus(MarketAppStatus.INSTALLED);
                        } else {
                            elem.setHtml(MarketAppStatusFormatter.formatStatus(status));
                        }

                    }).catch((reason: any) => {
                        api.application.ApplicationEvent.un(progressHandler);
                        elem.setHtml(MarketAppStatusFormatter.formatStatus(status));

                        api.DefaultErrorHandler.handle(reason);
                    });
                }
            });
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<MarketApplication>) {
            let data = node.getData();
            if (!!data.getAppKey()) {
                var viewer: MarketAppViewer = <MarketAppViewer>node.getViewer("name");
                if (!viewer) {
                    viewer = new MarketAppViewer();
                    viewer.setObject(node.getData(), node.calcLevel() > 1);
                    node.setViewer("name", viewer);
                }
                return viewer.toString();
            } else { // `load more` node
                var application = new api.dom.DivEl("children-to-load"),
                    parent = node.getParent();
                application.setHtml((parent.getMaxChildren() - parent.getChildren().length + 1) + " children left to load.");

                return application.toString();
            }
        }

        private appStatusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<MarketApplication>) {
            let data = node.getData();
            let statusWrapper = new api.dom.DivEl();

            if (!!data.getAppKey()) {

                let status = node.getData().getStatus();

                statusWrapper.setHtml(MarketAppStatusFormatter.formatStatus(status));
                statusWrapper.addClass(MarketAppStatusFormatter.getStatusCssClass(status));
            }

            return statusWrapper.toString();
        }

        sortNodeChildren(node: TreeNode<MarketApplication>) {
            this.initData(this.getRoot().getCurrentRoot().treeToList());
        }

        updateInstallApplications(installApplications: api.application.Application[]) {
            this.installApplications = installApplications;
        }

        fetchChildren(): wemQ.Promise<MarketApplication[]> {
            let root = this.getRoot().getCurrentRoot();
            let children = root.getChildren();
            var from = root.getChildren().length;
            if (from > 0 && !children[from - 1].getData().getAppKey()) {
                children.pop();
                from--;
            }

            return MarketApplicationsFetcher.fetchChildren(this.getVersion(), this.installApplications, from,
                MarketAppsTreeGrid.MAX_FETCH_SIZE).then(
                (data: MarketApplicationResponse) => {
                    let meta = data.getMetadata();
                    let applications = children.map((el) => {
                        return el.getData();
                    }).slice(0, from).concat(data.getApplications());
                    root.setMaxChildren(meta.getTotalHits());
                    if (from + meta.getHits() < meta.getTotalHits()) {
                        let emptyApplication = new MarketApplicationBuilder().setLatestVersion("").build();
                        applications.push(emptyApplication);
                    }
                    return applications;
                }).catch((reason: any) => {
                    var status500Message = "Woops... The server seems to be experiencing problems. Please try again later.";
                    var defaultErrorMessage = "Enonic Market is temporarily unavailable. Please try again later.";
                this.handleError(reason, reason.getStatusCode() === 500 ? status500Message : defaultErrorMessage);
                    return [];
                });
        }

        private getVersion(): string {
            let version: string = CONFIG.xpVersion;
            if (!version) {
                return '';
            }
            let parts = version.split('.');
            if (parts.length > 3) {
                parts.pop(); // remove '.snapshot'
                return parts.join('.');
            }
            return version;
        }

        getDataId(data: MarketApplication): string {
            return data.getAppKey() ? data.getAppKey().toString() : "";
        }
    }
}