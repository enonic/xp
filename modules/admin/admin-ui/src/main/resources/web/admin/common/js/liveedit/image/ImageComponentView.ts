module api.liveedit.image {

    import ComponentView = api.liveedit.ComponentView;
    import ImageComponent = api.content.page.region.ImageComponent;
    import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ImageComponentViewBuilder extends ContentBasedComponentViewBuilder<ImageComponent> {

        constructor() {
            super();
            this.setType(ImageItemType.get());
            this.setContentTypeName(ContentTypeName.IMAGE);
        }
    }

    export class ImageComponentView extends ContentBasedComponentView<ImageComponent> {

        private image: api.dom.Element;
        protected component: ImageComponent;

        constructor(builder: ImageComponentViewBuilder) {
            super(<ImageComponentViewBuilder>builder.
                setViewer(new ImageComponentViewer()).
                setInspectActionRequired(true));

            this.setPlaceholder(new ImagePlaceholder(this));

            this.liveEditModel = builder.parentRegionView.getLiveEditModel();

            this.initializeImage();

            this.handleContentRemovedEvent();
        }

        protected getContentId(): ContentId {
            return this.component ? this.component.getImage() : null;
        }

        private handleContentRemovedEvent() {
            let contentDeletedListener = (event) => {
                let deleted = event.getDeletedItems().some((deletedItem: api.content.event.ContentDeletedItem) => {
                    return !deletedItem.isPending() && deletedItem.getContentId().equals(this.component.getImage());
                });
                if (deleted) {
                    this.remove();
                }
            };

            ContentDeletedEvent.on(contentDeletedListener);

            this.onRemoved((event) => {
                ContentDeletedEvent.un(contentDeletedListener);
            });
        }

        private initializeImage() {

            let figureElChildren = this.getChildren();
            for (let i = 0; i < figureElChildren.length; i++) {
                let image = figureElChildren[i];
                if (image.getHTMLElement().tagName.toUpperCase() === 'IMG') {
                    this.image = image;

                    // no way to use ImgEl.onLoaded because all html tags are parsed as Element
                    this.image.getEl().addEventListener('load', (event) => {
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

    }
}
