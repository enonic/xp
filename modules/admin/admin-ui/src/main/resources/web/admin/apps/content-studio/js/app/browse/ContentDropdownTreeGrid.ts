import '../../api.ts';

import Viewer = api.ui.Viewer;
import TreeGrid = api.ui.treegrid.TreeGrid;
import TreeNode = api.ui.treegrid.TreeNode;
import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;

import ContentResponse = api.content.resource.result.ContentResponse;
import ContentPath = api.content.ContentPath;
import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
import ContentSummaryViewer = api.content.ContentSummaryViewer;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;

import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import ContentId = api.content.ContentId;
import DataChangedEvent = api.ui.treegrid.DataChangedEvent;

import ContentQueryResult = api.content.resource.result.ContentQueryResult;
import ContentSummaryJson = api.content.json.ContentSummaryJson;
import ContentQueryRequest = api.content.resource.ContentQueryRequest;
import GetContentByIdRequest = api.content.resource.GetContentByIdRequest;
import ListContentByIdRequest = api.content.resource.ListContentByIdRequest;

export class ContentDropdownTreeGrid extends TreeGrid<ContentSummary> {

    static MAX_FETCH_SIZE: number = 10;

    private filterQuery: api.content.query.ContentQuery;

    constructor() {
        let builder: TreeGridBuilder<ContentSummary> =
            new TreeGridBuilder<ContentSummary>().setColumnConfig([{
                name: 'Name',
                id: 'displayName',
                field: 'displayName',
                formatter: ContentDropdownTreeGrid.nameFormatter,
                style: {minWidth: 130}
            }]).setPartialLoadEnabled(true).setLoadBufferSize(20).// rows count
            prependClasses('content-tree-grid');

        super(builder);

        let columns = builder.getColumns();
        const nameColumn = columns[1];

        let updateColumns = (force?: boolean) => {
            if (force) {
                this.getGrid().setColumns([nameColumn]);
            } else {
                this.getGrid().resizeCanvas();
            }
        };

        this.initEventHandlers(updateColumns);
    }

