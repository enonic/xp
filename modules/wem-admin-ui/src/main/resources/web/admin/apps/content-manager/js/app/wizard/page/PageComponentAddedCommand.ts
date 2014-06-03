module app.wizard.page {

    import RootDataSet = api.data.RootDataSet;
    import PageComponentType = api.content.page.PageComponentType;
    import PageComponent = api.content.page.PageComponent;
    import ComponentPath = api.content.page.ComponentPath;
    import ComponentName = api.content.page.ComponentName;
    import RegionPath = api.content.page.RegionPath;
    import PageRegions = api.content.page.PageRegions;
    import DescriptorBasedPageComponentBuilder = api.content.page.DescriptorBasedPageComponentBuilder;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;

    export class PageComponentAddedCommand {

        private type: PageComponentType;

        private region: RegionPath;

        private precedingComponent: ComponentName;

        private pageRegions: PageRegions;

        private componentView: api.liveedit.PageComponentView;

        setPageRegions(value: PageRegions): PageComponentAddedCommand {
            this.pageRegions = value;
            return this;
        }

        setType(value: PageComponentType): PageComponentAddedCommand {
            this.type = value;
            return this;
        }

        setRegion(value: RegionPath): PageComponentAddedCommand {
            this.region = value;
            return this;
        }

        setPrecedingComponent(value: ComponentName): PageComponentAddedCommand {
            this.precedingComponent = value;
            return this;
        }

        setComponentView(value: api.liveedit.PageComponentView): PageComponentAddedCommand {
            this.componentView = value;
            return this;
        }

        execute(): PageComponent {
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.type, "type cannot be null");
            api.util.assertNotNull(this.region, "region cannot be null");
            api.util.assertNotNull(this.componentView, "componentView cannot be null");

            var pageComponent = this.addComponent();

            if (pageComponent) {
                this.componentView.getEl().setData("live-edit-component", pageComponent.getPath().toString());
            }
            this.componentView.getEl().setData("live-edit-type", this.type.getShortName());
            this.componentView.setData(pageComponent);
            return pageComponent;
        }

        private addComponent(): PageComponent {

            var wantedName = api.util.capitalize(api.util.removeInvalidChars(this.type.getShortName()));
            var componentName = this.pageRegions.ensureUniqueComponentName(this.region, new ComponentName(wantedName));

            var builder = this.type.newComponentBuilder();
            builder.setName(componentName);

            if (api.ObjectHelper.iFrameSafeInstanceOf(builder, DescriptorBasedPageComponentBuilder)) {
                    (<DescriptorBasedPageComponentBuilder<DescriptorBasedPageComponent>>builder).setConfig(new RootDataSet());
            }
            var component = builder.build();
            this.pageRegions.addComponentAfter(component, this.region, this.precedingComponent);
            return component;
        }
    }
}