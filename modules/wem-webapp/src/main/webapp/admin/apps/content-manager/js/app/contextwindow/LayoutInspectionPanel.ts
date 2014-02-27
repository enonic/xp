module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;

    export class LayoutInspectionPanel extends PageComponentInspectionPanel<api.content.page.layout.LayoutComponent, LayoutDescriptor> {

        private layoutComponent: api.content.page.layout.LayoutComponent;
        private layoutDescriptors: {
            [key: string]: LayoutDescriptor;
        };

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-layout", liveFormPanel, siteTemplate);
            this.layoutDescriptors = {};
            this.initElements();
        }

        private initElements() {
            var getLayoutDescriptorsRequest = new api.content.page.layout.GetLayoutDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            getLayoutDescriptorsRequest.sendAndParse().done((results: LayoutDescriptor[]) => {
                results.forEach((layoutDescriptor: LayoutDescriptor) => {
                    this.layoutDescriptors[layoutDescriptor.getKey().toString()] = layoutDescriptor;
                });
            });
        }

        getDescriptor(key: api.content.page.DescriptorKey): LayoutDescriptor {
            return this.layoutDescriptors[key.toString()];
        }

        setLayoutComponent(component: api.content.page.layout.LayoutComponent) {
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