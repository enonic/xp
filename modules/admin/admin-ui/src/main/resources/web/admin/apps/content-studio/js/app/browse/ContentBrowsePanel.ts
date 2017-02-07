import '../../api.ts';
import {ContentTreeGridActions} from './action/ContentTreeGridActions';
import {ContentBrowseToolbar} from './ContentBrowseToolbar';
import {ContentTreeGrid} from './ContentTreeGrid';
import {ContentBrowseFilterPanel} from './filter/ContentBrowseFilterPanel';
import {ContentBrowseItemPanel} from './ContentBrowseItemPanel';
import {ContentItemStatisticsPanel} from '../view/ContentItemStatisticsPanel';
import {MobileContentItemStatisticsPanel} from '../view/MobileContentItemStatisticsPanel';
import {FloatingDetailsPanel} from '../view/detail/FloatingDetailsPanel';
import {DockedDetailsPanel} from '../view/detail/DockedDetailsPanel';
import {DetailsView} from '../view/detail/DetailsView';
import {NonMobileDetailsPanelsManager, NonMobileDetailsPanelsManagerBuilder} from '../view/detail/NonMobileDetailsPanelsManager';
import {Router} from '../Router';
import {ActiveDetailsPanelManager} from '../view/detail/ActiveDetailsPanelManager';
import {ContentBrowseItem} from './ContentBrowseItem';
import {ToggleSearchPanelEvent} from './ToggleSearchPanelEvent';
import {ToggleSearchPanelWithDependenciesEvent} from './ToggleSearchPanelWithDependenciesEvent';
import {NewMediaUploadEvent} from '../create/NewMediaUploadEvent';
import {ContentPreviewPathChangedEvent} from '../view/ContentPreviewPathChangedEvent';
import {ContentPublishMenuButton} from './ContentPublishMenuButton';
import {TreeNodeParentOfContent} from './TreeNodeParentOfContent';
import {TreeNodesOfContentPath} from './TreeNodesOfContentPath';

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
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;

