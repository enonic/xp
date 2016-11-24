module api.liveedit.image {

    import ComponentView = api.liveedit.ComponentView;
    import ImageComponent = api.content.page.region.ImageComponent;
    import ContentDeletedEvent = api.content.event.ContentDeletedEvent;

    export class ImageComponentViewBuilder extends ComponentViewBuilder<ImageComponent> {

        constructor() {
            super();
            this.setType(ImageItemType.get());
        }
    }

    export class ImageComponentView extends ComponentView<ImageComponent> {

        private image: api.dom.Element;
        private imageComponent: ImageComponent;

        constructor(builder: ImageComponentViewBuilder) {
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.imageComponent = builder.component;

            super(builder.setPlaceholder(
                new ImagePlaceholder(this)).
                setViewer(new ImageComponentViewer()).
                setInspectActionRequired(true));

            this.initializeImage();

            this.handleContentRemovedEvent();
        }

        private handleContentRemovedEvent() {
            var contentDeletedListener = (event) => {
                var deleted = event.getDeletedItems().some((deletedItem: api.content.event.ContentDeletedItem) => {
                    return !deletedItem.isPending() && deletedItem.getContentId().equals(this.imageComponent.getImage());
                })
                if (deleted) {
                    this.remove();
                }
            }

            ContentDeletedEvent.on(contentDeletedListener);

            this.onRemoved((event) => {
                ContentDeletedEvent.un(contentDeletedListener);
            });
        }

        private initializeImage() {

            var figureElChildren = this.getChildren();
            for (var i = 0; i < figureElChildren.length; i++) {
                var image = figureElChildren[i];
                if (image.getHTMLElement().tagName.toUpperCase() == 'IMG') {
                    this.image = image;

                    // no way to use ImgEl.onLoaded because all html tags are parsed as Element
                    this.image.getEl().addEventListener("load", (event) => {
                        // refresh shader and highlighter after image loaded
                        // if it's still selected
                        if (this.isSelected()) {
                            this.highlightSelected();
                            //this.shade();
                        }
                    });
                }
                return;
            }
        }

        isEmpty(): boolean {
            return !this.imageComponent || this.imageComponent.isEmpty();
        }

    }
}