module LiveEdit {

    import RootDataSet = api.data.RootDataSet;
    import PageComponent = api.content.page.PageComponent;
    import PageRegions = api.content.page.PageRegions;
    import Region = api.content.page.region.Region;
    import PageComponentType = api.content.page.PageComponentType;
    import ComponentName = api.content.page.ComponentName;
    import DescriptorBasedPageComponentBuilder = api.content.page.DescriptorBasedPageComponentBuilder;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import PageItemViews = api.liveedit.PageItemViews;
    import PageComponentView = api.liveedit.PageComponentView;
    import ItemView = api.liveedit.ItemView;
    import RegionView = api.liveedit.RegionView;
    import ItemViewId = api.liveedit.ItemViewId;
    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;

    export class LiveEditPage {

        private static INSTANCE: LiveEditPage;

        private pageItemViews: PageItemViews;

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
                this.pageItemViews = new api.liveedit.PageItemViewsParser(body, event.getContent(), event.getPageRegions()).parse();
                this.pageItemViews.initializeEmpties();

                api.liveedit.PageComponentLoadedEvent.on((event: api.liveedit.PageComponentLoadedEvent) => {

                    this.pageItemViews.addItemView(event.getItemView());

                    if (event.getItemView().getType() == api.liveedit.layout.LayoutItemType.get()) {
                        LiveEdit.component.dragdropsort.DragDropSort.createSortableLayout(event.getItemView());
                    }
                });
            });


            LiveEditPage.INSTANCE = this;
        }

        getByItemId(id: ItemViewId) {
            return this.pageItemViews.getByItemId(id);
        }

        getItemViewByHTMLElement(htmlElement: HTMLElement) {
            return this.pageItemViews.getItemViewByElement(htmlElement);
        }

        getPageComponentViewByElement(htmlElement: HTMLElement): PageComponentView<PageComponent> {
            return this.pageItemViews.getPageComponentViewByElement(htmlElement);
        }

        getRegionViewByElement(htmlElement: HTMLElement): RegionView {
            return this.pageItemViews.getRegionViewByElement(htmlElement);
        }

        addItemView(itemView: ItemView) {
            this.pageItemViews.addItemView(itemView);
        }

        duplicatePageComponent(pageComponentView: PageComponentView<PageComponent>) {

            var origin = pageComponentView.getPageComponent();
            var duplicatedPageComponent = this.pageRegions.duplicateComponent(origin.getPath());
            var duplicatedView = pageComponentView.duplicate(duplicatedPageComponent);
            this.pageItemViews.addItemView(duplicatedView);
            new PageComponentDuplicateEvent(pageComponentView, duplicatedView).fire();
            LiveEdit.component.Selection.handleSelect(duplicatedView);
        }

        removePageComponentView(pageComponentView: PageComponentView<PageComponent>) {
            this.pageItemViews.removePageComponentView(pageComponentView);
        }

        createComponent(region: Region, type: PageComponentType, precedingComponent: ComponentName): PageComponent {


            var wantedName = api.util.capitalize(api.util.removeInvalidChars(type.getShortName()));
            var componentName = this.pageRegions.ensureUniqueComponentName(region.getPath(), new ComponentName(wantedName));

            var builder = type.newComponentBuilder();
            builder.setName(componentName);

            if (api.ObjectHelper.iFrameSafeInstanceOf(builder, DescriptorBasedPageComponentBuilder)) {
                (<DescriptorBasedPageComponentBuilder<DescriptorBasedPageComponent>>builder).setConfig(new RootDataSet());
            }
            var component = builder.build();
            this.pageRegions.addComponentAfter(component, region.getPath(), precedingComponent);
            return component;
        }

    }
}