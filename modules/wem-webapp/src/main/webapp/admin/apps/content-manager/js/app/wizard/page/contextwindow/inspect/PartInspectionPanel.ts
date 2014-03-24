module app.wizard.page.contextwindow.inspect {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import PartComponent = api.content.page.part.PartComponent;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class PartInspectionPanel extends PageComponentInspectionPanel<PartComponent, PartDescriptor> {

        private partDescriptors: {
            [key: string]: PartDescriptor;
        };

        constructor(liveFormPanel: app.wizard.page.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-part", liveFormPanel, siteTemplate);
            this.partDescriptors = {};

            var getPartDescriptorsRequest = new GetPartDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            getPartDescriptorsRequest.sendAndParse().done((results: PartDescriptor[]) => {
                results.forEach((partDescriptor: PartDescriptor) => {
                    this.partDescriptors[partDescriptor.getKey().toString()] = partDescriptor;
                });
            });
        }

        getDescriptor(): PartDescriptor {
            if (!this.getComponent().hasDescriptor()) {
                return null;
            }
            return this.partDescriptors[this.getComponent().getDescriptor().toString()];
        }

        setPartComponent(component: PartComponent) {
            this.setComponent(component);

            var partDescriptor = this.getDescriptor();
            if (!partDescriptor) {
                return;
            }
            this.setupComponentForm(component, partDescriptor);
        }
    }
}