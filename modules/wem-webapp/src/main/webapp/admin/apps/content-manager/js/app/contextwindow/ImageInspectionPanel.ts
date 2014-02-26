module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;

    export class ImageInspectionPanel extends PageComponentInspectionPanel<api.content.page.image.ImageComponent> {

        private imageComponent: api.content.page.image.ImageComponent;
        private imageDescriptors: {
            [key: string]: ImageDescriptor;
        };

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-image", liveFormPanel, siteTemplate);
            this.imageDescriptors = {};
            this.initElements();
        }

        private initElements() {
            var getImageDescriptorsRequest = new api.content.page.image.GetImageDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            getImageDescriptorsRequest.sendAndParse().done((results: ImageDescriptor[]) => {
                results.forEach((imageDescriptor: ImageDescriptor) => {
                    this.imageDescriptors[imageDescriptor.getKey().toString()] = imageDescriptor;
                });
            });
        }
        
        private getDescriptor(key: api.content.page.DescriptorKey): ImageDescriptor {
            return this.imageDescriptors[key.toString()];
        }

        setImageComponent(component: api.content.page.image.ImageComponent) {
            this.setComponent(component);
            this.imageComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                var imageDescriptor = this.getDescriptor(descriptorKey);
                if (!imageDescriptor) {
                    console.warn('Image descriptor not found' + descriptorKey);
                    return;
                }
                this.setupComponentForm(component, imageDescriptor);
            }
        }

    }
}