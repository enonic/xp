module api.liveedit.layout {

    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;

    export class LayoutView extends PageComponentView {

        private placeholder: LayoutPlaceholder;

        private regionViews: RegionView[] = [];

        constructor(element?: HTMLElement, dummy?: boolean) {
            super(LayoutItemType.get(), element, dummy);

            this.placeholder = new LayoutPlaceholder(this);
        }

        addRegion(view: RegionView) {
            this.regionViews.push(view);
        }

        setData(data: LayoutComponent) {
            super.setData(data);
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

        duplicate(): LayoutView {

            var duplicatedView = new LayoutView();
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }
    }
}