module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import ComponentPath = api.content.page.ComponentPath;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageRegions = api.content.page.PageRegions;

    export class PageComponentSetDescriptorCommand {

        private componentView: any;

        private pageRegions: PageRegions;

        private descriptor: Descriptor;

        private componentPath: ComponentPath;

        setComponentView(value: any): PageComponentSetDescriptorCommand {
            this.componentView = value;
            return this;
        }

        setPageRegions(value: PageRegions): PageComponentSetDescriptorCommand {
            this.pageRegions = value;
            return this;
        }

        setDescriptor(value: Descriptor): PageComponentSetDescriptorCommand {
            this.descriptor = value;
            return this;
        }

        setComponentPath(value: ComponentPath): PageComponentSetDescriptorCommand {
            this.componentPath = value;
            return this;
        }

        execute(): ComponentPath {
            api.util.assertNotNull(this.componentView, "uiComponent cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.descriptor, "descriptor cannot be null");
            api.util.assertNotNull(this.componentPath, "componentPath cannot be null");

            var component: DescriptorBasedPageComponent = <DescriptorBasedPageComponent>this.pageRegions.getComponent(this.componentPath);
            if (!component || !this.descriptor) {
                return;
            }

            new PageComponentNameChanger().
                setPageRegions(this.pageRegions).
                setComponentPath(this.componentPath).
                setComponentView(this.componentView).
                changeTo(this.descriptor.getName().toString());

            this.componentPath = component.getPath();

            component.setDescriptor(this.descriptor.getKey());

            var isLayoutDescriptor = api.ObjectHelper.iFrameSafeInstanceOf(this.descriptor, LayoutDescriptor);

            if (isLayoutDescriptor) {
                var layoutDescriptor = <LayoutDescriptor> this.descriptor;
                var layoutComponent = <LayoutComponent>component;
                this.addLayoutRegions(layoutComponent, layoutDescriptor);
            }

            return this.componentPath;
        }

        private addLayoutRegions(layoutComponent: LayoutComponent, layoutDescriptor: LayoutDescriptor) {
            var sourceRegions: LayoutRegions = layoutComponent.getLayoutRegions();
            var mergedRegions: LayoutRegions = sourceRegions.mergeRegions(layoutDescriptor.getRegions(), layoutComponent.getPath());
            layoutComponent.setLayoutRegions(mergedRegions);
        }
    }
}