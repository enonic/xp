module app.wizard.page.contextwindow.inspect {

    import Site = api.content.site.Site;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import PartComponent = api.content.page.part.PartComponent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import PartComponentView = api.liveedit.part.PartComponentView;

    export interface PartInspectionPanelConfig {

        site: Site;
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

            var getPartDescriptorsRequest = new GetPartDescriptorsByModulesRequest(config.site.getModuleKeys());
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

        setPartComponent(partView: PartComponentView) {
            this.setComponent(partView.getPageComponent());

            var partDescriptor = this.getDescriptor();
            this.setupComponentForm(partView.getPageComponent(), partDescriptor);
        }
    }
}