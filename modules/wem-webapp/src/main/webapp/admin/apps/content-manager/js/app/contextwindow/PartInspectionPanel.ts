module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PartDescriptor = api.content.page.part.PartDescriptor;

    export class PartInspectionPanel extends PageComponentInspectionPanel<api.content.page.part.PartComponent> {

        private partComponent: api.content.page.part.PartComponent;
        private descriptorComboBox: api.content.page.part.PartDescriptorComboBox;

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-part", liveFormPanel, siteTemplate);
            this.initElements();
        }

        private initElements() {
            var descriptorHeader = new api.dom.H6El();
            descriptorHeader.setText("Descriptor:");
            descriptorHeader.addClass("descriptor-header");
            this.appendChild(descriptorHeader);

            var partDescriptorsRequest = new api.content.page.part.GetPartDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            var partDescriptorLoader = new api.content.page.part.PartDescriptorLoader(partDescriptorsRequest);
            this.descriptorComboBox = new api.content.page.part.PartDescriptorComboBox(partDescriptorLoader);

            var firstLoad = (modules) => {
                this.descriptorComboBox.setValue(this.getLiveFormPanel().getDefaultPartDescriptor().getKey().toString());
                this.descriptorComboBox.removeLoadedListener(firstLoad);
            };
            this.descriptorComboBox.addLoadedListener(firstLoad);
            this.descriptorComboBox.addOptionSelectedListener((option: api.ui.combobox.Option<PartDescriptor>) => {
                if (this.partComponent) {
                    var selectedDescriptor = option.displayValue.getKey();
                    this.partComponent.setDescriptor(selectedDescriptor);
                }
            });
            this.appendChild(this.descriptorComboBox);
        }

        setPartComponent(component: api.content.page.part.PartComponent) {
            this.setComponent(component);
            this.partComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                this.descriptorComboBox.setDescriptor(descriptorKey);
            }
        }

    }
}