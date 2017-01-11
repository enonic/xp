import "../../../api.ts";
import {MarketAppViewer} from "./MarketAppViewer";
import {ApplicationInput} from "./../view/ApplicationInput";

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

import ContentResponse = api.content.resource.result.ContentResponse;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
import ContentSummaryViewer = api.content.ContentSummaryViewer;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;

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
import ProgressBar = api.ui.ProgressBar;

declare var CONFIG;

export class MarketAppsTreeGrid extends TreeGrid<MarketApplication> {

    static MAX_FETCH_SIZE: number = 10;

    private installApplications: Application[];

    public static debug: boolean = false;

    private applicationInput: ApplicationInput;

    constructor(applicationInput: ApplicationInput) {

        var nameColumn = new GridColumnBuilder<TreeNode<MarketApplication>>()
            .setName("Name")
            .setId("displayName")
            .setField("displayName")
            .setCssClass("app-name-and-icon")
            .setMinWidth(170)
            .setFormatter(MarketAppsTreeGrid.nameFormatter)
            .build();
        var versionColumn = new GridColumnBuilder<TreeNode<MarketApplication>>()
            .setName("Version")
            .setId("version")
            .setField("latestVersion")
            .setCssClass("version")
            .setMinWidth(40)
            .build();
        var appStatusColumns = new GridColumnBuilder<TreeNode<MarketApplication>>()
            .setName("AppStatus")
            .setId("appStatus")
            .setField("status")
            .setCssClass("status")
            .setMinWidth(50)
            .setFormatter(MarketAppsTreeGrid.appStatusFormatter)
            .setCssClass("app-status").build();

        super(new TreeGridBuilder<MarketApplication>()
            .setColumns([
                nameColumn,
                versionColumn,
                appStatusColumns
            ])
            .setPartialLoadEnabled(true)
            .setLoadBufferSize(2)
            .setCheckableRows(false)
            .setShowToolbar(false)
            .setRowHeight(70)
            .disableMultipleSelection(true)
            .prependClasses("market-app-tree-grid")
            .setSelectedCellCssClass("selected-sort-row")
            .setQuietErrorHandling(true)
            .setAutoLoad(false)
        );

        this.installApplications = [];
        this.applicationInput = applicationInput;

        this.subscribeAndManageInstallClick();
        this.subscribeOnUninstallEvent();
        this.subscribeOnInstallEvent();
        this.initAppsFilter();
    }

    private initAppsFilter() {
        var changeHandler = () => {
            this.refresh();
        };
        this.applicationInput.onTextValueChanged(changeHandler);
        this.applicationInput.getTextInput().onValueChanged(() => {
            this.mask();
        });
        this.applicationInput.onAppInstallFinished(() => {
            this.unmask();
        });
        this.applicationInput.getTextInput().getHTMLElement().onpaste = () => {
            this.mask();
        }
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

    private findNodeByAppUrl(url: string): TreeNode<MarketApplication> {
        let nodes: TreeNode<MarketApplication>[] = this.getGrid().getDataView().getItems();
        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            if (node.getData().getLatestVersionDownloadUrl() == url) {
                return node;
            }
        }
        return null
    }

