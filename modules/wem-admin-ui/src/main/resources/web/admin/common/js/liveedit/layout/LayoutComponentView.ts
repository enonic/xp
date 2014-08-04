module api.liveedit.layout {

    import PageComponent = api.content.page.PageComponent;
    import Region = api.content.page.region.Region;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;

    export class LayoutComponentViewBuilder extends PageComponentViewBuilder<LayoutComponent> {

        constructor() {
            super();
            this.setType(LayoutItemType.get());
        }
    }

    export class LayoutComponentView extends PageComponentView<LayoutComponent> {

        private layoutComponent: LayoutComponent;

        private placeholder: LayoutPlaceholder;

        private regionViews: RegionView[];

        constructor(builder: LayoutComponentViewBuilder) {
            this.regionViews = [];
            super(builder);
            this.layoutComponent = builder.pageComponent;

            this.placeholder = new LayoutPlaceholder(this);
            if (this.conditionedForEmpty()) {
                this.displayPlaceholder();
            }

            this.parseRegions();
        }

        setPageComponent(layoutComponent: LayoutComponent) {
            super.setPageComponent(layoutComponent);
            var regions = layoutComponent.getLayoutRegions().getRegions();
            this.regionViews.forEach((regionView: RegionView, index: number) => {
                var region = regions[index];
                regionView.setRegion(region);
            });
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);
            if (this.isEmpty()) {
                this.placeholder.select();
            }
        }

        deselect() {
            super.deselect();
            if (this.isEmpty()) {
                this.placeholder.deselect();
            }
        }

        private conditionedForEmpty(): boolean {
            if (!this.layoutComponent) {
                return super.isEmpty();
            }
            return this.isEmpty() || !this.layoutComponent.getDescriptor();
        }

        showLoadingPlaceholder() {
            this.displayPlaceholder();
            this.select();
            this.placeholder.deselect();
        }

        displayPlaceholder() {
            this.markAsEmpty();

            this.removeChildren();
            this.appendChild(this.placeholder);
        }

        duplicate(duplicate: LayoutComponent): LayoutComponentView {

            var duplicatedView = new LayoutComponentView(new LayoutComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setPageComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

        getTooltipViewer(): LayoutComponentViewer {
            return new LayoutComponentViewer();
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.regionViews.forEach((regionView: RegionView) => {
                var itemsInRegion = regionView.toItemViewArray();
                array = array.concat(itemsInRegion);
            });
            return array;
        }

        private parseRegions() {

            return this.doParseRegions();
        }

        private doParseRegions(parentElement?: api.dom.Element) {

            var layoutComponent = this.getPageComponent();
            var regions: Region[] = layoutComponent.getLayoutRegions().getRegions();
            var children = parentElement ? parentElement.getChildren() : this.getChildren();
            var regionIndex = 0;
            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    if (RegionItemType.get().equals(itemType)) {
                        var region = regions[regionIndex++];
                        var regionView = new RegionView(new RegionViewBuilder().
                            setParentView(this).
                            setParentElement(parentElement ? parentElement : this).
                            setRegion(region).
                            setElement(childElement));
                        this.addRegion(regionView);
                    }
                    else {
                        this.doParseRegions(childElement);
                    }
                }
                else {
                    this.doParseRegions(childElement);
                }
            });
        }

        private addRegion(regionView: RegionView) {
            this.regionViews.push(regionView);
            this.notifyItemViewAdded(new ItemViewAddedEvent(regionView));
            regionView.onItemViewAdded((event: ItemViewAddedEvent) => {
                this.notifyItemViewAdded(event);
            });
            regionView.onItemViewRemoved((event: ItemViewRemovedEvent) => {
                this.notifyItemViewRemoved(event);
            });
        }
    }
}