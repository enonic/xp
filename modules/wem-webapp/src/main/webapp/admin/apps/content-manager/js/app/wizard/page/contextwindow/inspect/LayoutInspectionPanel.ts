module app.wizard.page.contextwindow.inspect {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.layout.GetLayoutDescriptorsByModulesRequest;

    export class LayoutInspectionPanel extends PageComponentInspectionPanel<LayoutComponent, LayoutDescriptor> {

        private layoutComponent: LayoutComponent;
        private layoutDescriptors: {
            [key: string]: LayoutDescriptor;
        };

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-layout", liveFormPanel, siteTemplate);
            this.layoutDescriptors = {};
            this.initElements();
        }

        private initElements() {
            var getLayoutDescriptorsRequest = new GetLayoutDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            getLayoutDescriptorsRequest.sendAndParse().done((results: LayoutDescriptor[]) => {
                results.forEach((layoutDescriptor: LayoutDescriptor) => {
                    this.layoutDescriptors[layoutDescriptor.getKey().toString()] = layoutDescriptor;
                });
            });
        }

        getDescriptor(key: DescriptorKey): LayoutDescriptor {
            return this.layoutDescriptors[key.toString()];
        }

        setLayoutComponent(component: LayoutComponent) {
            this.setComponent(component);
            this.layoutComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                var layoutDescriptor = this.getDescriptor(descriptorKey);
                if (!layoutDescriptor) {
                    console.warn('Layout descriptor not found' + descriptorKey);
                    return;
                }
                this.setupComponentForm(component, layoutDescriptor);
            }
        }

    }
}