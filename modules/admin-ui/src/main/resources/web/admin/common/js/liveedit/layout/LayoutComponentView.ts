module api.liveedit.layout {

    import Component = api.content.page.region.Component;
    import Region = api.content.page.region.Region;
    import LayoutComponent = api.content.page.region.LayoutComponent;
    import Regions = api.content.page.region.Regions;
    import ComponentView = api.liveedit.ComponentView;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;

    export class LayoutComponentViewBuilder extends ComponentViewBuilder<LayoutComponent> {

        constructor() {
            super();
            this.setType(LayoutItemType.get());
        }
    }

    export class LayoutComponentView extends ComponentView<LayoutComponent> {

        private layoutComponent: LayoutComponent;

        private regionViews: RegionView[];

        constructor(builder: LayoutComponentViewBuilder) {
            this.regionViews = [];
            this.liveEditModel = builder.parentRegionView.liveEditModel;
            super(builder.setPlaceholder(new LayoutPlaceholder(this)));
            this.layoutComponent = builder.component;

            if (this.isEmpty()) {
                this.displayPlaceholder();
            }

            this.parseRegions();
        }

        setComponent(layoutComponent: LayoutComponent) {
            super.setComponent(layoutComponent);
            var regions = layoutComponent.getRegions().getRegions();
            this.regionViews.forEach((regionView: RegionView, index: number) => {
                var region = regions[index];
                regionView.setRegion(region);
            });
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }

        isEmpty(): boolean {
            return this.layoutComponent.isEmpty();
        }

        duplicate(duplicate: LayoutComponent): LayoutComponentView {

            var duplicatedView = new LayoutComponentView(new LayoutComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setComponent(duplicate));
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

            var layoutComponent: LayoutComponent = <LayoutComponent>this.getComponent();
            var layoutRegions = layoutComponent.getRegions();
            if (!layoutRegions) {
                return;
            }
            var regions: Region[] = layoutRegions.getRegions();
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
                        regionView.parseComponentViews();
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