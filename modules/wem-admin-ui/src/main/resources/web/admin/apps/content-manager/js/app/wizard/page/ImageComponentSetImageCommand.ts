module app.wizard.page {

    import ContentId = api.content.ContentId;
    import ComponentPath = api.content.page.ComponentPath;
    import PageRegions = api.content.page.PageRegions;

    export class ImageComponentSetImageCommand {

        private defaultModels: DefaultModels;

        private componentView: any;

        private pageRegions: PageRegions;

        private componentPath: ComponentPath;

        private image: ContentId;

        private imageName: string;

        setDefaultModels(value: DefaultModels): ImageComponentSetImageCommand {
            this.defaultModels = value;
            return this;
        }

        setComponentView(value: any): ImageComponentSetImageCommand {
            this.componentView = value;
            return this;
        }

        setPageRegions(value: PageRegions): ImageComponentSetImageCommand {
            this.pageRegions = value;
            return this;
        }

        setComponentPath(value: ComponentPath): ImageComponentSetImageCommand {
            this.componentPath = value;
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
            api.util.assertNotNull(this.componentView, "uiComponent cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.image, "image cannot be null");
            api.util.assertNotNull(this.componentPath, "componentPath cannot be null");


            var imageComponent = this.pageRegions.getImageComponent(this.componentPath);
            if (imageComponent != null) {
                imageComponent.setImage(this.image);
                if (this.defaultModels.hasImageDescriptor()) {
                    imageComponent.setDescriptor(this.defaultModels.getImageDescriptor().getKey());
                }

                new PageComponentNameChanger().
                    setPageRegions(this.pageRegions).
                    setComponentPath(this.componentPath).
                    setComponentView(this.componentView).
                    changeTo(this.imageName);

                this.componentPath = imageComponent.getPath();

                return this.componentPath;
            }
            else {
                api.notify.showWarning("ImageComponent to set image on not found: " + this.componentPath);
            }

        }
    }
}