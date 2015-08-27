module app.wizard {

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

    export class PageComponentsTreeGrid extends TreeGrid<ItemView> {

        private pageView: PageView;
        private content: Content;

        constructor(content: Content, pageView: PageView) {
            this.content = content;
            this.pageView = pageView;

            super(new TreeGridBuilder<ItemView>().
                setColumns([
                    new GridColumnBuilder<TreeNode<ItemView>>().
                        setName("Name").
                        setId("displayName").
                        setField("displayName").
                        setFormatter(this.nameFormatter.bind(this)).
                        setMinWidth(250).
                        build()
                ]).
                setOptions(
                new GridOptionsBuilder<TreeNode<ItemView>>().
                    setAutoHeight(true).
                    setShowHeaderRow(false).
                    setHideColumnHeaders(true).
                    setForceFitColumns(true).
                    setFullWidthRows(true).
                    setRowHeight(45).
                    setCheckableRows(false).
                    setMultiSelect(false).
                    build()).
                setShowToolbar(false).
                setAutoLoad(true).
                setExpandAll(true).
                prependClasses("components-grid"));
        }

        setPageView(pageView: PageView) {
            this.pageView = pageView;
            this.reload();
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ItemView>) {
            var viewer = <PageComponentsItemViewer>node.getViewer("name");
            if (!viewer) {
                var viewer = new PageComponentsItemViewer(this.content);
                viewer.setObject(node.getData());
                node.setViewer("name", viewer);
            }
            return viewer.toString();
        }

        getDataId(data: ItemView): string {
            return data.getItemId().toString();
        }

        hasChildren(data: ItemView): boolean {
            return this.getDataChildren(data).length > 0
        }

        fetch(node: TreeNode<ItemView>, dataId?: string): Q.Promise<ItemView> {
            var deferred = wemQ.defer<ItemView>();
            var itemViewId = dataId ? new api.liveedit.ItemViewId(parseInt(dataId)) : node.getData().getItemId();
            deferred.resolve(this.pageView.getItemViewById(itemViewId));
            return deferred.promise;
        }

        fetchRoot(): wemQ.Promise<ItemView[]> {
            var deferred = wemQ.defer<ItemView[]>();
            deferred.resolve([this.pageView]);
            return deferred.promise;
        }

        fetchChildren(parentNode: TreeNode<ItemView>): Q.Promise<ItemView[]> {
            var deferred = wemQ.defer<ItemView[]>();
            deferred.resolve(this.getDataChildren(parentNode.getData()));
            return deferred.promise;
        }

        private getDataChildren(data: ItemView): ItemView[] {
            var children = [];
            var dataType = data.getType();
            if (PageItemType.get().equals(dataType)) {
                var pageView = <PageView> data;
                children = pageView.getRegions();
            } else if (RegionItemType.get().equals(dataType)) {
                var regionView = <RegionView> data;
                children = regionView.getComponentViews();
            } else if (LayoutItemType.get().equals(dataType)) {
                var layoutView = <LayoutComponentView> data;
                children = layoutView.getRegions();
            }
            return children;
        }

    }
}
