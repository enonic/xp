module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class ImageInspectionPanel extends PageComponentInspectionPanel<api.content.page.image.ImageComponent> {

        private imageComponent: api.content.page.image.ImageComponent;

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-image", liveFormPanel, siteTemplate);

            this.initElements();
        }

        private initElements() {
            var descriptorHeader = new api.dom.H6El();
            descriptorHeader.setText("Descriptor:");
            descriptorHeader.addClass("descriptor-header");
            this.appendChild(descriptorHeader);

            var imageDescriptorsRequest = new api.content.page.image.GetImageDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            var imageDescriptorLoader = new api.content.page.image.ImageDescriptorLoader(imageDescriptorsRequest);
            var descriptorComboBox = new api.content.page.image.ImageDescriptorComboBox(imageDescriptorLoader);

            var firstLoad = (modules) => {
                descriptorComboBox.setValue(this.getLiveFormPanel().getDefaultImageDescriptor().getKey().toString());
                descriptorComboBox.removeLoadedListener(firstLoad);
            };
            descriptorComboBox.addLoadedListener(firstLoad);
            this.appendChild(descriptorComboBox);
        }

        setImageComponent(component: api.content.page.image.ImageComponent) {

            this.setComponent(component);
            this.imageComponent = component;

        }
    }
}