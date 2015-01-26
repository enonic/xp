module api.liveedit.image {

    import ComponentView = api.liveedit.ComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import ImageComponent = api.content.page.region.ImageComponent;

    export class ImageComponentViewBuilder extends ComponentViewBuilder<ImageComponent> {

        constructor() {
            super();
            this.setType(ImageItemType.get());
        }
    }

    export class ImageComponentView extends ComponentView<ImageComponent> {

        private imageComponent: ImageComponent;

        constructor(builder: ImageComponentViewBuilder) {
            this.liveEditModel = builder.parentRegionView.liveEditModel;
            this.imageComponent = builder.component;

            super(builder.setPlaceholder(
                new ImagePlaceholder(this)).
                setTooltipViewer(new ImageComponentViewer()));
            this.addClass('image-view');
        }

        getImage(): api.dom.ImgEl {
            return <api.dom.ImgEl>this.getChildren().filter((child: api.dom.Element) => (child.getEl().getTagName() == 'IMG'))[0];
        }

        isEmpty(): boolean {
            return !this.imageComponent || this.imageComponent.isEmpty();
        }

        duplicate(duplicate: ImageComponent): ImageComponentView {
            var duplicatedView = new ImageComponentView(new ImageComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

    }
}