    private subscribeOnInstallEvent() { // update status of market app
        api.application.ApplicationEvent.on((event: ApplicationEvent) => {

            let nodeToUpdate;

            if (MarketAppsTreeGrid.debug) {
                console.debug("MarketAppsTreeGrid: app event", event.getEventType(), event.getProgress());
            }

            switch (event.getEventType()) {
            case ApplicationEventType.PROGRESS:

                //TODO: send appKey from backend instead of looking for it!
                nodeToUpdate = this.findNodeByAppUrl(event.getApplicationUrl());
                if (!!nodeToUpdate) {

                    if (MarketAppsTreeGrid.debug) {
                        console.debug("MarketAppsTreeGrid: progress", event.getApplicationUrl(), event.getProgress());
                    }

                    let app = <MarketApplication>nodeToUpdate.getData();
                    app.setProgress(event.getProgress());

                    let row = this.getGrid().getDataView().getRowById(nodeToUpdate.getId());
                    if (row > -1) {
                        let cell = this.getGrid().getColumnIndex("appStatus");
                        this.getGrid().updateCell(row, cell);
                    }
                }
                break;
            case ApplicationEventType.INSTALLED:

                nodeToUpdate = this.getRoot().getCurrentRoot().findNode(event.getApplicationKey().toString());
                if (!!nodeToUpdate) {

                    if (MarketAppsTreeGrid.debug) {
                        console.debug("MarketAppsTreeGrid: installed", event.getApplicationUrl(), event.getProgress());
                    }

                    let app = <MarketApplication>nodeToUpdate.getData();
                    app.setStatus(MarketAppStatus.INSTALLED);

                    new api.application.GetApplicationRequest(event.getApplicationKey(), true).sendAndParse()
                        .then((application: api.application.Application)=> {
                            if (!!application) {
                                var marketApplication: MarketApplication = <MarketApplication>nodeToUpdate.getData();

                                if (MarketApplicationsFetcher.installedAppCanBeUpdated(marketApplication, application)) {
                                    marketApplication.setStatus(MarketAppStatus.OLDER_VERSION_INSTALLED);
                                } else {
                                    marketApplication.setStatus(MarketAppStatus.INSTALLED);
                                }
                                let row = this.getGrid().getDataView().getRowById(nodeToUpdate.getId());
                                if (row > -1) {
                                    this.getGrid().updateRow(row);
                                }
                            }
                        });
                }
                break;
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

                this.mask();

                app.setStatus(MarketAppStatus.INSTALLING);

                let row = this.getGrid().getDataView().getRowById(node.getId());
                if (row > -1) {
                    this.getGrid().updateCell(row, this.getGrid().getColumnIndex("appStatus"))
                }

                if (MarketAppsTreeGrid.debug) {
                    console.debug("MarketAppsTreeGrid: starting install", url, elem);
                }

                new api.application.InstallUrlApplicationRequest(url)
                    .sendAndParse().then((result: api.application.ApplicationInstallResult)=> {
                    // api.application.ApplicationEvent.un(progressHandler);
                    if (!result.getFailure()) {

                        elem.removeClass(MarketAppStatusFormatter.statusInstallCssClass + " " +
                                         MarketAppStatusFormatter.statusUpdateCssClass);
                        elem.addClass(MarketAppStatusFormatter.getStatusCssClass(MarketAppStatus.INSTALLED));

                    } else {
                        elem.setHtml(MarketAppStatusFormatter.formatStatus(status));
                    }
                    this.unmask();

                }).catch((reason: any) => {
                    this.unmask();
                    elem.setHtml(MarketAppStatusFormatter.formatStatus(status));
                    api.DefaultErrorHandler.handle(reason);
                });
            }
        });
    }

    public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<MarketApplication>) {
        const data = <MarketApplication>node.getData();

        if (data.getAppKey()) {
            var viewer: MarketAppViewer = <MarketAppViewer>node.getViewer("name");
            if (!viewer) {
                viewer = new MarketAppViewer();
                viewer.setObject(data, node.calcLevel() > 1);
                node.setViewer("name", viewer);
            }
            return viewer.toString();
        }

        return "";
    }

    isEmptyNode(node: TreeNode<MarketApplication>): boolean {
        const data = node.getData();
        return !data.getAppKey();
    }

    public static appStatusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<MarketApplication>) {
        let app = <MarketApplication>node.getData();
        let statusWrapper = new api.dom.AEl();

        if (!!app.getAppKey()) {

            let status = app.getStatus();
            let progress = app.getProgress();

            statusWrapper.setHtml(MarketAppStatusFormatter.formatStatus(status, progress), false);
            statusWrapper.addClass(MarketAppStatusFormatter.getStatusCssClass(status));

            if (status != MarketAppStatus.NOT_INSTALLED && status != MarketAppStatus.OLDER_VERSION_INSTALLED) {
                statusWrapper.getEl().setTabIndex(-1);
            }
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

        if (this.getErrorPanel().isVisible()) {
            this.hideErrorPanel();
            this.mask();
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
            var status500Message = "Woops... The server seems to be experiencing problems. Please try again later.",
                defaultErrorMessage = "Enonic Market is temporarily unavailable. Please try again later.";
            this.handleError(reason, reason.getStatusCode() === 500 ? status500Message : defaultErrorMessage);
            return [];
        });
    }

    initData(nodes: TreeNode<MarketApplication>[]) {
        var items = nodes;
        if (this.applicationInput && !api.util.StringHelper.isEmpty(this.applicationInput.getValue())) {
            items = nodes.filter((node: TreeNode<MarketApplication>) => {
                return this.nodePassesFilterCondition(node);
            });
        }
        super.initData(items);
        this.getGrid().getCanvasNode().style.height = (70 * items.length + "px");
        this.getGrid().resizeCanvas();
    }

    private nodePassesFilterCondition(node: TreeNode<MarketApplication>): boolean {
        var app: MarketApplication = node.getData();
        return app.isEmpty() ? true : this.appHasFilterEntry(app); // true for empty app because empty app is empty node that triggers loading
    }

    private appHasFilterEntry(app: MarketApplication): boolean {
        return this.applicationInput.hasMatchInEntry(app.getDisplayName()) ||
               this.applicationInput.hasMatchInEntry(app.getDescription());
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