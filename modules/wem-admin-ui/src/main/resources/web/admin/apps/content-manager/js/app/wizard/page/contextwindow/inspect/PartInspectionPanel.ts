module app.wizard.page.contextwindow.inspect {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import PartComponent = api.content.page.part.PartComponent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import PartView = api.liveedit.part.PartView;

    export interface PartInspectionPanelConfig {

        siteTemplate: SiteTemplate;
    }

    export class PartInspectionPanel extends DescriptorBasedPageComponentInspectionPanel<PartComponent, PartDescriptor> {

        private partDescriptors: {
            [key: string]: PartDescriptor;
        };

        constructor(config: PartInspectionPanelConfig) {
            super(<PageComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-part icon-xlarge"
            });

            this.partDescriptors = {};

            var getPartDescriptorsRequest = new GetPartDescriptorsByModulesRequest(config.siteTemplate.getModules());
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

        setPartComponent(partView: PartView) {
            this.setComponent(partView.getPageComponent());

            var partDescriptor = this.getDescriptor();
            this.setupComponentForm(partView.getPageComponent(), partDescriptor);
        }
    }
}