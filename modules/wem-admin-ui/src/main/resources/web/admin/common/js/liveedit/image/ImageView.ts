module api.liveedit.image {

    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import ImageComponent = api.content.page.image.ImageComponent;

    export class ImageViewBuilder extends PageComponentViewBuilder<ImageComponent> {

        constructor() {
            super();
            this.setType(ImageItemType.get());
        }
    }

    export class ImageView extends PageComponentView<ImageComponent> {

        private placeholder: ImagePlaceholder;

        constructor(builder: ImageViewBuilder) {
            super(builder);
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

            var duplicatedView = new ImageView(new ImageViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setPageComponent(duplicate));
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }

        getTooltipViewer(): ImageComponentViewer {
            return new ImageComponentViewer();
        }
    }
}