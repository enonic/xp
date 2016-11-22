import "../../api.ts";
import {ContentTreeGridActions} from "./action/ContentTreeGridActions";
import {ContentBrowseToolbar} from "./ContentBrowseToolbar";
import {ContentTreeGrid} from "./ContentTreeGrid";
import {ContentBrowseFilterPanel} from "./filter/ContentBrowseFilterPanel";
import {ContentBrowseItemPanel} from "./ContentBrowseItemPanel";
import {MobileContentItemStatisticsPanel} from "../view/MobileContentItemStatisticsPanel";
import {FloatingDetailsPanel} from "../view/detail/FloatingDetailsPanel";
import {DockedDetailsPanel} from "../view/detail/DockedDetailsPanel";
import {DetailsView} from "../view/detail/DetailsView";
import {NonMobileDetailsPanelsManager, NonMobileDetailsPanelsManagerBuilder} from "../view/detail/NonMobileDetailsPanelsManager";
import {Router} from "../Router";
import {ActiveDetailsPanelManager} from "../view/detail/ActiveDetailsPanelManager";
import {ContentBrowseItem} from "./ContentBrowseItem";
import {ToggleSearchPanelEvent} from "./ToggleSearchPanelEvent";
import {ToggleSearchPanelWithDependenciesEvent} from "./ToggleSearchPanelWithDependenciesEvent";
import {NewMediaUploadEvent} from "../create/NewMediaUploadEvent";
import {ContentPreviewPathChangedEvent} from "../view/ContentPreviewPathChangedEvent";
import {ContentPublishMenuManager} from "./ContentPublishMenuManager";
import {TreeNodeParentOfContent} from "./TreeNodeParentOfContent";
import {TreeNodesOfContentPath} from "./TreeNodesOfContentPath";

import TreeNode = api.ui.treegrid.TreeNode;
import BrowseItem = api.app.browse.BrowseItem;
import UploadItem = api.ui.uploader.UploadItem;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import ContentPath = api.content.ContentPath;
import ContentServerEventsHandler = api.content.event.ContentServerEventsHandler;
import DataChangedEvent = api.ui.treegrid.DataChangedEvent;

