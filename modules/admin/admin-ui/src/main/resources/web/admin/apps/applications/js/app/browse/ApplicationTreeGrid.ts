import "../../api.ts";
import {ApplicationBrowseActions} from "./ApplicationBrowseActions";

import GridColumn = api.ui.grid.GridColumn;
import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

import Application = api.application.Application;
import ApplicationViewer = api.application.ApplicationViewer;
import TreeGrid = api.ui.treegrid.TreeGrid;
import TreeNode = api.ui.treegrid.TreeNode;
import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

import UploadItem = api.ui.uploader.UploadItem;

export class ApplicationTreeGrid extends TreeGrid<Application> {

    constructor() {
        super(new TreeGridBuilder<Application>().setColumns([
                new GridColumnBuilder<TreeNode<Application>>().setName("Name").setId("displayName").setField("displayName").setFormatter(
                    this.nameFormatter).setMinWidth(250).build(),

                new GridColumnBuilder<TreeNode<Application>>().setName("Version").setId("version").setField("version").setCssClass(
                    "version").setMinWidth(50).setMaxWidth(70).build(),

                new GridColumnBuilder<TreeNode<Application>>().setName("State").setId("state").setField("state").setCssClass(
                    "state").setMinWidth(80).setMaxWidth(100).setFormatter(this.stateFormatter).build(),

                new GridColumnBuilder<TreeNode<Application>>().setName("ModifiedTime").setId("modifiedTime").setField(
                    "modifiedTime").setCssClass("modified").setMinWidth(150).setMaxWidth(170).setFormatter(DateTimeFormatter.format).build()

            ]).prependClasses("application-grid").setShowContextMenu(new TreeGridContextMenu(new ApplicationBrowseActions(this)))
        );

        api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
            this.getGrid().resizeCanvas();
        });
    }

    private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Application>) {
        var viewer = <ApplicationViewer>node.getViewer("name");
        if (!viewer) {
            var viewer = new ApplicationViewer();
            viewer.setObject(node.getData());
            node.setViewer("name", viewer);
        }
        return viewer.toString();
    }

    private stateFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Application>) {
        var data = node.getData(),
            status,
            statusEl = new api.dom.DivEl();

        if (data instanceof Application) {   // default node
            statusEl.getEl().setText(value);
        } else if (data instanceof ApplicationUploadMock) {   // uploading node
            status = new api.ui.ProgressBar((<any>data).getUploadItem().getProgress())
            statusEl.appendChild(status);
        }

        return statusEl.toString();
    }

    getDataId(data: Application): string {
        return data.getId();
    }

    fetchRoot(): wemQ.Promise<Application[]> {
        return new api.application.ListApplicationsRequest().sendAndParse();
    }

    fetch(node: TreeNode<Application>, dataId?: string): wemQ.Promise<api.application.Application> {
        return this.fetchByKey(node.getData().getApplicationKey());
    }

    private fetchByKey(applicationKey: api.application.ApplicationKey): wemQ.Promise<api.application.Application> {
        var deferred = wemQ.defer<api.application.Application>();
        new api.application.GetApplicationRequest(applicationKey,
            true).sendAndParse().then((application: api.application.Application)=> {
            deferred.resolve(application);
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });

        return deferred.promise;
    }

    updateApplicationNode(applicationKey: api.application.ApplicationKey) {
        var root = this.getRoot().getCurrentRoot();
        root.getChildren().forEach((child: TreeNode<Application>) => {
            var curApplication: Application = child.getData();
            if (curApplication.getApplicationKey().toString() == applicationKey.toString()) {
                this.updateNode(curApplication);
            }
        });
    }

    getByApplicationKey(applicationKey: api.application.ApplicationKey): Application {
        var root = this.getRoot().getCurrentRoot(),
            result;
        root.getChildren().forEach((child: TreeNode<Application>) => {
            var curApplication: Application = child.getData();
            if (curApplication.getApplicationKey().toString() == applicationKey.toString()) {
                result = curApplication;
            }
        });
        return result;
    }

    deleteApplicationNode(applicationKey: api.application.ApplicationKey) {
        var root = this.getRoot().getCurrentRoot();
        root.getChildren().forEach((child: TreeNode<Application>) => {
            var curApplication: Application = child.getData();
            if (curApplication.getApplicationKey().toString() == applicationKey.toString()) {
                this.deleteNode(curApplication);
            }
        });
    }

    appendApplicationNode(applicationKey: api.application.ApplicationKey): wemQ.Promise<void> {

        return this.fetchByKey(applicationKey)
            .then((data: api.application.Application) => {
                return this.appendNode(data, true);
            });
    }

    refreshNodeData(parentNode: TreeNode<Application>): wemQ.Promise<TreeNode<Application>> {
        return this.fetchByKey(parentNode.getData().getApplicationKey()).then((curApplication: Application) => {
            parentNode.setData(curApplication);
            this.refreshNode(parentNode);
            return parentNode;
        });
    }

    appendUploadNode(item: api.ui.uploader.UploadItem<Application>) {

        var appMock: ApplicationUploadMock = new ApplicationUploadMock(item);

        this.appendNode(<any>appMock, false).then(() => {

        }).done();

        item.onProgress((progress: number) => {
            this.invalidate();
        });
        item.onUploaded((model: Application) => {
            var nodeToRemove = this.getRoot().getCurrentRoot().findNode(item.getId());
            if (nodeToRemove) {
                this.deleteNode(nodeToRemove.getData());
                this.invalidate();
                this.triggerSelectionChangedListeners();
            }
        });
        item.onFailed(() => {
            this.deleteNode(<any>appMock);
            this.triggerSelectionChangedListeners();
        })
    }

}

export class ApplicationUploadMock {

    private id: string;
    private name: string;
    private uploadItem: UploadItem<Application>;

    constructor(uploadItem: UploadItem<Application>) {
        this.id = uploadItem.getId();
        this.name = uploadItem.getName();
        this.uploadItem = uploadItem;
    }

    getId(): string {
        return this.id;
    }

    getDisplayName(): string {
        return this.name;
    }

    getName(): string {
        return this.name;
    }

    getUploadItem(): UploadItem<Application> {
        return this.uploadItem;
    }

    getApplicationKey(): string {
        return this.name;
    }

    isLocal(): boolean {
        return false;
    }
}
