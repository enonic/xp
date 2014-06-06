module LiveEdit {

    import RootDataSet = api.data.RootDataSet;
    import PageComponent = api.content.page.PageComponent;
    import PageRegions = api.content.page.PageRegions;
    import Region = api.content.page.region.Region;
    import PageComponentType = api.content.page.PageComponentType;
    import ComponentName = api.content.page.ComponentName;
    import ComponentPath = api.content.page.ComponentPath;
    import DescriptorBasedPageComponentBuilder = api.content.page.DescriptorBasedPageComponentBuilder;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;

    export class LiveEditPage {

        private static INSTANCE: LiveEditPage;

        private pageRegions: PageRegions;

        static get(): LiveEditPage {
            return LiveEditPage.INSTANCE;
        }

        constructor() {

            api.liveedit.InitializeLiveEditEvent.on((event: api.liveedit.InitializeLiveEditEvent) => {

                api.liveedit.PageItemType.get().setContent(event.getContent());
                api.liveedit.PageItemType.get().setSiteTemplate(event.getSiteTemplate());

                var body = api.dom.Body.getAndLoadExistingChildren();
                var map = new api.liveedit.PageComponentIdMapResolver(body).resolve();
                new api.liveedit.NewPageComponentIdMapEvent(map).fire();

                this.pageRegions = event.getPageRegions();
                pageItemViews = new api.liveedit.PageItemViewsParser(body, this.pageRegions).parse();
                pageItemViews.initializeEmpties();

                api.liveedit.PageComponentLoadedEvent.on((event: api.liveedit.PageComponentLoadedEvent) => {

                    pageItemViews.addItemView(event.getItemView());

                    if (event.getItemView().getType() == api.liveedit.layout.LayoutItemType.get()) {
                        LiveEdit.component.dragdropsort.DragDropSort.createSortableLayout(event.getItemView());
                    }
                });
            });


            LiveEditPage.INSTANCE = this;
        }

        createComponent(region: Region, type: PageComponentType, precedingComponent: ComponentPath): PageComponent {


            var wantedName = api.util.capitalize(api.util.removeInvalidChars(type.getShortName()));
            var componentName = this.pageRegions.ensureUniqueComponentName(region.getPath(), new ComponentName(wantedName));

            var builder = type.newComponentBuilder();
            builder.setName(componentName);

            if (api.ObjectHelper.iFrameSafeInstanceOf(builder, DescriptorBasedPageComponentBuilder)) {
                (<DescriptorBasedPageComponentBuilder<DescriptorBasedPageComponent>>builder).setConfig(new RootDataSet());
            }
            var component = builder.build();
            this.pageRegions.addComponentAfter(component, region.getPath(),
                precedingComponent ? precedingComponent.getComponentName() : null);
            return component;
        }

    }
}