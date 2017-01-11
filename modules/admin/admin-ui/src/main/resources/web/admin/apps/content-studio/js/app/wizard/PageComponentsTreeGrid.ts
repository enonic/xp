import "../../api.ts";
import {PageComponentsItemViewer} from "./PageComponentsItemViewer";
import {PageComponentsGridDragHandler} from "./PageComponentsGridDragHandler";

import GridColumn = api.ui.grid.GridColumn;
import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;

import TreeGrid = api.ui.treegrid.TreeGrid;
import TreeNode = api.ui.treegrid.TreeNode;
import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;

import ItemView = api.liveedit.ItemView;
import PageView = api.liveedit.PageView;
import PageItemType = api.liveedit.PageItemType;
import RegionItemType = api.liveedit.RegionItemType;
import RegionView = api.liveedit.RegionView;
import LayoutItemType = api.liveedit.layout.LayoutItemType;
import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
import Content = api.content.Content;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class PageComponentsTreeGrid extends TreeGrid<ItemView> {

    private pageView: PageView;
    private content: Content;

    private gridDragHandler: PageComponentsGridDragHandler;

    constructor(content: Content, pageView: PageView) {
        super(new TreeGridBuilder<ItemView>().setColumns([
            new GridColumnBuilder<TreeNode<ItemView>>().setName("Name").setId("displayName").setField("displayName").setFormatter(
                PageComponentsTreeGrid.nameFormatter.bind(null, content)).setMinWidth(250).setBehavior("selectAndMove").build(),
            new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().setName("Menu").setId("menu").setMinWidth(45).setMaxWidth(
                45).setField("menu").setCssClass("menu-cell").setFormatter(PageComponentsTreeGrid.menuFormatter).build()
        ]).setOptions(
            new GridOptionsBuilder<TreeNode<ItemView>>().setAutoHeight(true).setShowHeaderRow(false).setHideColumnHeaders(
                true).setForceFitColumns(true).setFullWidthRows(true).

            // It is necessary to turn off the library key handling. It may cause
            // the conflicts with Mousetrap, which leads to skipping the key events
            // Do not set to true, if you are not fully aware of the result
            setEnableCellNavigation(false).setSelectedCellCssClass("selected cell").setCheckableRows(false).disableMultipleSelection(
                true).setMultiSelect(false).setRowHeight(45).setDragAndDrop(true).build()
        ).setShowToolbar(false).setAutoLoad(true).setExpandAll(true).prependClasses("components-grid"));

        this.content = content;
        this.pageView = pageView;

        this.gridDragHandler = new PageComponentsGridDragHandler(this);
    }

    queryScrollable(): api.dom.Element {
        return this;
    }

    setPageView(pageView: PageView): wemQ.Promise<void> {
        this.pageView = pageView;
        return this.reload();
    }

    public static nameFormatter(content: Content, row: number, cell: number, value: any, columnDef: any, node: TreeNode<ItemView>) {
        let viewer = <PageComponentsItemViewer>node.getViewer("name");
        if (!viewer) {
            viewer = new PageComponentsItemViewer(content);
            const data = node.getData();

            viewer.setObject(data);
            node.setViewer("name", viewer);
            if (!(api.ObjectHelper.iFrameSafeInstanceOf(data, RegionView) || api.ObjectHelper.iFrameSafeInstanceOf(data, PageView))) {
                viewer.addClass("draggable");
            }
        }
        return viewer.toString();
    }

    setInvalid(dataIds: string[]) {
        let root = this.getRoot().getCurrentRoot();
        let stylesHash: Slick.CellCssStylesHash = {};

        dataIds.forEach((dataId) => {
            let node = root.findNode(dataId);
            if (node) {
                let row = this.getGrid().getDataView().getRowById(node.getId());
                stylesHash[row] = {displayName: "invalid", menu: "invalid"};
            }
        });
        this.getGrid().setCellCssStyles("invalid-highlight", stylesHash);
    }

    getDataId(data: ItemView): string {
        return data.getItemId().toString();
    }

    hasChildren(data: ItemView): boolean {
        return this.getDataChildren(data).length > 0;
    }

    fetch(node: TreeNode<ItemView>, dataId?: string): Q.Promise<ItemView> {
        let deferred = wemQ.defer<ItemView>();
        let itemViewId = dataId ? new api.liveedit.ItemViewId(parseInt(dataId, 10)) : node.getData().getItemId();
        deferred.resolve(this.pageView.getItemViewById(itemViewId));
        return deferred.promise;
    }

    fetchRoot(): wemQ.Promise<ItemView[]> {
        let deferred = wemQ.defer<ItemView[]>();
        if (this.pageView.getFragmentView()) {
            deferred.resolve([this.pageView.getFragmentView()]);
        } else {
            deferred.resolve([this.pageView]);
        }
        return deferred.promise;
    }

    fetchChildren(parentNode: TreeNode<ItemView>): Q.Promise<ItemView[]> {
        let deferred = wemQ.defer<ItemView[]>();
        deferred.resolve(this.getDataChildren(parentNode.getData()));
        return deferred.promise;
    }

    private getDataChildren(data: ItemView): ItemView[] {
        let children = [];
        let dataType = data.getType();
        if (PageItemType.get().equals(dataType)) {
            let pageView = <PageView> data;
            children = pageView.getRegions();
            if (children.length === 0) {
                let fragmentRoot = pageView.getFragmentView();
                if (fragmentRoot) {
                    return [fragmentRoot];
                }
            }
        } else if (RegionItemType.get().equals(dataType)) {
            let regionView = <RegionView> data;
            children = regionView.getComponentViews();
        } else if (LayoutItemType.get().equals(dataType)) {
            let layoutView = <LayoutComponentView> data;
            children = layoutView.getRegions();
        }
        return children;
    }

    public static menuFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
        let wrapper = new api.dom.SpanEl();

        let icon = new api.dom.DivEl("menu-icon");
        wrapper.getEl().setInnerHtml(icon.toString(), false);
        return wrapper.toString();
    }

}
