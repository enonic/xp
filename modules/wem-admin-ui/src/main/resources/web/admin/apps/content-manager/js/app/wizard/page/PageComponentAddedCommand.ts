module app.wizard.page {

    import RootDataSet = api.data.RootDataSet;
    import PageComponentType = api.content.page.PageComponentType;
    import PageComponent = api.content.page.PageComponent;
    import ComponentPath = api.content.page.ComponentPath;
    import ComponentName = api.content.page.ComponentName;
    import RegionPath = api.content.page.RegionPath;
    import PageRegions = api.content.page.PageRegions;
    import DescriptorBasedPageComponentBuilder = api.content.page.DescriptorBasedPageComponentBuilder;

    export class PageComponentAddedCommand {

        private type: PageComponentType;

        private regionPath: RegionPath;

        private precedingComponent: ComponentName;

        private pageRegions: PageRegions;

        private componentView: api.dom.Element;

        setPageRegions(value: PageRegions): PageComponentAddedCommand {
            this.pageRegions = value;
            return this;
        }

        setType(value: PageComponentType): PageComponentAddedCommand {
            this.type = value;
            return this;
        }

        setRegion(value: RegionPath): PageComponentAddedCommand {
            this.regionPath = value;
            return this;
        }

        setPrecedingComponent(value: ComponentName): PageComponentAddedCommand {
            this.precedingComponent = value;
            return this;
        }

        setComponentView(value: api.dom.Element): PageComponentAddedCommand {
            this.componentView = value;
            return this;
        }

        execute(): PageComponent {

            var component = this.addComponent();

            if (component) {
                this.componentView.getEl().setData("live-edit-component", component.getPath().toString());
            }
            this.componentView.getEl().setData("live-edit-type", this.type.getShortName());

            return component;
        }

        private addComponent(): PageComponent {

            var wantedName = api.util.capitalize(api.util.removeInvalidChars(this.type.getShortName()));
            var componentName = this.pageRegions.ensureUniqueComponentName(this.regionPath, new ComponentName(wantedName));

            var builder = this.type.newComponentBuilder();
            builder.setName(componentName);
            if (api.ObjectHelper.iFrameSafeInstanceOf(builder, DescriptorBasedPageComponentBuilder)) {
                (<DescriptorBasedPageComponentBuilder>builder).setConfig(new RootDataSet());
            }
            var component = builder.build();
            this.pageRegions.addComponentAfter(component, this.regionPath, this.precedingComponent);
            return component;
        }
    }
}