module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageRegions = api.content.page.PageRegions;
    import PageComponentView = api.liveedit.PageComponentView;

    export class PageComponentSetDescriptorCommand {

        private pageComponentView: PageComponentView<DescriptorBasedPageComponent>;

        private pageRegions: PageRegions;

        private descriptor: Descriptor;

        setPageComponentView(value: PageComponentView<DescriptorBasedPageComponent>): PageComponentSetDescriptorCommand {
            this.pageComponentView = value;
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

        execute(): void {
            api.util.assertNotNull(this.pageComponentView, "itemView cannot be null");
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.descriptor, "descriptor cannot be null");

            var pageComponent = this.pageComponentView.getPageComponent();
            if (!pageComponent || !this.descriptor) {
                return;
            }

            new PageComponentNameChanger().
                setPageRegions(this.pageRegions).
                setComponentView(this.pageComponentView).
                changeTo(this.descriptor.getName().toString());

            var newPath = pageComponent.getPath();
            api.util.assertNotNull(newPath, "Did not expect new path for PageComponent to be null");

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