import "../../api.ts";
import {ContentTreeGridActions} from "./action/ContentTreeGridActions";
import {ContentBrowseToolbar} from "./ContentBrowseToolbar";
import {ContentTreeGrid} from "./ContentTreeGrid";
import {ContentBrowseFilterPanel} from "./filter/ContentBrowseFilterPanel";
import {ContentBrowseItemPanel} from "./ContentBrowseItemPanel";
import {MobileContentItemStatisticsPanel} from "../view/MobileContentItemStatisticsPanel";
import {MobileContentTreeGridActions} from "./action/MobileContentTreeGridActions";
import {DetailsPanel} from "../view/detail/DetailsPanel";
import {NonMobileDetailsPanelsManager, NonMobileDetailsPanelsManagerBuilder} from "../view/detail/NonMobileDetailsPanelsManager";
import {Router} from "../Router";
import {ActiveDetailsPanelManager} from "../view/detail/ActiveDetailsPanelManager";
import {ContentBrowseItem} from "./ContentBrowseItem";
import {ToggleSearchPanelEvent} from "./ToggleSearchPanelEvent";
import {ToggleSearchPanelWithDependenciesEvent} from "./ToggleSearchPanelWithDependenciesEvent";
import {NewMediaUploadEvent} from "../create/NewMediaUploadEvent";
import {ContentPreviewPathChangedEvent} from "../view/ContentPreviewPathChangedEvent";
import {ContentPublishMenuManager} from "./ContentPublishMenuManager";

import TreeNode = api.ui.treegrid.TreeNode;
import BrowseItem = api.app.browse.BrowseItem;
import UploadItem = api.ui.uploader.UploadItem;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
import CompareStatus = api.content.CompareStatus;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
import ContentPath = api.content.ContentPath;
import NodeServerChangeType = api.event.NodeServerChangeType;
import BatchContentRequest = api.content.BatchContentRequest;
import TreeNodesOfContentPath = api.content.TreeNodesOfContentPath;
import ContentId = api.content.ContentId;
import BatchContentServerEvent = api.content.event.BatchContentServerEvent;
import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
import ContentServerEventsHandler = api.content.event.ContentServerEventsHandler;
import DataChangedEvent = api.ui.treegrid.DataChangedEvent;

