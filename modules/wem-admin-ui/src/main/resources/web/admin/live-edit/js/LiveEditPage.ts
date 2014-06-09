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
    import SortableStartEvent = api.liveedit.SortableStartEvent;
    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;
    import PageComponentDeselectEvent = api.liveedit.PageComponentDeselectEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import PageComponentSelectComponentEvent = api.liveedit.PageComponentSelectComponentEvent;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;

    export class LiveEditPage {

        private static INSTANCE: LiveEditPage;

        private pageItemViews: PageItemViews;

        private pageRegions: PageRegions;

        private highlighter: LiveEdit.ui.Highlighter;

        static get(): LiveEditPage {
            return LiveEditPage.INSTANCE;
        }

        constructor() {

            api.liveedit.InitializeLiveEditEvent.on((event: api.liveedit.InitializeLiveEditEvent) => {

                api.liveedit.PageItemType.get().setContent(event.getContent());
                api.liveedit.PageItemType.get().setSiteTemplate(event.getSiteTemplate());

                var body = api.dom.Body.getAndLoadExistingChildren();

                this.pageRegions = event.getPageRegions();
                this.pageItemViews = new api.liveedit.PageItemViewsParser(body, event.getContent(), event.getPageRegions()).parse();
                this.pageItemViews.initializeEmpties();

                api.liveedit.PageComponentLoadedEvent.on((event: api.liveedit.PageComponentLoadedEvent) => {

                    this.pageItemViews.addItemView(event.getItemView());

                    if (event.getItemView().getType() == api.liveedit.layout.LayoutItemType.get()) {
                        LiveEdit.component.dragdropsort.DragDropSort.createSortableLayout(event.getItemView());
                    }
                });

                this.highlighter = new LiveEdit.ui.Highlighter();
                api.dom.Body.get().appendChild(this.highlighter);

                this.registerGlobalListeners();
            });

            LiveEditPage.INSTANCE = this;
        }

        private registerGlobalListeners(): void {
            wemjq(window).on('mouseOverComponent.liveEdit', (event, component?: ItemView) => {
                this.highlighter.showOnComponent(component);
            });
            wemjq(window).on('mouseOutComponent.liveEdit', () => {
                this.highlighter.hide();
            });
            PageComponentSelectComponentEvent.on((event: PageComponentSelectComponentEvent) => {
                var component = event.getItemView();

                // Highlighter should not be shown when type page is selected
                if (component.getType().equals(api.liveedit.PageItemType.get())) {
                    this.highlighter.hide();
                    return;
                } else if (component.getType().equals(api.liveedit.image.ImageItemType.get())) {
                    var image = (<api.liveedit.image.ImageView>component).getImage();
                    if (image) {
                        image.getEl().addEventListener("load", () => this.highlighter.showOnComponent(component));
                    }
                }

                this.highlighter.showOnComponent(component);
            });
            PageComponentDeselectEvent.on(() => {
                this.highlighter.hide();
            });
            PageComponentResetEvent.on((event: PageComponentResetEvent) => {
                this.highlighter.showOnComponent(event.getComponentView());
            });
            SortableStartEvent.on(() => {
                this.highlighter.hide();
            });
            PageComponentRemoveEvent.on(() => {
                this.highlighter.hide();
            });
            wemjq(window).on('editTextComponent.liveEdit', () => {
                this.highlighter.hide();
            });
            wemjq(window).on('resizeBrowserWindow.liveEdit', () => {
                this.highlighter.showOnComponent(this.pageItemViews.getSelectedView());
            });
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

        movePageComponent(pageComponent: PageComponentView<PageComponent>, regionView: RegionView,
                          precedingComponentView: PageComponentView<PageComponent>) {

            var precedingComponent: PageComponent = null;
            if (precedingComponentView) {
                precedingComponent = precedingComponentView.getPageComponent();
            }
            this.pageRegions.moveComponent(pageComponent.getPageComponent(), regionView.getRegion(), precedingComponent);
        }

        duplicatePageComponent(pageComponentView: PageComponentView<PageComponent>) {

            var origin = pageComponentView.getPageComponent();
            var duplicatedPageComponent = this.pageRegions.duplicateComponent(origin.getPath());
            var duplicatedView = pageComponentView.duplicate(duplicatedPageComponent);
            this.pageItemViews.addItemView(duplicatedView);
            new PageComponentDuplicateEvent(pageComponentView, duplicatedView).fire();
            duplicatedView.select();
        }

        removePageComponentView(pageComponentView: PageComponentView<PageComponent>) {
            this.pageItemViews.removePageComponentView(pageComponentView);
        }

        hasSelectedView(): boolean {
            return this.pageItemViews.hasSelectedView();
        }

        deselectSelectedView() {
            this.pageItemViews.deselectSelectedView();
        }

        createComponent(region: Region, type: PageComponentType, precedingComponentView: PageComponentView<PageComponent>): PageComponent {

            var wantedName = api.util.capitalize(api.util.removeInvalidChars(type.getShortName()));
            var componentName = this.pageRegions.ensureUniqueComponentName(region.getPath(), new ComponentName(wantedName));

            var builder = type.newComponentBuilder();
            builder.setName(componentName);

            if (api.ObjectHelper.iFrameSafeInstanceOf(builder, DescriptorBasedPageComponentBuilder)) {
                (<DescriptorBasedPageComponentBuilder<DescriptorBasedPageComponent>>builder).setConfig(new RootDataSet());
            }
            var component = builder.build();
            this.pageRegions.addComponentAfter(component, region, precedingComponentView.getPageComponent());
            return component;
        }

    }
}