module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import ComponentPath = api.content.page.ComponentPath;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageRegions = api.content.page.PageRegions;
    import PageComponentView = api.liveedit.PageComponentView;

    export class PageComponentNameChanger {

        private pageRegions: PageRegions;

        private pageComponentView: PageComponentView<PageComponent>;

        setPageRegions(value: PageRegions): PageComponentNameChanger {
            this.pageRegions = value;
            return this;
        }

        setComponentView(value: PageComponentView<PageComponent>): PageComponentNameChanger {
            this.pageComponentView = value;
            return this;
        }

        changeTo(name: string) {
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.pageComponentView, "pageComponentView cannot be null");

            var pageComponent = this.pageComponentView.getPageComponent();

            var newComponentName = this.pageRegions.ensureUniqueComponentName(pageComponent.getPath().getRegionPath(),
                new ComponentName(api.util.removeInvalidChars(api.util.capitalizeAll(name))));

            pageComponent.setName(newComponentName);
            this.pageComponentView.getEl().setData("live-edit-component", pageComponent.getPath().toString());
        }
    }
}