module app.wizard.page {

    import ContentId = api.content.ContentId;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import ComponentPath = api.content.page.ComponentPath;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageRegions = api.content.page.PageRegions;

    export class ImageComponentSetImageCommand {

        private defaultModels: DefaultModels;

        private uiComponent: any;

        private pageRegions: PageRegions;

        private componentPath: ComponentPath;

        private image: ContentId;

        private imageName: string;

        setDefaultModels(value: DefaultModels): ImageComponentSetImageCommand {
            this.defaultModels = value;
            return this;
        }

        setUIComponent(value: any): ImageComponentSetImageCommand {
            this.uiComponent = value;
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
            api.util.assertNotNull(this.uiComponent, "uiComponent cannot be null");
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
                    setUIComponent(this.uiComponent).
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