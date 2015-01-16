module app.wizard.page.contextwindow.inspect.region {

    import ImageComponentView = api.liveedit.image.ImageComponentView;

    export class ImageChangedEvent {

        private imageView: ImageComponentView;

        private contentId: api.content.ContentId;

        constructor(imageView: ImageComponentView, contentId: api.content.ContentId) {
            this.imageView = imageView;
            this.contentId = contentId;
        }

        getImageComponentView(): ImageComponentView {
            return this.imageView;
        }

        getContentId(): api.content.ContentId {
            return this.contentId;
        }
    }
}
