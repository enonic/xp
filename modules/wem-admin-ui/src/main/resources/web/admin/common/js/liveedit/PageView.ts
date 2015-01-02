module api.liveedit {

    import Content = api.content.Content;
    import Page = api.content.page.Page;
    import PageModel = api.content.page.PageModel;
    import Site = api.content.site.Site;
    import PageRegions = api.content.page.PageRegions;
    import Component = api.content.page.Component;
    import Region = api.content.page.region.Region;

    export class PageViewBuilder {

        liveEditModel: LiveEditModel;

        itemViewProducer: ItemViewIdProducer;

        element: api.dom.Body;

        setLiveEditModel(value: LiveEditModel): PageViewBuilder {
            this.liveEditModel = value;
            return this;
        }

        setItemViewProducer(value: ItemViewIdProducer): PageViewBuilder {
            this.itemViewProducer = value;
            return this;
        }

        setElement(value: api.dom.Body): PageViewBuilder {
            this.element = value;
            return this;
        }

        build(): PageView {
            return new PageView(this);
        }
    }

    export class PageView extends ItemView {

        private placeholder: PagePlaceholder;

        private regionViews: RegionView[];

        private viewsById: {[s:number] : ItemView;};

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        constructor(builder: PageViewBuilder) {

            this.liveEditModel = builder.liveEditModel;
            this.regionViews = [];
            this.viewsById = {};
            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];
            super(new ItemViewBuilder().
                setLiveEditModel(builder.liveEditModel).
                setItemViewIdProducer(builder.itemViewProducer).
                setType(PageItemType.get()).
                setElement(builder.element).
                setParentElement(builder.element.getParentElement()).
                setContextMenuActions(this.createPageContextMenuActions()).
                setContextMenuTitle(new PageViewContextMenuTitle(builder.liveEditModel.getContent())));
            this.setTooltipObject(builder.liveEditModel.getContent());
            this.parseItemViews();

            var arrayOfItemViews = this.toItemViewArray();
            arrayOfItemViews.forEach((itemView: ItemView) => {
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

            this.placeholder = new PagePlaceholder(this);
            this.refreshPlaceholder();
        }

        private refreshPlaceholder() {
            if (this.conditionedForEmpty()) {
                this.appendChild(this.placeholder);
                this.placeholder.select();
                this.markAsEmpty();
            }
        }

        conditionedForEmpty(): boolean {

            if (this.liveEditModel.getContent().isPageTemplate()) {
                return !this.liveEditModel.getPageModel().hasController();
            }
            else {
                return !this.liveEditModel.getPageModel().hasTemplate() && !this.liveEditModel.getPageModel().isUsingDefaultTemplate();
            }
        }

        private createPageContextMenuActions() {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Empty').onExecuted(() => {
                // TODO
            }));
            return actions;
        }

        getName(): string {
            return this.liveEditModel.getContent() ? this.liveEditModel.getContent().getDisplayName() : "[No name]";
        }

        getParentItemView(): ItemView {
            return null;
        }

        select(clickPosition?: Position) {
            new PageSelectEvent(this).fire();
            super.select(clickPosition);
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
            //console.log("GETTING VIEWS", this.viewsById);
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

        getPageComponentViewByElement(element: HTMLElement): PageComponentView<Component> {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            var itemView = this.getItemViewById(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());
            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageComponentView)) {
                return <PageComponentView<Component>>itemView;
            }
            return null;
        }

        private registerItemView(view: ItemView) {

            // logging...
            var extra = "";
            if (api.ObjectHelper.iFrameSafeInstanceOf(view, PageComponentView)) {
                var pageComponentView = <PageComponentView<Component>>view;
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

            this.notifyItemViewAdded(new ItemViewAddedEvent(view));
        }

        private unregisterItemView(view: ItemView) {
            console.debug("PageView.unregisterItemView: " + view.getItemId().toNumber());
            delete this.viewsById[view.getItemId().toNumber()];

            this.notifyItemViewRemoved(new ItemViewRemovedEvent(view));
        }

        private parseItemViews() {

            this.doParseItemViews();
        }

        private doParseItemViews(parentElement?: api.dom.Element) {

            var regions: Region[] = this.liveEditModel.getPageModel().getRegions().getRegions();
            var children = parentElement ? parentElement.getChildren() : this.getChildren();
            var regionIndex = 0;
            children.forEach((element: api.dom.Element) => {
                var itemType = ItemType.fromElement(element);
                if (itemType) {
                    if (RegionItemType.get().equals(itemType)) {
                        var region = regions[regionIndex++];
                        if (region) {
                            var regionView = new RegionView(new RegionViewBuilder().
                                setLiveEditModel(this.liveEditModel).
                                setParentView(this).
                                setRegion(region).
                                setElement(element));
                            this.addRegion(regionView);
                            regionView.parsePageComponentViews();
                        }
                    }
                    else {
                        this.doParseItemViews(parentElement);
                    }
                }
            });
        }

        onItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners.push(listener);
        }

        unItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners = this.itemViewAddedListeners.filter((current) => (current != listener));
        }

        private notifyItemViewAdded(event: ItemViewAddedEvent) {
            this.itemViewAddedListeners.forEach((listener) => listener(event));
        }

        onItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners.push(listener);
        }

        unItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners = this.itemViewRemovedListeners.filter((current) => (current != listener));
        }

        private notifyItemViewRemoved(event: ItemViewRemovedEvent) {
            this.itemViewRemovedListeners.forEach((listener) => listener(event));
        }
    }
}