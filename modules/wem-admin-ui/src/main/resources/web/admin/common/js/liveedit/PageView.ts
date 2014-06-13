module api.liveedit {

    import Content = api.content.Content;
    import PageRegions = api.content.page.PageRegions;
    import PageComponent = api.content.page.PageComponent;
    import Region = api.content.page.region.Region;

    export class PageViewBuilder {

        itemViewProducer: ItemViewIdProducer;

        pageRegions: PageRegions;

        content: Content;

        element: HTMLElement;

        setItemViewProducer(value: ItemViewIdProducer): PageViewBuilder {
            this.itemViewProducer = value;
            return this;
        }

        setPageRegions(value: PageRegions): PageViewBuilder {
            this.pageRegions = value;
            return this;
        }

        setContent(value: Content): PageViewBuilder {
            this.content = value;
            return this;
        }

        setElement(value: HTMLElement): PageViewBuilder {
            this.element = value;
            return this;
        }
    }

    export class PageView extends ItemView {

        private content: Content;

        private pageRegions: PageRegions;

        private regionViews: RegionView[] = [];

        private viewsById: {[s:number] : ItemView;} = {};

        constructor(builder: PageViewBuilder) {
            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.itemViewProducer).
                setType(PageItemType.get()).
                setElement(builder.element));
            this.setContent(builder.content);
            this.pageRegions = builder.pageRegions;
            this.parseItemViews();

            var arrayofItemViews = this.toItemViewArray();
            arrayofItemViews.forEach((itemView: ItemView) => {
                this.registerItemView(itemView);
            });

            this.regionViews.forEach((regionView: RegionView) => {
                regionView.onItemViewAdded((event: ItemViewAddedEvent) => {
                    this.registerItemView(event.getView());
                });
                regionView.onItemViewRemoved((event: ItemViewRemovedEvent) => {
                    this.unregisterItemView(event.getView());
                });
            });
        }

        getName(): string {

            return this.content.getDisplayName();
        }

        private setContent(content: Content) {
            this.content = content;
            if (content) {
                this.setTooltipObject(content);
            }
        }

        getParentItemView(): ItemView {
            return null;
        }

        select() {
            new PageSelectEvent(this).fire();
            super.select();
        }

        getTooltipViewer(): api.ui.Viewer<api.content.ContentSummary> {
            return new api.content.ContentSummaryViewer();
        }

        addRegion(regionView: RegionView) {
            this.regionViews.push(regionView);
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.regionViews.forEach((regionView: RegionView) => {
                var itemViews = regionView.toItemViewArray();
                array = array.concat(itemViews);
            });
            return array;
        }

        hasSelectedView(): boolean {
            return !!this.getSelectedView();
        }

        getSelectedView(): ItemView {
            for (var id in this.viewsById) {
                if (this.viewsById.hasOwnProperty(id) && this.viewsById[id].isSelected()) {
                    return this.viewsById[id];
                }
            }
            return null;
        }

        deselectSelectedView() {
            var selectedView = this.getSelectedView();
            if (selectedView) {
                this.getSelectedView().deselect();
            }
        }

        getItemViewById(id: ItemViewId): ItemView {
            api.util.assertNotNull(id, "value cannot be null");
            return this.viewsById[id.toNumber()];
        }

        getItemViewByElement(element: HTMLElement): ItemView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            var itemView = this.getItemViewById(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());

            return  itemView;
        }

        getRegionViewByElement(element: HTMLElement): RegionView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            var itemView = this.getItemViewById(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());

            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, RegionView)) {
                return <RegionView>itemView;
            }
            return null;
        }

        getPageComponentViewByElement(element: HTMLElement): PageComponentView<PageComponent> {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            var itemView = this.getItemViewById(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());
            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageComponentView)) {
                return <PageComponentView<PageComponent>>itemView;
            }
            return null;
        }

        private registerItemView(view: ItemView) {

            // logging...
            var extra = "";
            if (api.ObjectHelper.iFrameSafeInstanceOf(view, PageComponentView)) {
                var pageComponentView = <PageComponentView<PageComponent>>view;
                if (pageComponentView.hasComponentPath()) {
                    extra = pageComponentView.getComponentPath().toString();
                }
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(view, RegionView)) {
                var regionView = <RegionView>view;
                extra = regionView.getRegionPath().toString();
            }

            console.debug("PageView.registerItemView: " + view.getItemId().toNumber() + " : " + view.getType().getShortName() + " : " +
                          extra);
            this.viewsById[view.getItemId().toNumber()] = view;
        }

        private unregisterItemView(view: ItemView) {
            console.debug("PageView.unregisterItemView: " + view.getItemId().toNumber());
            delete this.viewsById[view.getItemId().toNumber()];
        }

        private parseItemViews() {

            this.doParseItemViews();
        }

        private doParseItemViews(parentElement?: api.dom.Element) {

            var regions: Region[] = this.pageRegions.getRegions();
            var children = parentElement ? parentElement.getChildren() : this.getChildren();
            var regionIndex = 0;
            children.forEach((element: api.dom.Element) => {
                var itemType = ItemType.fromElement(element);
                if (itemType) {
                    if (RegionItemType.get().equals(itemType)) {
                        var region = regions[regionIndex++];
                        var regionView = new RegionView(new RegionViewBuilder().
                            setParentView(this).
                            setRegion(region).
                            setElement(element.getHTMLElement()));
                        this.addRegion(regionView);
                    }
                    else {
                        this.doParseItemViews(parentElement);
                    }
                }
            });
        }
    }
}