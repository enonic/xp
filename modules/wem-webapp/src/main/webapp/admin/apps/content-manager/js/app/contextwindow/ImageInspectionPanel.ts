module app.contextwindow {

    export class ImageInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private imageComponent: api.content.page.image.ImageComponent;

        constructor(config: ComponentInspectionPanelConfig) {
            super(config, "live-edit-font-icon-image");

            this.initElements();
        }

        private initElements() {
            var templateHeader = new api.dom.H6El();
            templateHeader.setText("Template:");
            templateHeader.addClass("template-header");
            this.appendChild(templateHeader);

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
            this.imageComponent = component;
            this.setName(component.getName().toString(), component.getPath().toString());
        }
    }
}