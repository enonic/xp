module api.liveedit.part {

    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import PartComponent = api.content.page.part.PartComponent;

    export class PartView extends PageComponentView<PartComponent> {

        private contentViews: ContentView[] = [];

        private placeholder: PartPlaceholder;

        constructor(parentRegionView: RegionView, partComponent: PartComponent, element?: HTMLElement, dummy?: boolean) {
            super(PartItemType.get(), parentRegionView, partComponent, element, dummy);

            this.placeholder = new PartPlaceholder(this);
        }

        addContent(view: ContentView) {
            this.contentViews.push(view);
        }

        getContents(): ContentView[] {
            return this.contentViews;
        }

        showHighlighter(value: boolean) {


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

        duplicate(duplicate: PartComponent): PartView {

            var duplicatedView = new PartView(this.getParentRegionView(), duplicate);
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }
    }
}