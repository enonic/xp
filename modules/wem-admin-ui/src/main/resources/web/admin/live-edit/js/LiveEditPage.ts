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
    import LayoutView = api.liveedit.layout.LayoutView;
    import TextView = api.liveedit.text.TextView;
    import SortableStartEvent = api.liveedit.SortableStartEvent;
    import PageComponentAddedEvent = api.liveedit.PageComponentAddedEvent;
    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;
    import PageComponentDeselectEvent = api.liveedit.PageComponentDeselectEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;

    export class LiveEditPage {

        private static INSTANCE: LiveEditPage;

        private pageItemViews: PageItemViews;

        private pageRegions: PageRegions;

        private highlighter: LiveEdit.ui.Highlighter;

        private shader: LiveEdit.ui.Shader;

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

                api.ui.Tooltip.allowMultipleInstances(false);

                this.shader = new LiveEdit.ui.Shader();

                this.registerGlobalListeners();
            });

            LiveEditPage.INSTANCE = this;
        }

        private registerGlobalListeners(): void {
            wemjq(window).on('mouseOverComponent.liveEdit', (event, component?: ItemView) => {
                this.highlighter.highlightItemView(component);
                component.showTooltip();
            });
            wemjq(window).on('mouseOutComponent.liveEdit', (event, component?: ItemView) => {
                this.highlighter.hide();
                component.hideTooltip();
            });
            ItemViewSelectedEvent.on((event: ItemViewSelectedEvent) => {
                var component = event.getItemView();

                // Highlighter should not be shown when type page is selected
                if (component.getType().equals(api.liveedit.PageItemType.get())) {
                    this.highlighter.hide();
                    this.shader.shadeItemView(component);
                    return;
                } else if (component.getType().equals(api.liveedit.image.ImageItemType.get())) {
                    var image = (<api.liveedit.image.ImageView>component).getImage();
                    if (image) {
                        image.getEl().addEventListener("load", () => {
                            this.highlighter.highlightItemView(component);
                            this.shader.shadeItemView(component);
                        });
                    }
                }

                this.highlighter.highlightItemView(component);
                this.shader.shadeItemView(component);
            });
            PageComponentDeselectEvent.on(() => {
                this.highlighter.hide();
                this.shader.hide();
            });
            PageComponentResetEvent.on((event: PageComponentResetEvent) => {
                this.highlighter.highlightItemView(event.getComponentView());
                this.shader.shadeItemView(event.getComponentView());
            });
            SortableStartEvent.on(() => {
                this.highlighter.hide();
                this.shader.hide();
            });
            PageComponentRemoveEvent.on(() => {
                this.highlighter.hide();
                this.shader.hide();
            });
            wemjq(window).on('editTextComponent.liveEdit', (event:JQueryEventObject, component: TextView) => {
                this.highlighter.hide();
                this.shader.shadeItemView(component);
            });
            wemjq(window).on('resizeBrowserWindow.liveEdit', () => {
                var selectedView = this.pageItemViews.getSelectedView();
                this.highlighter.highlightItemView(selectedView);
                this.shader.shadeItemView(selectedView);
            });
            LiveEdit.ui.ShaderClickedEvent.on(() => {
                var selectedView = this.pageItemViews.getSelectedView();
                if (selectedView) {
                    selectedView.deselect();
                }
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

        addPageComponentView(pageComponentView: PageComponentView<PageComponent>, toRegion: RegionView, atIndex: number) {

            this.addItemView(pageComponentView);

            toRegion.addPageComponentView(pageComponentView, atIndex);

            pageComponentView.empty();

            var closestParentLayoutView = LayoutView.getClosestParentLayoutView(pageComponentView);
            if (closestParentLayoutView) {
                closestParentLayoutView.addPadding();
            }

            new PageComponentAddedEvent().setPageComponentView(pageComponentView).fire();
            pageComponentView.select();
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

            var regionView = pageComponentView.getParentItemView();
            regionView.removePageComponentView(pageComponentView);
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
            var precedingPageComponent: PageComponent = null;
            if (precedingComponentView) {
                precedingPageComponent = precedingComponentView.getPageComponent();
            }
            var component = builder.build();
            this.pageRegions.addComponentAfter(component, region, precedingPageComponent);
            return component;
        }
    }
}