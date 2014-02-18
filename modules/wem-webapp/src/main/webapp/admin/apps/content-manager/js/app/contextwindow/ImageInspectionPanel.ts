module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;

    export class ImageInspectionPanel extends PageComponentInspectionPanel<api.content.page.image.ImageComponent> {

        private imageComponent: api.content.page.image.ImageComponent;
        private descriptorComboBox: api.content.page.image.ImageDescriptorComboBox;

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
            this.descriptorComboBox = new api.content.page.image.ImageDescriptorComboBox(imageDescriptorLoader);

            var firstLoad = (modules) => {
                this.descriptorComboBox.setValue(this.getLiveFormPanel().getDefaultImageDescriptor().getKey().toString());
                this.descriptorComboBox.removeLoadedListener(firstLoad);
            };
            this.descriptorComboBox.addLoadedListener(firstLoad);
            this.descriptorComboBox.addOptionSelectedListener((option: api.ui.combobox.Option<ImageDescriptor>) => {
                if (this.imageComponent) {
                    var selectedDescriptor = option.displayValue.getKey();
                    this.imageComponent.setDescriptor(selectedDescriptor);
                }
            });
            this.appendChild(this.descriptorComboBox);
        }

        setImageComponent(component: api.content.page.image.ImageComponent) {
            this.setComponent(component);
            this.imageComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                this.descriptorComboBox.setDescriptor(descriptorKey);
            }
        }
    }
}