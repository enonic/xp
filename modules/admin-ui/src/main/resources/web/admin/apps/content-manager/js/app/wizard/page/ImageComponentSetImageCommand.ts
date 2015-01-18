module app.wizard.page {

    import ContentId = api.content.ContentId;
    import Regions = api.content.page.region.Regions;
    import ImageComponentView = api.liveedit.image.ImageComponentView;

    export class ImageComponentSetImageCommand {

        private defaultModels: DefaultModels;

        private imageView: ImageComponentView;

        private pageRegions: Regions;

        private image: ContentId;

        private imageName: string;

        setDefaultModels(value: DefaultModels): ImageComponentSetImageCommand {
            this.defaultModels = value;
            return this;
        }

        setComponentView(value: ImageComponentView): ImageComponentSetImageCommand {
            this.imageView = value;
            return this;
        }

        setPageRegions(value: Regions): ImageComponentSetImageCommand {
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

        execute(): void {
            api.util.assertNotNull(this.defaultModels, "defaultModels cannot be null");
            api.util.assertNotNull(this.imageView, "imageView cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.image, "image cannot be null");

            var imageComponent = this.imageView.getComponent();
            imageComponent.setImage(this.image);

            new ComponentNameChanger().
                setPageRegions(this.pageRegions).
                setComponentView(this.imageView).
                changeTo(this.imageName);
        }
    }
}