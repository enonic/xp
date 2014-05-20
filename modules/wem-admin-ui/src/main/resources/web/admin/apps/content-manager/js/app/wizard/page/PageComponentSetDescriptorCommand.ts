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
    import ItemView = api.liveedit.ItemView;

    export class PageComponentSetDescriptorCommand {

        private itemView: ItemView;

        private pageRegions: PageRegions;

        private descriptor: Descriptor;

        private componentPath: ComponentPath;

        setItemView(value: ItemView): PageComponentSetDescriptorCommand {
            this.itemView = value;
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
            api.util.assertNotNull(this.itemView, "itemView cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.descriptor, "descriptor cannot be null");
            api.util.assertNotNull(this.componentPath, "componentPath cannot be null");

            var component: DescriptorBasedPageComponent = <DescriptorBasedPageComponent>this.pageRegions.getComponent(this.componentPath);
            if (!component || !this.descriptor) {
                return null;
            }

            new PageComponentNameChanger().
                setPageRegions(this.pageRegions).
                setComponentPath(this.componentPath).
                setComponentView(this.itemView).
                changeTo(this.descriptor.getName().toString());

            var newPath = component.getPath();
            api.util.assertNotNull(newPath, "Did not expect new path for PageComponent to be null");

            component.setDescriptor(this.descriptor.getKey());

            var isLayoutDescriptor = api.ObjectHelper.iFrameSafeInstanceOf(this.descriptor, LayoutDescriptor);

            if (isLayoutDescriptor) {
                var layoutDescriptor = <LayoutDescriptor> this.descriptor;
                var layoutComponent = <LayoutComponent>component;
                this.addLayoutRegions(layoutComponent, layoutDescriptor);
            }

            return newPath;
        }

        private addLayoutRegions(layoutComponent: LayoutComponent, layoutDescriptor: LayoutDescriptor) {
            var sourceRegions: LayoutRegions = layoutComponent.getLayoutRegions();
            var mergedRegions: LayoutRegions = sourceRegions.mergeRegions(layoutDescriptor.getRegions(), layoutComponent.getPath());
            layoutComponent.setLayoutRegions(mergedRegions);
        }
    }
}