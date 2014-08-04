module api.liveedit.image {

    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import ImageComponent = api.content.page.image.ImageComponent;

    export class ImageComponentViewBuilder extends PageComponentViewBuilder<ImageComponent> {

        constructor() {
            super();
            this.setType(ImageItemType.get());
        }
    }

    export class ImageComponentView extends PageComponentView<ImageComponent> {

        private placeholder: ImagePlaceholder;

        private imageComponent: ImageComponent;

        constructor(builder: ImageComponentViewBuilder) {
            super(builder);
            this.placeholder = new ImagePlaceholder(this);
            this.imageComponent = builder.pageComponent;
            if (this.conditionedForEmpty()) {
                this.displayPlaceholder();
            }
        }

        getImage(): api.dom.ImgEl {
            return <api.dom.ImgEl>this.getChildren().filter((child: api.dom.Element) => (child.getEl().getTagName() == 'IMG'))[0];
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

        showLoadingPlaceholder() {
            this.displayPlaceholder();
            this.select();
            this.placeholder.deselect();
        }

        conditionedForEmpty(): boolean {
            if (!this.imageComponent) {
                return this.isEmpty();
            }
            return this.isEmpty() || !this.imageComponent.getDescriptor();
        }

        displayPlaceholder() {
            this.markAsEmpty();

            this.removeChildren();
            this.appendChild(this.placeholder);
        }

        duplicate(duplicate: ImageComponent): ImageComponentView {
            var duplicatedView = new ImageComponentView(new ImageComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setPageComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

        getTooltipViewer(): ImageComponentViewer {
            return new ImageComponentViewer();
        }
    }
}