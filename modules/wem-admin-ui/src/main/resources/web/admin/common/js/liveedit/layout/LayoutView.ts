module api.liveedit.layout {

    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;

    export class LayoutView extends PageComponentView<LayoutComponent> {

        private placeholder: LayoutPlaceholder;

        private regionViews: RegionView[];

        constructor(parentRegionView: RegionView, layoutComponent: LayoutComponent, element?: HTMLElement, dummy?: boolean) {
            this.regionViews = [];
            super(LayoutItemType.get(), parentRegionView, layoutComponent, element, dummy);

            this.placeholder = new LayoutPlaceholder(this);
        }

        addRegion(view: RegionView) {
            this.regionViews.push(view);
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

        select() {
            super.select();
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

        empty() {
            super.empty();

            this.removeChildren();
            this.appendChild(this.placeholder);

        }

        duplicate(duplicate: LayoutComponent): LayoutView {

            var duplicatedView = new LayoutView(this.getParentItemView(), duplicate);
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }

        getTooltipViewer(): LayoutComponentViewer {
            return new LayoutComponentViewer();
        }

        static getClosestParentLayoutView(itemView: ItemView): LayoutView {

            var parent = itemView.getParentItemView();
            while (!api.ObjectHelper.iFrameSafeInstanceOf(parent, LayoutView)) {
                parent = parent.getParentItemView();
                if (parent == null) {
                    break;
                }
            }
            if (!parent) {
                return null;
            }
            return <LayoutView>parent;
        }

        static getParentLayoutView(pageComponentView: PageComponentView<PageComponent>): LayoutView {

            var parentRegion = pageComponentView.getParentItemView();
            var potentialLayoutView = parentRegion.getParentItemView()
            if (api.ObjectHelper.iFrameSafeInstanceOf(potentialLayoutView, LayoutView)) {
                return <LayoutView> potentialLayoutView;
            }
            else {
                return null;
            }
        }

        static hasParentLayoutView(pageComponentView: PageComponentView<PageComponent>): boolean {
            return !LayoutView.getParentLayoutView(pageComponentView) ? false : true;
        }
    }
}