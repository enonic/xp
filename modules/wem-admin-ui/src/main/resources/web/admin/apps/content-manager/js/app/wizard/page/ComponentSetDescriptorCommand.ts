module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import LayoutComponent = api.content.page.region.LayoutComponent;
    import LayoutRegions = api.content.page.region.LayoutRegions;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import ComponentPathRegionAndComponent = api.content.page.region.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.region.ComponentName;
    import PageRegions = api.content.page.PageRegions;
    import ComponentView = api.liveedit.ComponentView;

    export class ComponentSetDescriptorCommand {

        private componentView: ComponentView<DescriptorBasedComponent>;

        private pageRegions: PageRegions;

        private descriptor: Descriptor;

        setComponentView(value: ComponentView<DescriptorBasedComponent>): ComponentSetDescriptorCommand {
            this.componentView = value;
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
            api.util.assertNotNull(this.componentView, "componentView cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.descriptor, "descriptor cannot be null");

            var component = this.componentView.getComponent();
            if (!component || !this.descriptor) {
                return;
            }

            new ComponentNameChanger().
                setPageRegions(this.pageRegions).
                setComponentView(this.componentView).
                changeTo(this.descriptor.getDisplayName());

            var newPath = component.getPath();
            api.util.assertNotNull(newPath, "Did not expect new path for Component to be null");

            component.setDescriptor(this.descriptor.getKey());

            var isLayoutDescriptor = api.ObjectHelper.iFrameSafeInstanceOf(this.descriptor, LayoutDescriptor);

            if (isLayoutDescriptor) {
                var layoutDescriptor = <LayoutDescriptor> this.descriptor;
                var layoutComponent = <LayoutComponent>component;
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