    private static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummary>) {
        const data = node.getData();
        if (data) {
            let viewer: ContentSummaryViewer = <ContentSummaryViewer> node.getViewer('name');
            if (!viewer) {
                viewer = new ContentSummaryViewer();
                node.setViewer('name', viewer);
            }
            viewer.setObject(node.getData(), node.calcLevel() > 1);
            return viewer ? viewer.toString() : '';
        }

        return '';
    }

    private initEventHandlers(updateColumnsHandler: Function) {
        let onBecameActive = (active: boolean) => {
            if (active) {
                updateColumnsHandler(true);
                this.unActiveChanged(onBecameActive);
            }
        };
        // update columns when grid becomes active for the first time
        this.onActiveChanged(onBecameActive);

        api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            if (this.isInRenderingView()) {
                updateColumnsHandler(item.isRangeSizeChanged());
            }
        });

        /*
         * Filter (search) events.
         */
        /*ContentBrowseSearchEvent.on((event) => {
         let contentQueryResult = <ContentQueryResult<ContentSummary,ContentSummaryJson>>event.getContentQueryResult();
         let contentSummaries = contentQueryResult.getContents();
         let compareRequest = CompareContentRequest.fromContentSummaries(contentSummaries);
         this.filterQuery = event.getContentQuery();
         compareRequest.sendAndParse().then((compareResults: CompareContentResults) => {
         let contents: ContentSummary[] = ContentSummaryAndCompareStatusFetcher.updateCompareStatus(contentSummaries,
         compareResults);
         ContentSummaryAndCompareStatusFetcher.updateReadOnly(contents).then(() => {
         let metadata = contentQueryResult.getMetadata();
         if (metadata.getTotalHits() > metadata.getHits()) {
         contents.push(new ContentSummary());
         }
         this.filter(contents);
         this.getRoot().getCurrentRoot().setMaxChildren(metadata.getTotalHits());
         this.notifyLoaded();
         });

         }).catch((reason: any) => {
         api.DefaultErrorHandler.handle(reason);
         }).done();
         });
         */

        /*
         ContentBrowseResetEvent.on((event) => {
         this.resetFilter();
         });
         ContentBrowseRefreshEvent.on((event) => {
         this.notifyLoaded();
         });
         ContentVersionSetEvent.on((event: ContentVersionSetEvent) => {
         this.updateContentNode(event.getContentId());
         });
         */
    }

    isEmptyNode(node: TreeNode<ContentSummary>): boolean {
        return !node.getDataId() || node.getDataId() === '';
    }

    hasChildren(data: ContentSummary): boolean {
        return data.hasChildren();
    }

    getDataId(data: ContentSummary): string {
        return data.getId();
    }

    fetch(node: TreeNode<ContentSummary>, dataId?: string): wemQ.Promise<ContentSummary> {
        return this.fetchById(node.getData().getContentId());
    }

    private fetchById(id: api.content.ContentId): wemQ.Promise<ContentSummary> {
        return new GetContentByIdRequest(id).sendAndParse();
    }

    fetchChildren(parentNode?: TreeNode<ContentSummary>): wemQ.Promise<ContentSummary[]> {
        let parentContentId: api.content.ContentId = null;
        if (parentNode) {
            parentContentId = parentNode.getData() ? parentNode.getData().getContentId() : parentContentId;
        } else {
            parentNode = this.getRoot().getCurrentRoot();
        }
        let from = parentNode.getChildren().length;
        if (from > 0 && !parentNode.getChildren()[from - 1].getData()) {
            parentNode.getChildren().pop();
            from--;
        }

        if (!this.isFiltered() || parentNode !== this.getRoot().getCurrentRoot()) {
            return new ListContentByIdRequest(parentContentId).setFrom(from).setSize(
                ContentDropdownTreeGrid.MAX_FETCH_SIZE).sendAndParse().then(
                (data: ContentResponse<ContentSummary>) => {
                    // TODO: Will reset the ids and the selection for child nodes.
                    let contents = parentNode.getChildren().map((el) => {
                        return el.getData();
                    }).slice(0, from).concat(data.getContents());
                    let meta = data.getMetadata();
                    parentNode.setMaxChildren(meta.getTotalHits());
                    if (from + meta.getHits() < meta.getTotalHits()) {
                        contents.push(new ContentSummaryBuilder().build());
                    }
                    return contents;
                });
        } else {
            this.filterQuery.setFrom(from);
            this.filterQuery.setSize(ContentDropdownTreeGrid.MAX_FETCH_SIZE);
            return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(this.filterQuery).setExpand(
                api.rest.Expand.SUMMARY).sendAndParse().then(
                (contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                    return contentQueryResult.getContents();
                });
        }
    }

    fetchChildrenIds(parentNode?: TreeNode<ContentSummary>): wemQ.Promise<ContentId[]> {
        let parentContentId: api.content.ContentId = null;
        if (parentNode) {
            parentContentId = parentNode.getData() ? parentNode.getData().getContentId() : parentContentId;
        } else {
            parentNode = this.getRoot().getCurrentRoot();
        }
        let size = parentNode.getChildren().length;
        if (size > 0 && !parentNode.getChildren()[size - 1].getData()) {
            parentNode.getChildren().pop();
            size--;
        }

        if (!this.isFiltered() || parentNode !== this.getRoot().getCurrentRoot()) {
            return ContentSummaryAndCompareStatusFetcher.fetchChildrenIds(parentContentId).then(
                (response: ContentId[]) => {
                    return response;
                });
        } else {
            this.filterQuery.setFrom(0);
            this.filterQuery.setSize(size + 1);
            return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(this.filterQuery).setExpand(
                api.rest.Expand.SUMMARY).sendAndParse().then(
                (contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                    return contentQueryResult.getContents().map((content => content.getContentId()));
                });
        }
    }

    private fetchChildrenData(parentNode: TreeNode<ContentSummary>): wemQ.Promise<ContentSummary[]> {
        return this.fetchChildren(parentNode);
    }

    deleteNodes(dataList: ContentSummary[]): void {
        /*let root = this.getRoot().getCurrentRoot();
         let node: TreeNode<ContentSummary>;

         // Do not remove the items, that is not new and switched to 'PENDING_DELETE'
         dataList = dataList.filter((data) => {
         node = root.findNode(this.getDataId(data));
         if (node.getData().getCompareStatus() !== CompareStatus.NEW) {
         node.clearViewers();
         return false;
         }
         return true;
         });*/
        super.deleteNodes(dataList);
    }

    refreshNodeData(parentNode: TreeNode<ContentSummary>): wemQ.Promise<TreeNode<ContentSummary>> {
        return new GetContentByIdRequest(parentNode.getData().getContentId()).sendAndParse().then((content: Content)=> {
            parentNode.setData(content);
            this.refreshNode(parentNode);
            return parentNode;
        });
    }

    sortNodeChildren(node: TreeNode<ContentSummary>) {
        let rootNode = this.getRoot().getCurrentRoot();
        if (node !== rootNode) {
            if (node.hasChildren()) {
                node.setChildren([]);
                node.setMaxChildren(0);

                this.fetchChildren(node)
                    .then((dataList: ContentSummary[]) => {
                        let parentNode = this.getRoot().getCurrentRoot().findNode(node.getDataId());
                        parentNode.setChildren(this.dataToTreeNodes(dataList, parentNode));
                        let rootList = this.getRoot().getCurrentRoot().treeToList();
                        this.initData(rootList);
                    }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }
        }
    }

    selectAll() {
        this.getGrid().mask();
        setTimeout(() => {
            super.selectAll();
            this.getGrid().unmask();
        }, 5);
    }

    /*findByPaths(paths: api.content.ContentPath[], useParent: boolean = false): TreeNodesOfContentPath[] {
     let root = this.getRoot().getDefaultRoot().treeToList(false, false);
     let filter = this.getRoot().getFilteredRoot().treeToList(false, false);
     let all: TreeNode<ContentSummary>[] = root.concat(filter);
     let result: TreeNodesOfContentPath[] = [];
     let resultIds: string[] = [];

     for (let i = 0; i < paths.length; i++) {
     let node = useParent
     ? new TreeNodesOfContentPath(paths[i].getParentPath(), paths[i])
     : new TreeNodesOfContentPath(paths[i]);
     if (useParent && node.getPath().isRoot()) {
     node.getNodes().push(this.getRoot().getDefaultRoot());
     if (this.isFiltered()) {
     node.getNodes().push(this.getRoot().getFilteredRoot());
     }
     } else {
     for (let j = 0; j < all.length; j++) {
     let treeNode = all[j];
     let path = (treeNode.getData() && treeNode.getData().getContentSummary())
     ? treeNode.getData().getContentSummary().getPath()
     : null;
     if (path && path.equals(node.getPath())) {
     node.getNodes().push(treeNode);
     }
     }
     }
     if (node.hasNodes()) {
     if (resultIds.indexOf(node.getId()) < 0) {
     result.push(node);
     resultIds.push(node.getId());
     }
     }
     }

     return result;
     }*/

    selectNodeByPath(targetPath: ContentPath) {
        let currentSelectedNode: TreeNode<ContentSummary> = this.getSelectedNodes()[0];
        let nodeToSearchTargetIn: TreeNode<ContentSummary>;

        if (currentSelectedNode && targetPath.isDescendantOf(currentSelectedNode.getData().getPath())) {
            nodeToSearchTargetIn = currentSelectedNode;
        } else {
            nodeToSearchTargetIn = this.getRoot().getCurrentRoot();
        }

        // go down and expand path's parents level by level until we reach the desired element within the list of fetched children
        this.doSelectNodeByPath(nodeToSearchTargetIn, targetPath);
    }

    private doSelectNodeByPath(nodeToSearchTargetIn: TreeNode<ContentSummary>, targetPath: ContentPath) {
        this.expandNode(nodeToSearchTargetIn).then(() => {
            // if true means one of direct children of node is searched target node
            if (this.isTargetNodeLevelReached(nodeToSearchTargetIn, targetPath)) {
                this.findChildNodeByPath(nodeToSearchTargetIn, targetPath).then((targetNode) => {
                    this.selectNode(targetNode.getDataId());
                    this.scrollToRow(this.getGrid().getDataView().getRowById(targetNode.getId()));
                });
            } else {
                let nextLevelChildPath = targetPath.getPathAtLevel(!!nodeToSearchTargetIn.getData()
                    ? nodeToSearchTargetIn.getData().getPath().getLevel() + 1
                    : 1);
                this.findChildNodeByPath(nodeToSearchTargetIn, nextLevelChildPath).then((targetNode) => {
                    this.doSelectNodeByPath(targetNode, targetPath);
                });
            }
        }).catch((reason: any) => {
            this.handleError(reason);
        }).done();
    }

    private isTargetNodeLevelReached(nodeToSearchTargetIn: TreeNode<ContentSummary>, targetPath: ContentPath): boolean {
        let nodeToExpandLevel = !!nodeToSearchTargetIn.getData() ? nodeToSearchTargetIn.getData().getPath().getLevel() : 0;
        let targetNodeLevelReached = (targetPath.getLevel() - 1) === nodeToExpandLevel;

        return targetNodeLevelReached;
    }

    private findChildNodeByPath(node: TreeNode<ContentSummary>,
                                childNodePath: ContentPath): wemQ.Promise<TreeNode<ContentSummary>> {
        let childNode = this.doFindChildNodeByPath(node, childNodePath);

        if (childNode) {
            return wemQ.resolve(childNode);
        }

        return this.waitChildrenLoadedAndFindChildNodeByPath(node, childNodePath);
    }

    private doFindChildNodeByPath(node: TreeNode<ContentSummary>,
                                  childNodePath: ContentPath): TreeNode<ContentSummary> {
        const children = node.getChildren();
        for (let i = 0; i < children.length; i++) {
            const childPath = children[i].getData().getPath();

            if (childPath && childPath.equals(childNodePath)) {
                return children[i];
            }
        }

        // scrolling to last child of node to make node load the rest
        let child: TreeNode<ContentSummary> = children[children.length - 1];
        this.scrollToRow(this.getGrid().getDataView().getRowById(child.getId()));

        return null;
    }

    private waitChildrenLoadedAndFindChildNodeByPath(node: TreeNode<ContentSummary>,
                                                     childNodePath: ContentPath): wemQ.Promise<TreeNode<ContentSummary>> {
        let deferred = wemQ.defer<TreeNode<ContentSummary>>();

        let dateChangedHandler = (event: DataChangedEvent<ContentSummary>) => {
            let childNode = this.doFindChildNodeByPath(node, childNodePath);
            if (childNode) {
                this.unDataChanged(dateChangedHandler);
                deferred.resolve(this.doFindChildNodeByPath(node, childNodePath));
            }
        };

        this.onDataChanged(dateChangedHandler);

        // check in case child was loaded between this method call and listener set
        const childNode = this.doFindChildNodeByPath(node, childNodePath);
        if (childNode) {
            this.unDataChanged(dateChangedHandler);
            deferred.resolve(this.doFindChildNodeByPath(node, childNodePath));
        }

        return deferred.promise;
    }

    updateContentNode(contentId: api.content.ContentId) {
        let root = this.getRoot().getCurrentRoot();
        let treeNode = root.findNode(contentId.toString());
        if (treeNode) {
            let content = treeNode.getData();
            this.updateNode(new ContentSummaryBuilder(content).build());
        }
    }

    appendContentNode(parentNode: TreeNode<ContentSummary>, childData: ContentSummary, index: number,
                      update: boolean = true): TreeNode<ContentSummary> {

        let appendedNode = this.dataToTreeNode(childData, parentNode);
        let data = parentNode.getData();

        if (!parentNode.hasParent() ||
            (data && parentNode.hasChildren()) ||
            (data && !parentNode.hasChildren() && !data.hasChildren())) {
            parentNode.insertChild(appendedNode, index);
        }

        if (data && !data.hasChildren()) {
            parentNode.setData(new ContentSummaryBuilder(data).setHasChildren(true).build());
        }

        parentNode.clearViewers();

        if (update) {
            this.initAndRender();
        }

        return appendedNode;

    }

    /*appendContentNodes(relationships: TreeNodeParentOfContent[]): wemQ.Promise<TreeNode<ContentSummary>[]> {

     let deferred = wemQ.defer<TreeNode<ContentSummary>[]>();
     let parallelPromises: wemQ.Promise<TreeNode<ContentSummary>[]>[] = [];
     let result: TreeNode<ContentSummary>[] = [];

     relationships.forEach((relationship) => {
     parallelPromises.push(this.fetchChildrenIds(relationship.getNode()).then((contentIds: ContentId[]) => {
     relationship.getChildren().forEach((content: ContentSummary) => {
     result.push(this.appendContentNode(relationship.getNode(), content, contentIds.indexOf(content.getContentId()), false));
     });
     return result;
     }));
     });

     wemQ.all(parallelPromises).then(() => {
     deferred.resolve(result);
     });
     return deferred.promise;
     }*/

    placeContentNode(parent: TreeNode<ContentSummary>,
                     child: TreeNode<ContentSummary>): wemQ.Promise<TreeNode<ContentSummary>> {
        return this.fetchChildrenIds(parent).then((result: ContentId[]) => {
            let map = result.map((el) => {
                return el.toString();
            });
            let index = map.indexOf(child.getData().getId());

            if (!parent.hasParent() ||
                (child.getData() && parent.hasChildren()) ||
                (child.getData() && !parent.hasChildren() && !child.getData().hasChildren())) {
                let parentExpanded = parent.isExpanded();
                parent.moveChild(child, index);
                parent.setExpanded(parentExpanded); // in case of a single child it forces its parent to stay expanded
            }

            child.clearViewers();

            return child;

        });
    }

    placeContentNodes(nodes: TreeNode<ContentSummary>[]): wemQ.Promise<any> {
        let parallelPromises: wemQ.Promise<any>[] = [];

        nodes.forEach((node: TreeNode<ContentSummary>) => {
            parallelPromises.push(this.placeContentNode(node.getParent(), node));
        });

        return wemQ.allSettled(parallelPromises).then((results) => {
            let rootList = this.getRoot().getCurrentRoot().treeToList();
            this.initData(rootList);
            this.invalidate();
            return results;
        });
    }

    deleteContentNode(node: TreeNode<ContentSummary>,
                      update: boolean = true): TreeNode<ContentSummary> {
        let parentNode = node.getParent();

        node.remove();

        let data = parentNode ? parentNode.getData() : null;
        if (data && !parentNode.hasChildren() && data.hasChildren()) {
            parentNode.setData(new ContentSummaryBuilder(data).setHasChildren(false).build());
        }

        if (update) {
            this.initAndRender();
        }

        return parentNode;
    }

    deleteContentNodes(nodes: TreeNode<ContentSummary>[],
                       update: boolean = true) {

        this.deselectDeletedNodes(nodes);

        nodes.forEach((node) => {
            this.deleteContentNode(node, false);
        });

        if (update) {
            this.initAndRender();
        }
    }

    private deselectDeletedNodes(nodes: TreeNode<ContentSummary>[]) {
        let deselected = [];
        this.getSelectedDataList().forEach((content: ContentSummary) => {

            let wasDeleted = nodes.some((node: TreeNode<ContentSummary>) => {
                return content.getContentId().equals(node.getData().getContentId()) ||
                       content.getPath().isDescendantOf(node.getData().getPath());
            });

            if (wasDeleted) {
                deselected.push(content.getId());
            }

        });
        this.deselectNodes(deselected);
    }

    updatePathsInChildren(node: TreeNode<ContentSummary>) {
        node.getChildren().forEach((child) => {
            let nodeSummary = node.getData() ? node.getData() : null;
            let childSummary = child.getData() ? child.getData() : null;
            if (nodeSummary && childSummary) {
                let path = ContentPath.fromParent(nodeSummary.getPath(), childSummary.getPath().getName());
                child.setData(new ContentSummaryBuilder(childSummary).setPath(path).build());
                child.clearViewers();
                this.updatePathsInChildren(child);
            }
        });
    }

    sortNodesChildren(nodes: TreeNode<ContentSummary>[]): wemQ.Promise<void> {

        let parallelPromises: wemQ.Promise<any>[] = [];

        nodes.sort((a, b) => {
            return a.getDataId().localeCompare(b.getDataId());
        });

        let groups = [];
        let group = [];

        groups.push(group);

        for (let i = 0; i < nodes.length; i++) {
            if (!!group[group.length - 1] &&
                nodes[i].getDataId() !== group[group.length - 1].getDataId()) {
                group = [];
                groups.push(group);
            }

            group.push(nodes[i]);
        }

        groups.forEach((grp: TreeNode<ContentSummary>[]) => {
            if (grp.length > 0) {
                parallelPromises.push(
                    this.updateNodes(grp[0].getData()).then(() => {
                        let hasChildren = grp[0].hasChildren();
                        grp[0].setChildren([]);
                        return this.fetchChildren(grp[0]).then((dataList: ContentSummary[]) => {
                            grp.forEach((el) => {
                                if (hasChildren) {
                                    el.setChildren(this.dataToTreeNodes(dataList, el));
                                }
                            });
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        });
                    }).then(() => {
                        let rootList = this.getRoot().getCurrentRoot().treeToList();
                        this.initData(rootList);
                    })
                );
            }
        });

        return wemQ.all(parallelPromises).spread<void>(() => {
            return wemQ(null);
        }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    protected handleItemMetadata(row: number) {
        let node = this.getItem(row);
        if (this.isEmptyNode(node)) {
            return {cssClasses: 'empty-node'};
        }

        return null;
    }
}