export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummaryAndCompareStatus> {

    protected treeGrid: ContentTreeGrid;
    protected browseToolbar: ContentBrowseToolbar;
    protected filterPanel: ContentBrowseFilterPanel;

    private mobileContentItemStatisticsPanel: MobileContentItemStatisticsPanel;

    constructor() {

        super();

        this.onShown(() => {
            Router.setHash('browse');
        });

        ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            this.getBrowseActions().TOGGLE_SEARCH_PANEL.setVisible(item.isInRangeOrSmaller(ResponsiveRanges._360_540));
        });

        this.handleGlobalEvents();
    }

    private getBrowseActions(): ContentTreeGridActions {
        return <ContentTreeGridActions>this.treeGrid.getContextMenu().getActions();
    }

    protected createToolbar(): ContentBrowseToolbar {
        return new ContentBrowseToolbar(this.getBrowseActions());
    }

    protected createTreeGrid(): ContentTreeGrid {
        let treeGrid = new ContentTreeGrid();

        treeGrid.onDataChanged((event: api.ui.treegrid.DataChangedEvent<ContentSummaryAndCompareStatus>) => {
            if (event.getType() === 'updated') {
                let browseItems = this.treeNodesToBrowseItems(event.getTreeNodes());
                this.getBrowseItemPanel().updateItemViewers(browseItems);
                treeGrid.getContextMenu().getActions().updateActionsEnabledState(
                    this.treeNodesToBrowseItems(this.treeGrid.getRoot().getFullSelection()));
            }
        });

        return treeGrid;
    }

    protected createBrowseItemPanel(): ContentBrowseItemPanel {
        return new ContentBrowseItemPanel(this.treeGrid);
    }

    protected createFilterPanel(): ContentBrowseFilterPanel {
        let filterPanel = new ContentBrowseFilterPanel();

        let showMask = () => {
            if (this.isVisible()) {
                this.treeGrid.mask();
            }
        };

        filterPanel.onSearchStarted(showMask);
        filterPanel.onReset(showMask);
        filterPanel.onRefreshStarted(showMask);

        return filterPanel;
    }

    doRender(): wemQ.Promise<boolean> {
        return super.doRender().then((rendered) => {

            let detailsView = new DetailsView();

            let nonMobileDetailsPanelsManagerBuilder = NonMobileDetailsPanelsManager.create();
            this.initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder, detailsView);
            this.initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder, detailsView);
            this.initItemStatisticsPanelForMobile(detailsView);

            let nonMobileDetailsPanelsManager = nonMobileDetailsPanelsManagerBuilder.build();
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

            let contentPublishMenuButton = new ContentPublishMenuButton(this.getBrowseActions());

            this.browseToolbar.appendChild(nonMobileDetailsPanelsManager.getToggleButton());
            this.browseToolbar.appendChild(contentPublishMenuButton);

            this.subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager, contentPublishMenuButton);

            return rendered;
        }).catch((error) => {
            console.error(`Couldn't render ContentBrowsePanel`, error);
            return true;
        });
    }

    private subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager: NonMobileDetailsPanelsManager,
                                           contentPublishMenuButton: ContentPublishMenuButton) {

        this.getTreeGrid().onSelectionChanged((currentSelection: TreeNode<ContentSummaryAndCompareStatus>[],
                                               fullSelection: TreeNode<ContentSummaryAndCompareStatus>[]) => {
            let item = this.getFirstSelectedBrowseItem(fullSelection);
            this.doUpdateDetailsPanel(item ? item.getModel() : null);
        });

        ResponsiveManager.onAvailableSizeChanged(this.getFilterAndGridSplitPanel(), (item: ResponsiveItem) => {
            nonMobileDetailsPanelsManager.handleResizeEvent();
        });

        ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            if (ResponsiveRanges._540_720.isFitOrBigger(item.getOldRangeValue())) {
                contentPublishMenuButton.maximize();
                if (item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    nonMobileDetailsPanelsManager.hideActivePanel();
                    ActiveDetailsPanelManager.setActiveDetailsPanel(this.mobileContentItemStatisticsPanel.getDetailsPanel());
                }
            } else {
                contentPublishMenuButton.minimize();
            }
        });
    }

    private initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder,
                                            detailsPanelView: DetailsView) {

        let dockedDetailsPanel = new DockedDetailsPanel(detailsPanelView);

        let contentPanelsAndDetailPanel: api.ui.panel.SplitPanel = new api.ui.panel.SplitPanelBuilder(this.getFilterAndGridSplitPanel(),
            dockedDetailsPanel).setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL).setSecondPanelSize(280,
            api.ui.panel.SplitPanelUnit.PIXEL).setSecondPanelMinSize(280, api.ui.panel.SplitPanelUnit.PIXEL).setAnimationDelay(
            600).setSecondPanelShouldSlideRight(true).build();

        contentPanelsAndDetailPanel.addClass('split-panel-with-details');
        contentPanelsAndDetailPanel.setSecondPanelSize(280, api.ui.panel.SplitPanelUnit.PIXEL);

        nonMobileDetailsPanelsManagerBuilder.setSplitPanelWithGridAndDetails(contentPanelsAndDetailPanel);
        nonMobileDetailsPanelsManagerBuilder.setDefaultDetailsPanel(dockedDetailsPanel);

        this.appendChild(contentPanelsAndDetailPanel);
    }

    private initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder, detailsView: DetailsView) {

        let floatingDetailsPanel = new FloatingDetailsPanel(detailsView);

        nonMobileDetailsPanelsManagerBuilder.setFloatingDetailsPanel(floatingDetailsPanel);

        this.appendChild(floatingDetailsPanel);
    }

    private initItemStatisticsPanelForMobile(detailsView: DetailsView) {
        this.mobileContentItemStatisticsPanel = new MobileContentItemStatisticsPanel(this.getBrowseActions(), detailsView);

        let updateMobilePanel = () => {
            const browseItem = this.getFirstSelectedBrowseItem();
            const item = browseItem.toViewItem();

            if (this.itemChanged()) {
                this.mobileContentItemStatisticsPanel.getPreviewPanel().showMask();
                this.mobileContentItemStatisticsPanel.setItem(item);

                setTimeout(() => {
                    this.mobileContentItemStatisticsPanel.getPreviewPanel().setBlankFrame();
                    this.mobileContentItemStatisticsPanel.getPreviewPanel().showMask();
                    new api.content.page.IsRenderableRequest(new api.content.ContentId(browseItem.getId())).sendAndParse().then(
                        (renderable: boolean) => {
                            item.setRenderable(renderable);
                            this.mobileContentItemStatisticsPanel.getPreviewPanel().setItem(item);
                        });
                }, 300);
            }
        };

        api.ui.treegrid.TreeGridItemClickedEvent.on((event) => {
            if (this.isSomethingSelectedInMobileMode()) {
                if (this.itemChanged()) {
                    this.mobileContentItemStatisticsPanel.getPreviewPanel().setBlank();
                }
                this.mobileContentItemStatisticsPanel.slideIn();
                updateMobilePanel();
            }
        });

        this.appendChild(this.mobileContentItemStatisticsPanel);
    }

    private itemChanged(): boolean {
        const prevItem = this.mobileContentItemStatisticsPanel.getPreviewPanel().getItem();
        const browseItem = this.getFirstSelectedBrowseItem();
        return !prevItem || !prevItem.getModel() || prevItem.getModel().getId() !== browseItem.getId();
    }

    // tslint:disable-next-line:max-line-length
    private getFirstSelectedBrowseItem(fullSelection?: TreeNode<ContentSummaryAndCompareStatus>[]): BrowseItem<ContentSummaryAndCompareStatus> {
        let browseItems: BrowseItem<ContentSummaryAndCompareStatus>[] = this.treeNodesToBrowseItems(!!fullSelection
                ? fullSelection
                : this.treeGrid.getRoot().getFullSelection());
        let item: BrowseItem<ContentSummaryAndCompareStatus> = null;
        if (browseItems.length > 0) {
            item = browseItems[0];
        }
        return item;
    }

    private isSomethingSelected(): boolean {
        return this.getFirstSelectedBrowseItem() != null;
    }

    private isMobileMode(): boolean {
        // return ActiveDetailsPanelManager.getActiveDetailsPanel() === this.mobileContentItemStatisticsPanel.getDetailsPanel();
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
        let browseItems: ContentBrowseItem[] = [];

        // do not proceed duplicated content. still, it can be selected
        nodes.forEach((node: TreeNode<ContentSummaryAndCompareStatus>, index: number) => {
            let i = 0;
            for (; i <= index; i++) {
                if (nodes[i].getData().getId() === node.getData().getId()) {
                    break;
                }
            }
            if (i === index) {
                let data = node.getData();
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
            this.filterPanel.setDependencyItem(event.getContent(), event.isInbound());
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
        let path = this.getPathFromPreviewPath(contentPreviewPath);
        if (path) {
            let contentPath = api.content.ContentPath.fromString(path);
            if (this.isSingleItemSelectedInGrid() && !this.isGivenPathSelectedInGrid(contentPath)) {
                this.selectContentInGridByPath(contentPath);
            }
        }
    }

    private selectContentInGridByPath(path: api.content.ContentPath) {
        this.treeGrid.selectNodeByPath(path);
    }

    private isGivenPathSelectedInGrid(path: api.content.ContentPath): boolean {
        let contentSummary: ContentSummaryAndCompareStatus = this.treeGrid.getSelectedNodes()[0].getData();
        return contentSummary.getPath().equals(path);
    }

    private isSingleItemSelectedInGrid(): boolean {
        return this.treeGrid.getSelectedNodes() && this.treeGrid.getSelectedNodes().length === 1;
    }

    private getPathFromPreviewPath(contentPreviewPath: string): string {
        return api.rendering.UriHelper.getPathFromPortalPreviewUri(contentPreviewPath, api.rendering.RenderingMode.PREVIEW,
            api.content.Branch.DRAFT);
    }

    private subscribeOnContentEvents() {
        let handler = ContentServerEventsHandler.getInstance();

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
            console.debug('ContentBrowsePanel: created', data, oldPaths);
        }

        this.processContentCreated(data, oldPaths);
    }

    private handleContentUpdated(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug('ContentBrowsePanel: updated', data);
        }

        return this.doHandleContentUpdate(data).then((changed) => {
            this.updateStatisticsPanel(data);

            return this.treeGrid.placeContentNodes(changed);
        });
    }

    private handleContentDeleted(paths: ContentPath[]) {
        if (ContentBrowsePanel.debug) {
            console.debug('ContentBrowsePanel: deleted', paths);
        }

        let nodes = this.treeGrid.findByPaths(paths).map(el => el.getNodes());
        let merged = [];
        // merge array of nodes arrays
        merged = merged.concat.apply(merged, nodes);

        merged.forEach((node: TreeNode<ContentSummaryAndCompareStatus>) => {
            let contentSummary = node.getData().getContentSummary();
            if (node.getData() && !!contentSummary) {
                this.doUpdateDetailsPanel(null);
            }
        });

        this.treeGrid.deleteContentNodes(merged);

        // now get unique parents and update their hasChildren
        let uniqueParents = paths.map(path => path.getParentPath()).filter((parent, index, self) => {
            return self.indexOf(parent) === index;
        });
        let parentNodes = this.treeGrid.findByPaths(uniqueParents).map(parentNode => parentNode.getNodes());
        let mergedParentNodes = [];
        mergedParentNodes = mergedParentNodes.concat.apply(mergedParentNodes, parentNodes);

        mergedParentNodes.forEach((parentNode: TreeNode<ContentSummaryAndCompareStatus>) => {
            if (parentNode.getChildren().length === 0) {
                // update parent if all children were deleted
                this.treeGrid.refreshNodeData(parentNode);
            }
        });

        this.setRefreshOfFilterRequired();
        window.setTimeout(() => {
            this.refreshFilter();
        }, 1000);
    }

    private handleContentPending(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug('ContentBrowsePanel: pending', data);
        }
        this.doHandleContentUpdate(data);
    }

    private handleContentPublished(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug('ContentBrowsePanel: published', data);
        }
        this.doHandleContentUpdate(data);
    }

    private handleContentUnpublished(data: ContentSummaryAndCompareStatus[]) {
        if (ContentBrowsePanel.debug) {
            console.debug('ContentBrowsePanel: unpublished', data);
        }
        this.doHandleContentUpdate(data);
    }

    private processContentCreated(data: ContentSummaryAndCompareStatus[], oldPaths?: ContentPath[]) {

        let paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        let createResult: TreeNodesOfContentPath[] = this.treeGrid.findByPaths(paths, true);
        let parentsOfContents: TreeNodeParentOfContent[] = [];

        for (let i = 0; i < createResult.length; i++) {

            let dataToHandle: ContentSummaryAndCompareStatus[] = [];

            data.forEach((el) => {

                if (el.getContentSummary().getPath().isChildOf(createResult[i].getPath())) {

                    if (oldPaths && oldPaths.length > 0) {
                        let movedNodes: TreeNode<ContentSummaryAndCompareStatus>[] = [];
                        let renameResult: TreeNodesOfContentPath[] = this.treeGrid.findByPaths(oldPaths);
                        let premerged = renameResult.map((curRenameResult) => {
                            return curRenameResult.getNodes();
                        });
                        // merge array of nodes arrays
                        movedNodes = movedNodes.concat.apply(movedNodes, premerged);

                        movedNodes.forEach((node) => {
                            if (node.getDataId() === el.getId()) {
                                node.setData(el);
                                node.clearViewers();
                                this.treeGrid.updatePathsInChildren(node);
                            }
                        });
                        this.treeGrid.placeContentNodes(movedNodes);
                    } else {
                        dataToHandle.push(el);
                    }
                }
            });

            createResult[i].getNodes().map((node) => {
                parentsOfContents.push(new TreeNodeParentOfContent(dataToHandle, node));
            });
        }

        this.treeGrid.appendContentNodes(parentsOfContents).then((results: TreeNode<ContentSummaryAndCompareStatus>[]) => {
            let appendedNodesThatShouldBeVisible = [];
            results.forEach((appendedNode) => {
                if (appendedNode.getParent() && appendedNode.getParent().isExpanded()) {
                    appendedNodesThatShouldBeVisible.push(appendedNode);
                }
            });

            this.treeGrid.placeContentNodes(appendedNodesThatShouldBeVisible).then(() => {
                this.treeGrid.initAndRender();

                this.setRefreshOfFilterRequired();
                window.setTimeout(() => {
                    this.refreshFilter();
                }, 1000);
            });
        });
    }

    private doHandleContentUpdate(data: ContentSummaryAndCompareStatus[]): wemQ.Promise<TreeNode<ContentSummaryAndCompareStatus>[]> {
        let changed = this.updateNodes(data);

        this.updateDetailsPanel(data);

        this.treeGrid.invalidate();

        // Update since CompareStatus changed
        return ContentSummaryAndCompareStatusFetcher.updateReadOnly(changed.map(node => node.getData())).then(() => {

            let changedEvent = new DataChangedEvent<ContentSummaryAndCompareStatus>(changed, DataChangedEvent.UPDATED);
            this.treeGrid.notifyDataChanged(changedEvent);

            return changed;
        });
    }

    private updateNodes(data: ContentSummaryAndCompareStatus[]): TreeNode<ContentSummaryAndCompareStatus>[] {
        let paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        let treeNodes: TreeNodesOfContentPath[] = this.treeGrid.findByPaths(paths);

        let changed = [];
        data.forEach((el) => {
            for (let i = 0; i < treeNodes.length; i++) {
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
            console.debug('ContentBrowsePanel: sorted', data);
        }
        let paths: api.content.ContentPath[] = data.map(d => d.getContentSummary().getPath());
        let sortResult: TreeNodesOfContentPath[] = this.treeGrid.findByPaths(paths);

        let nodes = sortResult.map((el) => {
            return el.getNodes();
        });
        let merged = [];
        // merge array of nodes arrays
        merged = merged.concat.apply(merged, nodes);

        this.treeGrid.sortNodesChildren(merged).then(() => this.treeGrid.invalidate());
    }

    private handleNewMediaUpload(event: NewMediaUploadEvent) {
        event.getUploadItems().forEach((item: UploadItem<ContentSummary>) => {
            this.treeGrid.appendUploadNode(item);
        });
    }

    private updateStatisticsPanel(data: ContentSummaryAndCompareStatus[]) {
        let previewItem = this.getBrowseItemPanel().getStatisticsItem();

        if (!previewItem) {
            return;
        }

        let toBrowseItem = (content: ContentSummaryAndCompareStatus, renderable: boolean) => {
            let item = new BrowseItem<ContentSummaryAndCompareStatus>(content).setId(content.getId()).setDisplayName(
                content.getDisplayName()).setPath(content.getPath().toString()).setIconUrl(
                new api.content.util.ContentIconUrlResolver().setContent(content.getContentSummary()).resolve()).setRenderable(
                renderable);

            return item;
        };

        let isChildFragment = (possibleChildFragment: ContentSummaryAndCompareStatus, possibleParent: ContentSummaryAndCompareStatus) => {
            return possibleChildFragment.getType().equals(api.schema.content.ContentTypeName.FRAGMENT) &&
                possibleChildFragment.getPath().isChildOf(possibleParent.getPath());
        };

        data.some((content: ContentSummaryAndCompareStatus) => {
            if (content.getPath().toString() === previewItem.getPath()) {
                new api.content.page.IsRenderableRequest(content.getContentId()).sendAndParse().then((renderable: boolean) => {
                    this.getBrowseItemPanel().setStatisticsItem(toBrowseItem(content, renderable));
                });
                return true;
            } else if (isChildFragment(content, previewItem.getModel())) {
                // child fragment was updated - preview refresh is needed
                (<ContentItemStatisticsPanel>this.getBrowseItemPanel().getPanel(1)).getPreviewPanel().setItem(previewItem, true);
                this.mobileContentItemStatisticsPanel.getPreviewPanel().setItem(previewItem, true);
                return true;
            }
        });
    }

    private updateDetailsPanel(data: ContentSummaryAndCompareStatus[]) {
        let detailsPanel = ActiveDetailsPanelManager.getActiveDetailsPanel();
        let itemInDetailPanel = detailsPanel ? detailsPanel.getItem() : null;

        if (!itemInDetailPanel) {
            return;
        }

        let content: ContentSummaryAndCompareStatus;
        let detailsPanelNeedsUpdate = data.some((contentItem: ContentSummaryAndCompareStatus) => {
            if (contentItem.getId() === itemInDetailPanel.getId()) {
                content = contentItem;
                return true;
            }
        });

        if (detailsPanelNeedsUpdate) {
            this.doUpdateDetailsPanel(content);
        }
    }

    private doUpdateDetailsPanel(item: ContentSummaryAndCompareStatus) {
        let detailsPanel = ActiveDetailsPanelManager.getActiveDetailsPanel();
        if (detailsPanel) {
            detailsPanel.setItem(item);
        }
    }

    getBrowseItemPanel(): ContentBrowseItemPanel {
        return <ContentBrowseItemPanel>super.getBrowseItemPanel();
    }
}
