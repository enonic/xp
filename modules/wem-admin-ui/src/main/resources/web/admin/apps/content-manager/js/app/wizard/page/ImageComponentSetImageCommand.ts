module app.wizard.page {

    import ContentId = api.content.ContentId;
    import ComponentPath = api.content.page.ComponentPath;
    import PageRegions = api.content.page.PageRegions;
    import ImageView = api.liveedit.image.ImageView;

    export class ImageComponentSetImageCommand {

        private defaultModels: DefaultModels;

        private imageView: ImageView;

        private pageRegions: PageRegions;

        private image: ContentId;

        private imageName: string;

        setDefaultModels(value: DefaultModels): ImageComponentSetImageCommand {
            this.defaultModels = value;
            return this;
        }

        setPageComponentView(value: ImageView): ImageComponentSetImageCommand {
            this.imageView = value;
            return this;
        }

        setPageRegions(value: PageRegions): ImageComponentSetImageCommand {
            this.pageRegions = value;
            return this;
        }

        setImage(value: ContentId): ImageComponentSetImageCommand {
            this.image = value;
            return this;
        }

        setImageName(value: string): ImageComponentSetImageCommand {
            this.imageName = value;
            return this;
        }

        execute(): ComponentPath {
            api.util.assertNotNull(this.defaultModels, "defaultModels cannot be null");
            api.util.assertNotNull(this.imageView, "itemView cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.image, "image cannot be null");

            var imageComponent = this.imageView.getPageComponent();
            imageComponent.setImage(this.image);
            if (this.defaultModels.hasImageDescriptor()) {
                imageComponent.setDescriptor(this.defaultModels.getImageDescriptor().getKey());
            }

            new PageComponentNameChanger().
                setPageRegions(this.pageRegions).
                setComponentView(this.imageView).
                changeTo(this.imageName);

            return imageComponent.getPath();
        }
    }
}