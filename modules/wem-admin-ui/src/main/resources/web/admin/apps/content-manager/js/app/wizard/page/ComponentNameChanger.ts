module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedComponent = api.content.page.DescriptorBasedComponent;
    import Component = api.content.page.Component;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageRegions = api.content.page.PageRegions;
    import ComponentView = api.liveedit.ComponentView;

    export class ComponentNameChanger {

        private pageRegions: PageRegions;

        private componentView: ComponentView<Component>;

        setPageRegions(value: PageRegions): ComponentNameChanger {
            this.pageRegions = value;
            return this;
        }

        setComponentView(value: ComponentView<Component>): ComponentNameChanger {
            this.componentView = value;
            return this;
        }

        changeTo(name: string) {
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.componentView, "componentView cannot be null");

            var component = this.componentView.getComponent();

            var componentName = new ComponentName(name);
            component.setName(componentName);
        }
    }
}