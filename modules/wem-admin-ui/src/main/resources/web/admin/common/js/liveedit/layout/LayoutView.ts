module api.liveedit.layout {

    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import RegionContainingView = api.liveedit.RegionContainingView;

    export class LayoutView extends PageComponentView<LayoutComponent> implements RegionContainingView {

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

        setPageComponent(data: LayoutComponent) {
            super.setPageComponent(data);
            var regions = data.getLayoutRegions().getRegions();
            this.regionViews.forEach((regionView: RegionView, index: number) => {
                var region = regions[index];
                regionView.setData(region);
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
    }
}