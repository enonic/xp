module app.contextwindow.inspect {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PartDescriptor = api.content.page.part.PartDescriptor;

    export class PartInspectionPanel extends PageComponentInspectionPanel<api.content.page.part.PartComponent, PartDescriptor> {

        private partComponent: api.content.page.part.PartComponent;
        private partDescriptors: {
            [key: string]: PartDescriptor;
        };
        
        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-part", liveFormPanel, siteTemplate);
            this.partDescriptors = {};
            this.initElements();
        }

        private initElements() {
            var getPartDescriptorsRequest = new api.content.page.part.GetPartDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            getPartDescriptorsRequest.sendAndParse().done((results: PartDescriptor[]) => {
                results.forEach((partDescriptor: PartDescriptor) => {
                    this.partDescriptors[partDescriptor.getKey().toString()] = partDescriptor;
                });
            });
        }

        getDescriptor(key: api.content.page.DescriptorKey): PartDescriptor {
            return this.partDescriptors[key.toString()];
        }
        
        setPartComponent(component: api.content.page.part.PartComponent) {
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