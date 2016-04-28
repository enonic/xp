module api.liveedit.layout {

    import Component = api.content.page.region.Component;
    import ComponentPath = api.content.page.region.ComponentPath;
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

        private itemViewAddedListener: (event: ItemViewAddedEvent) => void;

        private itemViewRemovedListener: (event: ItemViewRemovedEvent) => void;

        public static debug: boolean;

        constructor(builder: LayoutComponentViewBuilder) {
            this.regionViews = [];

            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.layoutComponent = builder.component;
            LayoutComponentView.debug = false;

            this.itemViewAddedListener = (event: ItemViewAddedEvent) => this.notifyItemViewAdded(event.getView(), event.isNew());
            this.itemViewRemovedListener = (event: ItemViewRemovedEvent) => this.notifyItemViewRemoved(event.getView());

            super(builder.
                setViewer(new LayoutComponentViewer()).
                setPlaceholder(new LayoutPlaceholder(this)).
                setInspectActionRequired(true));

            this.parseRegions();
        }

        getRegionViewByName(name: string): RegionView {

            for (var i = 0; i < this.regionViews.length; i++) {
                var regionView = this.regionViews[i];
                if (regionView.getRegionName() == name) {
                    return regionView;
                }
            }
            return null;
        }

        getComponentViewByPath(path: ComponentPath): ComponentView<Component> {

            var firstLevelOfPath = path.getFirstLevel();

            for (var i = 0; i < this.regionViews.length; i++) {
                var regionView = this.regionViews[i];
                if (firstLevelOfPath.getRegionName() == regionView.getRegionName()) {
                    if (path.numberOfLevels() == 1) {
                        return regionView.getComponentViewByIndex(firstLevelOfPath.getComponentIndex());
                    }
                    else {
                        var layoutView: LayoutComponentView = <LayoutComponentView>regionView.getComponentViewByIndex(firstLevelOfPath.getComponentIndex());
                        return layoutView.getComponentViewByPath(path.removeFirstLevel());
                    }
                }
            }

            return null;
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
            return !this.layoutComponent || this.layoutComponent.isEmpty();
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
            this.regionViews.forEach((regionView) => {
                this.unregisterRegionView(regionView);
            });

            this.regionViews = [];

            return this.doParseRegions();
        }

        private doParseRegions(parentElement?: api.dom.Element) {

            var layoutComponent: LayoutComponent = <LayoutComponent>this.getComponent();
            var layoutRegions = layoutComponent.getRegions();
            if (!layoutRegions) {
                return;
            }
            var children = parentElement ? parentElement.getChildren() : this.getChildren();

            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                var isRegionView = api.ObjectHelper.iFrameSafeInstanceOf(childElement, RegionView);
                var region, regionName, regionView;

                if (isRegionView) {
                    regionName = RegionItemType.getRegionName(childElement);
                    region = layoutRegions.getRegionByName(regionName);
                    if (region) {
                        // reuse existing region view
                        regionView = <RegionView> childElement;
                        // update view's data
                        regionView.setRegion(region);
                        // register it again because we unregistered everything before parsing
                        this.registerRegionView(regionView);
                    }

                } else if (itemType && RegionItemType.get().equals(itemType)) {
                    regionName = RegionItemType.getRegionName(childElement);
                    region = layoutRegions.getRegionByName(regionName);

                    if (region) {
                        regionView = new RegionView(new RegionViewBuilder().
                            setParentView(this).
                            setParentElement(parentElement ? parentElement : this).
                            setRegion(region).
                            setElement(childElement));

                        this.registerRegionView(regionView);
                    }

                } else {
                    this.doParseRegions(childElement);
                }
            });
        }

        private registerRegionView(regionView: RegionView) {
            if (LayoutComponentView.debug) {
                console.log('LayoutComponentView.registerRegionView: ' + regionView.toString());
            }

            this.regionViews.push(regionView);
            this.notifyItemViewAdded(regionView);

            regionView.onItemViewAdded(this.itemViewAddedListener);
            regionView.onItemViewRemoved(this.itemViewRemovedListener);
        }

        private unregisterRegionView(regionView: RegionView) {
            if (LayoutComponentView.debug) {
                console.log('LayoutComponentView.unregisterRegionView: ' + regionView.toString(), this.regionViews);
            }

            var index = this.regionViews.indexOf(regionView);
            if (index > -1) {
                this.regionViews.splice(index, 1);

                this.notifyItemViewRemoved(regionView);

                regionView.unItemViewAdded(this.itemViewAddedListener);
                regionView.unItemViewRemoved(this.itemViewRemovedListener);
            }
        }
    }
}