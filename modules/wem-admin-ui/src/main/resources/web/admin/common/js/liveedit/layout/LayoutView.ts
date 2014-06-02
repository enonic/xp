module api.liveedit.layout {

    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;

    export class LayoutView extends PageComponentView {

        private placeholder: LayoutPlaceholder;

        private regions: RegionView[] = [];

        constructor(element?: HTMLElement, dummy?: boolean) {
            super(LayoutItemType.get(), element, dummy);

            this.placeholder = new LayoutPlaceholder(this);
        }

        addRegion(view: RegionView) {
            this.regions.push(view);
        }

        select() {
            super.select();
            if( this.isEmpty() ) {
                this.placeholder.select();
            }
        }

        deselect() {
            super.deselect();
            if( this.isEmpty() ) {
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