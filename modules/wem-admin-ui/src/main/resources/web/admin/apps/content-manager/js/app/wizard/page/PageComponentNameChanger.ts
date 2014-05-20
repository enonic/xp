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

    export class PageComponentNameChanger {

        private pageRegions: PageRegions;

        private componentPath: ComponentPath;

        private itemView: ItemView;

        setPageRegions(value: PageRegions): PageComponentNameChanger {
            this.pageRegions = value;
            return this;
        }

        setComponentView(value: ItemView): PageComponentNameChanger {
            this.itemView = value;
            return this;
        }

        setComponentPath(value: ComponentPath): PageComponentNameChanger {
            this.componentPath = value;
            return this;
        }

        changeTo(name: string) {
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.componentPath, "componentPath cannot be null");
            api.util.assertNotNull(this.itemView, "itemView cannot be null");

            var component = this.pageRegions.getComponent(this.componentPath);

            var newComponentName = this.pageRegions.ensureUniqueComponentName(component.getPath().getRegionPath(),
                new ComponentName(api.util.removeInvalidChars(api.util.capitalizeAll(name))));

            component.setName(newComponentName);
            this.itemView.getEl().setData("live-edit-component", component.getPath().toString());
        }
    }
}