export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummaryAndCompareStatus> {

    private browseActions: ContentTreeGridActions;

    private toolbar: ContentBrowseToolbar;

    private contentTreeGrid: ContentTreeGrid;

    private contentFilterPanel: ContentBrowseFilterPanel;

    private contentBrowseItemPanel: ContentBrowseItemPanel;

    private mobileContentItemStatisticsPanel: MobileContentItemStatisticsPanel;

    constructor() {

        this.contentTreeGrid = new ContentTreeGrid();

        this.contentBrowseItemPanel = new ContentBrowseItemPanel(this.contentTreeGrid);

        this.contentFilterPanel = new ContentBrowseFilterPanel();

        this.browseActions = <ContentTreeGridActions>this.contentTreeGrid.getContextMenu().getActions();

        this.toolbar = new ContentBrowseToolbar(this.browseActions);

        super({
            browseToolbar: this.toolbar,
            treeGrid: this.contentTreeGrid,
            browseItemPanel: this.contentBrowseItemPanel,
            filterPanel: this.contentFilterPanel,
            hasDetailsPanel: true
        });

        var showMask = () => {
            if (this.isVisible()) {
                this.contentTreeGrid.mask();
            }
        };
        this.contentFilterPanel.onSearchStarted(showMask);
        this.contentFilterPanel.onReset(showMask);
        this.contentFilterPanel.onRefreshStarted(showMask);

        this.getTreeGrid().onDataChanged((event: api.ui.treegrid.DataChangedEvent<ContentSummaryAndCompareStatus>) => {
            if (event.getType() === 'updated') {
                let browseItems = this.treeNodesToBrowseItems(event.getTreeNodes());
                this.getBrowseItemPanel().updateItemViewers(browseItems);
                this.browseActions.updateActionsEnabledState(
                    this.treeNodesToBrowseItems(this.contentTreeGrid.getRoot().getFullSelection()));
            }
        });

        this.onShown(() => {
            Router.setHash("browse");
        });

        ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(item.isInRangeOrSmaller(ResponsiveRanges._360_540));
        });

        this.handleGlobalEvents();
    }

    doRender(): wemQ.Promise<boolean> {
        return super.doRender().then((rendered) => {

            var detailsView = new DetailsView();

            var nonMobileDetailsPanelsManagerBuilder = NonMobileDetailsPanelsManager.create();
            this.initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder, detailsView);
            this.initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder, detailsView);
            this.initItemStatisticsPanelForMobile(detailsView);

            var nonMobileDetailsPanelsManager = nonMobileDetailsPanelsManagerBuilder.build();
            if (nonMobileDetailsPanelsManager.requiresCollapsedDetailsPanel()) {
                nonMobileDetailsPanelsManager.hideDockedDetailsPanel();
            }
            nonMobileDetailsPanelsManager.ensureButtonHasCorrectState();

            this.setActiveDetailsPanel(nonMobileDetailsPanelsManager);

            this.onShown(() => {
                if (!!nonMobileDetailsPanelsManager.getActivePanel().getActiveWidget()) {
                    nonMobileDetailsPanelsManager.getActivePanel().getActiveWidget().slideIn();
                }
            });

            new ContentPublishMenuManager(this.browseActions);
            this.toolbar.appendChild(nonMobileDetailsPanelsManager.getToggleButton());
            this.toolbar.appendChild(ContentPublishMenuManager.getPublishMenuButton());

            this.subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager);

            return rendered;
        }).catch((error) => {
            console.error("Couldn't render ContentBrowsePanel", error);
            return true;
        });
    }

    private subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager: NonMobileDetailsPanelsManager) {

        this.getTreeGrid().onSelectionChanged((currentSelection: TreeNode<ContentSummaryAndCompareStatus>[],
                                               fullSelection: TreeNode<ContentSummaryAndCompareStatus>[]) => {
            var item = this.getFirstSelectedBrowseItem(fullSelection);
            this.doUpdateDetailsPanel(item ? item.getModel() : null);
        });

        ResponsiveManager.onAvailableSizeChanged(this.getFilterAndGridSplitPanel(), (item: ResponsiveItem) => {
            nonMobileDetailsPanelsManager.handleResizeEvent();
        });

        ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            if (ResponsiveRanges._540_720.isFitOrBigger(item.getOldRangeValue())) {
                ContentPublishMenuManager.getPublishMenuButton().maximize();
                if (item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    nonMobileDetailsPanelsManager.hideActivePanel();
                    ActiveDetailsPanelManager.setActiveDetailsPanel(this.mobileContentItemStatisticsPanel.getDetailsPanel());
                }
            } else {
                ContentPublishMenuManager.getPublishMenuButton().minimize();
            }
        });
    }

    private initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder,
                                            detailsPanelView: DetailsView) {

        var dockedDetailsPanel = new DockedDetailsPanel(detailsPanelView);

        var contentPanelsAndDetailPanel: api.ui.panel.SplitPanel = new api.ui.panel.SplitPanelBuilder(this.getFilterAndGridSplitPanel(),
            dockedDetailsPanel).setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL).setSecondPanelSize(280,
            api.ui.panel.SplitPanelUnit.PIXEL).setSecondPanelMinSize(280, api.ui.panel.SplitPanelUnit.PIXEL).setAnimationDelay(
            600).setSecondPanelShouldSlideRight(true).build();

        contentPanelsAndDetailPanel.addClass("split-panel-with-details");
        contentPanelsAndDetailPanel.setSecondPanelSize(280, api.ui.panel.SplitPanelUnit.PIXEL);

        nonMobileDetailsPanelsManagerBuilder.setSplitPanelWithGridAndDetails(contentPanelsAndDetailPanel);
        nonMobileDetailsPanelsManagerBuilder.setDefaultDetailsPanel(dockedDetailsPanel);

        this.appendChild(contentPanelsAndDetailPanel);
    }

    private initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder, detailsView: DetailsView) {

        var floatingDetailsPanel = new FloatingDetailsPanel(detailsView);

        nonMobileDetailsPanelsManagerBuilder.setFloatingDetailsPanel(floatingDetailsPanel);

        this.appendChild(floatingDetailsPanel);
    }

    private initItemStatisticsPanelForMobile(detailsView: DetailsView) {
        this.mobileContentItemStatisticsPanel = new MobileContentItemStatisticsPanel(this.browseActions, detailsView);

        const updateMobilePanel = () => {
            const defer = wemQ.defer();

            const prevItem = this.mobileContentItemStatisticsPanel.getPreviewPanel().getItem();
            const browseItem = this.getFirstSelectedBrowseItem();
            const item = browseItem.toViewItem();

            const itemChanged = !prevItem || !prevItem.getModel() || prevItem.getModel().getId() !== browseItem.getId();

            if (itemChanged) {
                new api.content.page.IsRenderableRequest(new api.content.ContentId(browseItem.getId())).sendAndParse().then(
                    (renderable: boolean) => {
                        item.setRenderable(renderable);
                        this.mobileContentItemStatisticsPanel.getPreviewPanel().setItem(item);
                        this.mobileContentItemStatisticsPanel.setItem(item);
                        defer.resolve(true);
                    });
            } else {
                defer.resolve(true);
            }

            return defer.promise;
        };

        const showMobilePanel = () => this.mobileContentItemStatisticsPanel.slideIn();

        const updateAndShowMobilePanel = () => updateMobilePanel().then(showMobilePanel);

        api.ui.treegrid.TreeGridItemClickedEvent.on((event) => {
            if (this.isSomethingSelectedInMobileMode()) {
                updateAndShowMobilePanel();
            }
        });

        this.appendChild(this.mobileContentItemStatisticsPanel);
    }

    private updateMobilePanel(fullSelection?: TreeNode<ContentSummaryAndCompareStatus>[]) {
        this.mobileContentItemStatisticsPanel.setItem(this.getFirstSelectedBrowseItem(fullSelection).toViewItem());
    }

    private getFirstSelectedBrowseItem(fullSelection?: TreeNode<ContentSummaryAndCompareStatus>[]): BrowseItem<ContentSummaryAndCompareStatus> {
        var browseItems: BrowseItem<ContentSummaryAndCompareStatus>[] = this.treeNodesToBrowseItems(!!fullSelection
                ? fullSelection
                : this.contentTreeGrid.getRoot().getFullSelection()),
            item: BrowseItem<ContentSummaryAndCompareStatus> = null;
        if (browseItems.length > 0) {
            item = browseItems[0];
        }
        return item;
    }

    private isSomethingSelected(): boolean {
        return this.getFirstSelectedBrowseItem() != null;
    }

    private isMobileMode(): boolean {
        // return ActiveDetailsPanelManager.getActiveDetailsPanel() == this.mobileContentItemStatisticsPanel.getDetailsPanel();
        return this.mobileContentItemStatisticsPanel.isVisible();
    }

    private isSomethingSelectedInMobileMode(): boolean {
        return this.isMobileMode() && this.isSomethingSelected();
    }

    private setActiveDetailsPanel(nonMobileDetailsPanelsManager: NonMobileDetailsPanelsManager) {
        if (this.mobileContentItemStatisticsPanel.isVisible()) {
            ActiveDetailsPanelManager.setActiveDetailsPanel(this.mobileContentItemStatisticsPanel.getDetailsPanel());
        } else {
            ActiveDetailsPanelManager.setActiveDetailsPanel(nonMobileDetailsPanelsManager.getActivePanel());
        }
    }

    treeNodesToBrowseItems(nodes: TreeNode<ContentSummaryAndCompareStatus>[]): ContentBrowseItem[] {
        var browseItems: ContentBrowseItem[] = [];

        // do not proceed duplicated content. still, it can be selected
        nodes.forEach((node: TreeNode<ContentSummaryAndCompareStatus>, index: number) => {
            for (var i = 0; i <= index; i++) {
                if (nodes[i].getData().getId() === node.getData().getId()) {
                    break;
                }
            }
            if (i === index) {
                var data = node.getData();
                if (!!data && !!data.getContentSummary()) {
                    let item = new ContentBrowseItem(data).setId(data.getId()).setDisplayName(
                        data.getContentSummary().getDisplayName()).setPath(data.getContentSummary().getPath().toString()).setIconUrl(
                        new api.content.util.ContentIconUrlResolver().setContent(data.getContentSummary()).resolve());
                    browseItems.push(<ContentBrowseItem> item);
                }
            }
        });

        return browseItems;
    }


    private handleGlobalEvents() {

        ToggleSearchPanelEvent.on(() => {
            this.toggleFilterPanel();
        });

        ToggleSearchPanelWithDependenciesEvent.on((event: ToggleSearchPanelWithDependenciesEvent) => {
            this.showFilterPanel();
            this.contentFilterPanel.setDependencyItem(event.getContent(), event.isInbound());
        });

        NewMediaUploadEvent.on((event) => {
            this.handleNewMediaUpload(event);
        });

        this.subscribeOnContentEvents();

        ContentPreviewPathChangedEvent.on((event: ContentPreviewPathChangedEvent) => {
            this.selectPreviewedContentInGrid(event.getPreviewPath());
        });
    }

    private selectPreviewedContentInGrid(contentPreviewPath: string) {
        var path = this.getPathFromPreviewPath(contentPreviewPath);
        if (path) {
            var contentPath = api.content.ContentPath.fromString(path);
            if (this.isSingleItemSelectedInGrid() && !this.isGivenPathSelectedInGrid(contentPath)) {
                this.selectContentInGridByPath(contentPath);
            }
        }
    }

    private selectContentInGridByPath(path: api.content.ContentPath) {
        this.contentTreeGrid.expandTillNodeWithGivenPath(path, this.contentTreeGrid.getSelectedNodes()[0]);
    }

    private isGivenPathSelectedInGrid(path: api.content.ContentPath): boolean {
        var contentSummary: ContentSummaryAndCompareStatus = this.contentTreeGrid.getSelectedNodes()[0].getData();
        return contentSummary.getPath().equals(path);
    }

    private isSingleItemSelectedInGrid(): boolean {
        return this.contentTreeGrid.getSelectedNodes() && this.contentTreeGrid.getSelectedNodes().length == 1;
    }

    private getPathFromPreviewPath(contentPreviewPath: string): string {
        return api.rendering.UriHelper.getPathFromPortalPreviewUri(contentPreviewPath, api.rendering.RenderingMode.PREVIEW,
            api.content.Branch.DRAFT);
    }

    private subscribeOnContentEvents() {
        var handler = ContentServerEventsHandler.getInstance();

        handler.onContentCreated((data: ContentSummaryAndCompareStatus[]) => this.handleContentCreated(data));

        handler.onContentUpdated((data: ContentSummaryAndCompareStatus[]) => this.handleContentUpdated(data));

        handler.onContentRenamed((data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) => {
            this.handleContentCreated(data, oldPaths);
        });

        handler.onContentDeleted((data: api.content.event.ContentServerChangeItem[]) => {
            this.handleContentDeleted(data.map(d => d.getPath()));
        });

        handler.onContentPending((data: ContentSummaryAndCompareStatus[]) => this.handleContentPending(data));

        handler.onContentDuplicated((data: ContentSummaryAndCompareStatus[]) => this.handleContentCreated(data));

        handler.onContentPublished((data: ContentSummaryAndCompareStatus[]) => this.handleContentPublished(data));

        handler.onContentUnpublished((data: ContentSummaryAndCompareStatus[]) => this.handleContentUnpublished(data));

        handler.onContentMoved((data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) => {
            // combination of delete and create
            this.handleContentDeleted(oldPaths);
            this.handleContentCreated(data);
        });

        handler.onContentSorted((data: ContentSummaryAndCompareStatus[]) => this.handleContentSorted(data));
    }

    private handleContentCreated(data: ContentSummaryAndCompareStatus[], oldPaths?: ContentPath[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: created", data, oldPaths);
        }

        this.processContentCreated(data, oldPaths);
    }

    private handleContentUpdated(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: updated", data);
        }

        var changed = this.doHandleContentUpdate(data);

        this.updateStatisticsPanel(data);

        return this.contentTreeGrid.placeContentNodes(changed);
    }

    private handleContentDeleted(paths: ContentPath[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: deleted", paths);
        }

        var nodes = this.contentTreeGrid.findByPaths(paths).map(el => el.getNodes());
        var merged = [];
        // merge array of nodes arrays
        merged = merged.concat.apply(merged, nodes);

        merged.forEach((node: TreeNode<ContentSummaryAndCompareStatus>) => {
            var contentSummary = node.getData().getContentSummary();
            if (node.getData() && !!contentSummary) {
                this.doUpdateDetailsPanel(null);
            }
        });

        this.contentTreeGrid.deleteContentNodes(merged);

        // now get unique parents and update their hasChildren
        var uniqueParents = paths.map(path => path.getParentPath()).filter((parent, index, self) => {
            return self.indexOf(parent) === index;
        });
        let parentNodes = this.contentTreeGrid.findByPaths(uniqueParents).map(parentNode => parentNode.getNodes());
        let mergedParentNodes = [];
        mergedParentNodes = mergedParentNodes.concat.apply(mergedParentNodes, parentNodes);

        mergedParentNodes.forEach((parentNode: TreeNode<ContentSummaryAndCompareStatus>) => {
            if (parentNode.getChildren().length == 0) {
                // update parent if all children were deleted
                this.contentTreeGrid.refreshNodeData(parentNode);
            }
        });

        this.setRefreshOfFilterRequired();
        window.setTimeout(() => {
            this.refreshFilter();
        }, 1000);
    }

    private handleContentPending(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: pending", data);
        }
        this.doHandleContentUpdate(data);
    }

    private handleContentPublished(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: published", data);
        }
        this.doHandleContentUpdate(data);
    }

    private handleContentUnpublished(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: unpublished", data);
        }
        this.doHandleContentUpdate(data);
    }

    private processContentCreated(data: ContentSummaryAndCompareStatus[], oldPaths?: ContentPath[]) {

        var paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath()),
            createResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths, true),
            parentsOfContents: TreeNodeParentOfContent[] = [];

        for (var i = 0; i < createResult.length; i++) {

            var dataToHandle: ContentSummaryAndCompareStatus[] = [];

            data.forEach((el) => {

                if (el.getContentSummary().getPath().isChildOf(createResult[i].getPath())) {

                    if (oldPaths && oldPaths.length > 0) {
                        var movedNodes: TreeNode<ContentSummaryAndCompareStatus>[] = [],
                            renameResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(oldPaths);
                        var premerged = renameResult.map((curRenameResult) => {
                            return curRenameResult.getNodes();
                        });
                        // merge array of nodes arrays
                        movedNodes = movedNodes.concat.apply(movedNodes, premerged);

                        movedNodes.forEach((node) => {
                            if (node.getDataId() === el.getId()) {
                                node.setData(el);
                                node.clearViewers();
                                this.contentTreeGrid.updatePathsInChildren(node);
                            }
                        });
                        this.contentTreeGrid.placeContentNodes(movedNodes);
                    } else {
                        dataToHandle.push(el);
                    }
                }
            });

            createResult[i].getNodes().map((node) => {
                parentsOfContents.push(new TreeNodeParentOfContent(dataToHandle, node));
            });
        }

        this.contentTreeGrid.appendContentNodes(parentsOfContents).then((results: TreeNode<ContentSummaryAndCompareStatus>[]) => {
            var appendedNodesThatShouldBeVisible = [];
            results.forEach((appendedNode) => {
                if (appendedNode.getParent() && appendedNode.getParent().isExpanded()) {
                    appendedNodesThatShouldBeVisible.push(appendedNode);
                }
            });

            this.contentTreeGrid.placeContentNodes(appendedNodesThatShouldBeVisible).then(() => {
                this.contentTreeGrid.initAndRender();

                this.setRefreshOfFilterRequired();
                window.setTimeout(() => {
                    this.refreshFilter();
                }, 1000);
            });
        });
    }

    private doHandleContentUpdate(data: ContentSummaryAndCompareStatus[]): TreeNode<ContentSummaryAndCompareStatus>[] {
        var changed = this.updateNodes(data);

        this.updateDetailsPanel(data);

        this.contentTreeGrid.invalidate();

        // Update since CompareStatus changed
        let changedEvent = new DataChangedEvent<ContentSummaryAndCompareStatus>(changed, DataChangedEvent.UPDATED);
        this.contentTreeGrid.notifyDataChanged(changedEvent);

        return changed;
    }

    private updateNodes(data: ContentSummaryAndCompareStatus[]): TreeNode<ContentSummaryAndCompareStatus>[] {
        var paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        var treeNodes: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths);

        let changed = [];
        data.forEach((el) => {
            for (var i = 0; i < treeNodes.length; i++) {
                if (treeNodes[i].getId() === el.getId()) {
                    treeNodes[i].updateNodeData(el);
                    changed.push(...treeNodes[i].getNodes());
                    break;
                }
            }
        });

        return changed;
    }

    private handleContentSorted(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: sorted", data);
        }
        var paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        var sortResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths);

        var nodes = sortResult.map((el) => {
            return el.getNodes();
        });
        var merged = [];
        // merge array of nodes arrays
        merged = merged.concat.apply(merged, nodes);

        this.contentTreeGrid.sortNodesChildren(merged).then(() => this.contentTreeGrid.invalidate());
    }

    private handleNewMediaUpload(event: NewMediaUploadEvent) {
        event.getUploadItems().forEach((item: UploadItem<ContentSummary>) => {
            this.contentTreeGrid.appendUploadNode(item);
        });
    }

    private updateStatisticsPanel(data: ContentSummaryAndCompareStatus[]) {
        var previewItem = this.getBrowseItemPanel().getStatisticsItem();

        if (!previewItem) {
            return;
        }

        var content: ContentSummaryAndCompareStatus;
        var previewItemNeedsUpdate = data.some((contentItem: ContentSummaryAndCompareStatus) => {
            if (contentItem.getPath().toString() === previewItem.getPath()) {
                content = contentItem;
                return true;
            }
        });

        if (previewItemNeedsUpdate) {
            new api.content.page.IsRenderableRequest(content.getContentId()).sendAndParse().then((renderable: boolean) => {
                var item = new BrowseItem<ContentSummaryAndCompareStatus>(content).setId(content.getId()).setDisplayName(
                    content.getDisplayName()).setPath(content.getPath().toString()).setIconUrl(
                    new api.content.util.ContentIconUrlResolver().setContent(content.getContentSummary()).resolve()).setRenderable(
                    renderable);
                this.getBrowseItemPanel().setStatisticsItem(item);
            });
        }
    }

    private updateDetailsPanel(data: ContentSummaryAndCompareStatus[]) {
        var detailsPanel = ActiveDetailsPanelManager.getActiveDetailsPanel();
        var itemInDetailPanel = detailsPanel ? detailsPanel.getItem() : null;

        if (!itemInDetailPanel) {
            return;
        }

        var content: ContentSummaryAndCompareStatus;
        var detailsPanelNeedsUpdate = data.some((contentItem: ContentSummaryAndCompareStatus) => {
            if (contentItem.getId() == itemInDetailPanel.getId()) {
                content = contentItem;
                return true;
            }
        });

        if (detailsPanelNeedsUpdate) {
            this.doUpdateDetailsPanel(content);
        }
    }

    private doUpdateDetailsPanel(item: ContentSummaryAndCompareStatus) {
        var detailsPanel = ActiveDetailsPanelManager.getActiveDetailsPanel();
        if (detailsPanel) {
            detailsPanel.setItem(item)
        }
    }

    getBrowseItemPanel(): ContentBrowseItemPanel {
        return <ContentBrowseItemPanel>super.getBrowseItemPanel();
    }
}
