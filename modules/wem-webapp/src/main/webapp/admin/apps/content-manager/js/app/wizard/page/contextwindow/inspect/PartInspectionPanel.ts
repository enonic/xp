module app.wizard.page.contextwindow.inspect {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import PartComponent = api.content.page.part.PartComponent;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class PartInspectionPanel extends PageComponentInspectionPanel<PartComponent, PartDescriptor> {

        private partComponent: PartComponent;
        private partDescriptors: {
            [key: string]: PartDescriptor;
        };
        
        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-part", liveFormPanel, siteTemplate);
            this.partDescriptors = {};
            this.initElements();
        }

        private initElements() {
            var getPartDescriptorsRequest = new GetPartDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            getPartDescriptorsRequest.sendAndParse().done((results: PartDescriptor[]) => {
                results.forEach((partDescriptor: PartDescriptor) => {
                    this.partDescriptors[partDescriptor.getKey().toString()] = partDescriptor;
                });
            });
        }

        getDescriptor(key: DescriptorKey): PartDescriptor {
            return this.partDescriptors[key.toString()];
        }
        
        setPartComponent(component: PartComponent) {
            this.setComponent(component);
            this.partComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                var partDescriptor = this.getDescriptor(descriptorKey);
                if (!partDescriptor) {
                    console.warn('Part descriptor not found' + descriptorKey);
                    return;
                }
                this.setupComponentForm(component, partDescriptor);
            }
        }

    }
}