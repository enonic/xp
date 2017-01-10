import "../../api.ts";
import {ApplicationBrowseActions} from "./ApplicationBrowseActions";
import {ApplicationRowFormatter} from "./ApplicationRowFormatter";

import GridColumn = api.ui.grid.GridColumn;
import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

import Application = api.application.Application;
import ApplicationViewer = api.application.ApplicationViewer;
import ApplicationUploadMock = api.application.ApplicationUploadMock;
import TreeGrid = api.ui.treegrid.TreeGrid;
import TreeNode = api.ui.treegrid.TreeNode;
import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

import UploadItem = api.ui.uploader.UploadItem;
import ApplicationKey = api.application.ApplicationKey;

export class ApplicationTreeGrid extends TreeGrid<Application> {

    constructor() {
        super(new TreeGridBuilder<Application>().setColumnConfig([{
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: ApplicationRowFormatter.nameFormatter,
                style: {minWidth: 250}
            }, {
                name: "Version",
                id: "version",
                field: "version",
                style: {cssClass: "version", minWidth: 50, maxWidth: 130}
            }, {
                name: "State",
                id: "state",
                field: "state",
                formatter: ApplicationRowFormatter.stateFormatter,
                style: {cssClass: "state", minWidth: 80, maxWidth: 100}
            }]).prependClasses("application-grid")
        );

        this.setContextMenu(new TreeGridContextMenu(ApplicationBrowseActions.init(this)));

        this.initEventHandlers();
    }

    private initEventHandlers() {
        api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, () => {
            this.getGrid().resizeCanvas();
        });
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
        let deferred = wemQ.defer<api.application.Application>();
        new api.application.GetApplicationRequest(applicationKey,
            true).sendAndParse().then((application: api.application.Application)=> {
            deferred.resolve(application);
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });

        return deferred.promise;
    }

    fetchRootKeys(): wemQ.Promise<ApplicationKey[]> {
        return new api.application.ListApplicationKeysRequest().sendAndParse();
    }

    placeNode(data: Application, stashedParentNode?: TreeNode<Application>): wemQ.Promise<void> {
        const parentNode = this.getParentNode(true, stashedParentNode);
        let index = parentNode.getChildren().length;
        for (let i = 0; i < index; i++) {
            if (parentNode.getChildren()[i].getData().getDisplayName().localeCompare(data.getDisplayName()) >= 0) {
                index = i;
                break;
            }
        }
        return this.insertNode(data, true, index, stashedParentNode);
    }

    updateApplicationNode(applicationKey: api.application.ApplicationKey) {
        let root = this.getRoot().getCurrentRoot();
        root.getChildren().forEach((child: TreeNode<Application>) => {
            let curApplication: Application = child.getData();
            if (curApplication.getApplicationKey().toString() == applicationKey.toString()) {
                this.updateNode(curApplication);
            }
        });
    }

    getByApplicationKey(applicationKey: api.application.ApplicationKey): Application {
        let root = this.getRoot().getCurrentRoot(),
            result;
        root.getChildren().forEach((child: TreeNode<Application>) => {
            let curApplication: Application = child.getData();
            if (curApplication.getApplicationKey().toString() == applicationKey.toString()) {
                result = curApplication;
            }
        });
        return result;
    }

    deleteApplicationNode(applicationKey: api.application.ApplicationKey) {
        let root = this.getRoot().getCurrentRoot();
        root.getChildren().forEach((child: TreeNode<Application>) => {
            let curApplication: Application = child.getData();
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

    placeApplicationNode(applicationKey: api.application.ApplicationKey): wemQ.Promise<void> {
        return this.fetchByKey(applicationKey)
            .then((data: api.application.Application) => {
                return this.placeNode(data);
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

        let appMock: ApplicationUploadMock = new ApplicationUploadMock(item);

        this.appendNode(<any>appMock, false).done();

        let deleteUploadedNodeHandler = () => {
            let nodeToRemove = this.getRoot().getCurrentRoot().findNode(item.getId());
            if (nodeToRemove) {
                this.deleteNode(nodeToRemove.getData());
                this.invalidate();
                this.triggerSelectionChangedListeners();
            }
        };

        item.onProgress((progress: number) => {
            this.invalidate();
        });

        item.onUploaded(deleteUploadedNodeHandler);

        item.onUploadStopped(deleteUploadedNodeHandler);

        item.onFailed(() => {
            this.deleteNode(<any>appMock);
            this.triggerSelectionChangedListeners();
        });
    }

}