export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummaryAndCompareStatus> {

    private browseActions: ContentTreeGridActions;

    private toolbar: ContentBrowseToolbar;

    private contentTreeGrid: ContentTreeGrid;

    private contentFilterPanel: ContentBrowseFilterPanel;

    private contentBrowseItemPanel: ContentBrowseItemPanel;

    private mobileContentItemStatisticsPanel: MobileContentItemStatisticsPanel;

    private mobileBrowseActions: MobileContentTreeGridActions;

    private floatingDetailsPanel: DetailsPanel;

    private defaultDockedDetailsPanel: DetailsPanel;

    constructor() {

        this.contentTreeGrid = new ContentTreeGrid();

        // this.contentBrowseItemPanel = components.detailPanel = new ContentBrowseItemPanel();
        this.contentBrowseItemPanel = new ContentBrowseItemPanel();

        this.contentFilterPanel = new ContentBrowseFilterPanel();

        this.browseActions = <ContentTreeGridActions>this.contentTreeGrid.getContextMenu().getActions();

        this.toolbar = new ContentBrowseToolbar(this.browseActions);

        this.defaultDockedDetailsPanel = DetailsPanel.create().setUseSplitter(false).build();

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

                this.browseActions.updateActionsEnabledState(this.getBrowseItemPanel().getItems());
                this.mobileBrowseActions.updateActionsEnabledState(this.getBrowseItemPanel().getItems());
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

    doRender(): boolean {
        super.doRender();

        var nonMobileDetailsPanelsManagerBuilder = NonMobileDetailsPanelsManager.create();
        this.initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder);
        this.initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder);
        this.initItemStatisticsPanelForMobile();

        var nonMobileDetailsPanelsManager = nonMobileDetailsPanelsManagerBuilder.build();
        if (nonMobileDetailsPanelsManager.requiresCollapsedDetailsPanel()) {
            nonMobileDetailsPanelsManager.hideDockedDetailsPanel();
        }
        nonMobileDetailsPanelsManager.ensureButtonHasCorrectState();

        this.setActiveDetailsPanel(nonMobileDetailsPanelsManager);

        this.subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager);

        this.onShown(() => {
            if (!!nonMobileDetailsPanelsManager.getActivePanel().getActiveWidget()) {
                nonMobileDetailsPanelsManager.getActivePanel().getActiveWidget().slideIn();
            }
        });

        this.toolbar.appendChild(nonMobileDetailsPanelsManager.getToggleButton());

        let contentPublishMenuManager = new ContentPublishMenuManager(this.browseActions);
        this.toolbar.appendChild(contentPublishMenuManager.getPublishMenuButton());

        return true;
    }

    private subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager: NonMobileDetailsPanelsManager) {

        this.getTreeGrid().onSelectionChanged((currentSelection: TreeNode<Object>[], fullSelection: TreeNode<Object>[]) => {
            var browseItems: api.app.browse.BrowseItem<ContentSummaryAndCompareStatus>[] = this.getBrowseItemPanel().getItems(),
                item: api.app.browse.BrowseItem<ContentSummaryAndCompareStatus> = null;
            if (browseItems.length > 0) {
                item = browseItems[browseItems.length - 1];
            }
            this.updateDetailsPanel(item ? item.getModel() : null);
        });

        ResponsiveManager.onAvailableSizeChanged(this.getFilterAndGridSplitPanel(), (item: ResponsiveItem) => {
            nonMobileDetailsPanelsManager.handleResizeEvent();
        });

        ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            if (ResponsiveRanges._540_720.isFitOrBigger(item.getOldRangeValue()) &&
                item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                nonMobileDetailsPanelsManager.hideActivePanel();
                ActiveDetailsPanelManager.setActiveDetailsPanel(this.mobileContentItemStatisticsPanel.getDetailsPanel());
            }
        });

    }

    private initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder) {

        var contentPanelsAndDetailPanel: api.ui.panel.SplitPanel = new api.ui.panel.SplitPanelBuilder(this.getFilterAndGridSplitPanel(),
            this.defaultDockedDetailsPanel).setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL).setSecondPanelSize(280,
            api.ui.panel.SplitPanelUnit.PIXEL).setSecondPanelMinSize(280, api.ui.panel.SplitPanelUnit.PIXEL).setAnimationDelay(
            600).setSecondPanelShouldSlideRight(true).build();

        contentPanelsAndDetailPanel.addClass("split-panel-with-details");
        contentPanelsAndDetailPanel.setSecondPanelSize(280, api.ui.panel.SplitPanelUnit.PIXEL);

        this.appendChild(contentPanelsAndDetailPanel);

        nonMobileDetailsPanelsManagerBuilder.setSplitPanelWithGridAndDetails(contentPanelsAndDetailPanel);
        nonMobileDetailsPanelsManagerBuilder.setDefaultDetailsPanel(this.defaultDockedDetailsPanel);
    }

    private initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder) {

        this.floatingDetailsPanel = DetailsPanel.create().build();

        this.floatingDetailsPanel.addClass("floating-details-panel");

        nonMobileDetailsPanelsManagerBuilder.setFloatingDetailsPanel(this.floatingDetailsPanel);

        this.appendChild(this.floatingDetailsPanel);
    }

    private initItemStatisticsPanelForMobile() {
        this.mobileBrowseActions = new MobileContentTreeGridActions(this.contentTreeGrid);
        this.mobileContentItemStatisticsPanel = new MobileContentItemStatisticsPanel(this.mobileBrowseActions);

        let updateItem = () => {
            if (ActiveDetailsPanelManager.getActiveDetailsPanel() == this.mobileContentItemStatisticsPanel.getDetailsPanel()) {
                var browseItems: api.app.browse.BrowseItem<ContentSummaryAndCompareStatus>[] = this.getBrowseItemPanel().getItems();
                if (browseItems.length == 1) {
                    new api.content.page.IsRenderableRequest(new api.content.ContentId(browseItems[0].getId())).sendAndParse().then(
                        (renderable: boolean) => {
                            var item: api.app.view.ViewItem<ContentSummaryAndCompareStatus> = browseItems[0].toViewItem();
                            item.setRenderable(renderable);
                            this.mobileContentItemStatisticsPanel.setItem(item);
                            this.mobileBrowseActions.updateActionsEnabledState(browseItems);
                        });
                }
            }
        };

        // new selection
        this.contentTreeGrid.onSelectionChanged(updateItem);

        // repeated selection
        api.content.TreeGridItemClickedEvent.on((event) => {
            if (event.isRepeatedSelection()) {
                updateItem();
            }
        });

        this.appendChild(this.mobileContentItemStatisticsPanel);
    }

    private setActiveDetailsPanel(nonMobileDetailsPanelsManager: NonMobileDetailsPanelsManager) {
        if (this.mobileContentItemStatisticsPanel.isVisible()) {
            ActiveDetailsPanelManager.setActiveDetailsPanel(this.mobileContentItemStatisticsPanel.getDetailsPanel());
        } else {
            ActiveDetailsPanelManager.setActiveDetailsPanel(nonMobileDetailsPanelsManager.getActivePanel());
        }
    }

    treeNodesToBrowseItems(nodes: TreeNode<ContentSummaryAndCompareStatus>[]): BrowseItem<ContentSummaryAndCompareStatus>[] {
        var browseItems: BrowseItem<ContentSummaryAndCompareStatus>[] = [];

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
                        new ContentIconUrlResolver().setContent(data.getContentSummary()).resolve());
                    browseItems.push(item);
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
            this.handleContentCreated(data, oldPaths)
        });

        handler.onContentDeleted((data: api.content.event.ContentServerChangeItem[]) => {
            this.handleContentDeleted(data.map(d => d.getPath()));
        });

        handler.onContentPending((data: ContentSummaryAndCompareStatus[]) => this.handleContentPending(data));

        handler.onContentDuplicated((data: ContentSummaryAndCompareStatus[]) => this.handleContentCreated(data));

        handler.onContentPublished((data: ContentSummaryAndCompareStatus[]) => this.handleContentPublishedOrUnpublished(data));

        handler.onContentUnpublished((data: ContentSummaryAndCompareStatus[]) => this.handleContentPublishedOrUnpublished(data));

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

        var paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        var createResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths, true);

        var isFiltered = this.contentTreeGrid.getRoot().isFiltered(),
            nodes: TreeNode<ContentSummaryAndCompareStatus>[] = [];

        data.forEach((el) => {
            for (var i = 0; i < createResult.length; i++) {
                if (el.getContentSummary().getPath().isChildOf(createResult[i].getPath())) {
                    if (oldPaths && oldPaths.length > 0) {
                        var renameResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(oldPaths);
                        var premerged = renameResult.map((el) => {
                            return el.getNodes();
                        });
                        // merge array of nodes arrays
                        nodes = nodes.concat.apply(nodes, premerged);
                        nodes.forEach((node) => {
                            if (node.getDataId() === el.getId()) {
                                node.setData(el);
                                node.clearViewers();
                                this.contentTreeGrid.xUpdatePathsInChildren(node);
                            }
                        });
                    } else {
                        this.contentTreeGrid.xAppendContentNodes(
                            createResult[i].getNodes().map((node) => {
                                return new api.content.TreeNodeParentOfContent(el, node);
                            }),
                            !isFiltered
                        ).then((results) => {
                                nodes = nodes.concat(results);
                            });
                    }
                    break;
                }
            }
        });

        this.contentTreeGrid.initAndRender();

        isFiltered = true;
        if (isFiltered) {
            this.setRefreshOfFilterRequired();
            window.setTimeout(() => {
                this.refreshFilter();
            }, 1000);
        }

    }

    private handleContentUpdated(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: updated", data);
        }
        var paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        var treeNodes: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths);

        let changed = [];
        data.forEach((el) => {
            for (var i = 0; i < treeNodes.length; i++) {
                if (treeNodes[i].getId() === el.getId()) {
                    treeNodes[i].updateNodeData(el);
                    this.updateStatisticsPreview(el); // update preview item
                    this.updateItemInDetailsPanelIfNeeded(el);
                    changed.push(...treeNodes[i].getNodes());
                    break;
                }
            }
        });

        // Unpdate since CompareStatus changed
        let changedEvent = new DataChangedEvent<ContentSummaryAndCompareStatus>(changed, DataChangedEvent.UPDATED);
        this.contentTreeGrid.notifyDataChanged(changedEvent);

        return this.contentTreeGrid.xPlaceContentNodes(changed);
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

                this.updateDetailsPanel(null);
            }
        });

        this.contentTreeGrid.xDeleteContentNodes(merged);

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
        var paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        var treeNodes: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths);

        data.forEach((el) => {
            for (var i = 0; i < treeNodes.length; i++) {
                if (treeNodes[i].getId() === el.getId()) {

                    treeNodes[i].updateNodeData(el);

                    this.updateItemInDetailsPanelIfNeeded(el);

                    break;
                }
            }
        });

        this.contentTreeGrid.invalidate();
    }

    private handleContentPublishedOrUnpublished(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug("ContentBrowsePanel: published or unpublished", data);
        }
        var paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        var treeNodes: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths);

        let changed = [];
        data.forEach((el) => {
            for (var i = 0; i < treeNodes.length; i++) {
                if (treeNodes[i].getId() === el.getId()) {
                    treeNodes[i].updateNodeData(el);
                    this.updateItemInDetailsPanelIfNeeded(el);
                    changed.push(...treeNodes[i].getNodes());
                    break;
                }
            }
        });
        this.contentTreeGrid.invalidate();

        // Unpdate since CompareStatus changed
        let changedEvent = new DataChangedEvent<ContentSummaryAndCompareStatus>(changed, DataChangedEvent.UPDATED);
        this.contentTreeGrid.notifyDataChanged(changedEvent);
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

        this.contentTreeGrid.xSortNodesChildren(merged).then(() => this.contentTreeGrid.invalidate());
    }

    private handleNewMediaUpload(event: NewMediaUploadEvent) {
        event.getUploadItems().forEach((item: UploadItem<ContentSummary>) => {
            this.contentTreeGrid.appendUploadNode(item);
        });
    }

    private updateStatisticsPreview(el: ContentSummaryAndCompareStatus) {
        var content = el,
            previewItem = this.getBrowseItemPanel().getStatisticsItem();

        if (!!content && !!previewItem && content.getPath().toString() === previewItem.getPath()) {
            new api.content.page.IsRenderableRequest(el.getContentId()).sendAndParse().then((renderable: boolean) => {
                var item = new BrowseItem<ContentSummaryAndCompareStatus>(content).setId(content.getId()).setDisplayName(
                    content.getDisplayName()).setPath(content.getPath().toString()).setIconUrl(
                    new ContentIconUrlResolver().setContent(content.getContentSummary()).resolve()).setRenderable(renderable);
                this.getBrowseItemPanel().setStatisticsItem(item);
            });
        }
    }

    private updateDetailsPanel(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        var detailsPanel = ActiveDetailsPanelManager.getActiveDetailsPanel();
        return detailsPanel ? detailsPanel.setItem(item) : wemQ<any>(null);
    }

    private updateItemInDetailsPanelIfNeeded(item: ContentSummaryAndCompareStatus) {
        var detailsPanelItem: ContentSummaryAndCompareStatus = ActiveDetailsPanelManager.getActiveDetailsPanel().getItem();
        if (detailsPanelItem && (detailsPanelItem.getId() == item.getId())) {
            this.updateDetailsPanel(item);
        }
    }

    getBrowseItemPanel(): ContentBrowseItemPanel {
        return <ContentBrowseItemPanel>super.getBrowseItemPanel();
    }
}
