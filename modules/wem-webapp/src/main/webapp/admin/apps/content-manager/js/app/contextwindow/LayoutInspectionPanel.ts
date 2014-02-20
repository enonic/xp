module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;

    export class LayoutInspectionPanel extends PageComponentInspectionPanel<api.content.page.layout.LayoutComponent> {

        private layoutComponent: api.content.page.layout.LayoutComponent;
        private descriptorComboBox: api.content.page.layout.LayoutDescriptorComboBox;


        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {

            super("live-edit-font-icon-layout", liveFormPanel, siteTemplate);
            this.initElements();
        }

        private initElements() {
            var descriptorHeader = new api.dom.H6El();
            descriptorHeader.setText("Descriptor:");
            descriptorHeader.addClass("descriptor-header");
            this.appendChild(descriptorHeader);

            var layoutDescriptorsRequest = new api.content.page.layout.GetLayoutDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            var layoutDescriptorLoader = new api.content.page.layout.LayoutDescriptorLoader(layoutDescriptorsRequest);
            this.descriptorComboBox = new api.content.page.layout.LayoutDescriptorComboBox(layoutDescriptorLoader);

            var onDescriptorsLoaded = () => {
                this.descriptorComboBox.setValue(this.getLiveFormPanel().getDefaultLayoutDescriptor().getKey().toString());
                this.descriptorComboBox.removeLoadedListener(onDescriptorsLoaded); // execute only on the first loaded event
            };
            this.descriptorComboBox.addLoadedListener(onDescriptorsLoaded);

            this.descriptorComboBox.addOptionSelectedListener((option: api.ui.combobox.Option<LayoutDescriptor>) => {
                if (this.layoutComponent) {
                    var selectedDescriptor = option.displayValue.getKey();
                    this.layoutComponent.setDescriptor(selectedDescriptor);
                }
            });
            this.appendChild(this.descriptorComboBox);
        }

        setLayoutComponent(component: api.content.page.layout.LayoutComponent) {
            this.setComponent(component);
            this.layoutComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                this.descriptorComboBox.setDescriptor(descriptorKey);
            }
        }

    }
}