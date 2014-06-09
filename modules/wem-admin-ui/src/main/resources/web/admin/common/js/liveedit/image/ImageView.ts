module api.liveedit.image {

    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import ImageComponent = api.content.page.image.ImageComponent;

    export class ImageView extends PageComponentView<ImageComponent> {

        private placeholder: ImagePlaceholder;

        constructor(parentRegionView: RegionView, imageComponent: ImageComponent, element?: HTMLElement, dummy?: boolean) {
            super(ImageItemType.get(), parentRegionView, imageComponent, element, dummy);

            this.placeholder = new ImagePlaceholder(this);
        }

        getImage(): api.dom.ImgEl {
            return <api.dom.ImgEl>this.getChildren().filter((child: api.dom.Element) => (child.getEl().getTagName() == 'IMG'))[0];
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

        duplicate(duplicate: ImageComponent): ImageView {

            var duplicatedView = new ImageView(this.getParentRegionView(), duplicate);
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }
    }
}