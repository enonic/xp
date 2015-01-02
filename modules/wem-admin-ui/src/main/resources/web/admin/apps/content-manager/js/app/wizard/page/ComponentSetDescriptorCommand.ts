module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedComponent = api.content.page.DescriptorBasedComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageRegions = api.content.page.PageRegions;
    import ComponentView = api.liveedit.ComponentView;

    export class ComponentSetDescriptorCommand {

        private pageComponentView: ComponentView<DescriptorBasedComponent>;

        private pageRegions: PageRegions;

        private descriptor: Descriptor;

        setPageComponentView(value: ComponentView<DescriptorBasedComponent>): ComponentSetDescriptorCommand {
            this.pageComponentView = value;
            return this;
        }

        setPageRegions(value: PageRegions): ComponentSetDescriptorCommand {
            this.pageRegions = value;
            return this;
        }

        setDescriptor(value: Descriptor): ComponentSetDescriptorCommand {
            this.descriptor = value;
            return this;
        }

        execute(): void {
            api.util.assertNotNull(this.pageComponentView, "itemView cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.descriptor, "descriptor cannot be null");

            var pageComponent = this.pageComponentView.getPageComponent();
            if (!pageComponent || !this.descriptor) {
                return;
            }

            new ComponentNameChanger().
                setPageRegions(this.pageRegions).
                setComponentView(this.pageComponentView).
                changeTo(this.descriptor.getDisplayName());

            var newPath = pageComponent.getPath();
            api.util.assertNotNull(newPath, "Did not expect new path for Component to be null");

            pageComponent.setDescriptor(this.descriptor.getKey());

            var isLayoutDescriptor = api.ObjectHelper.iFrameSafeInstanceOf(this.descriptor, LayoutDescriptor);

            if (isLayoutDescriptor) {
                var layoutDescriptor = <LayoutDescriptor> this.descriptor;
                var layoutComponent = <LayoutComponent>pageComponent;
                this.addLayoutRegions(layoutComponent, layoutDescriptor);
            }
        }

        private addLayoutRegions(layoutComponent: LayoutComponent, layoutDescriptor: LayoutDescriptor) {
            var sourceRegions: LayoutRegions = layoutComponent.getLayoutRegions();
            var mergedRegions: LayoutRegions = sourceRegions.mergeRegions(layoutDescriptor.getRegions(), layoutComponent);
            layoutComponent.setLayoutRegions(mergedRegions);
        }
    }
}