module LiveEdit {

    import RootDataSet = api.data.RootDataSet;
    import PageComponent = api.content.page.PageComponent;
    import PageRegions = api.content.page.PageRegions;
    import Region = api.content.page.region.Region;
    import PageComponentType = api.content.page.PageComponentType;
    import ComponentName = api.content.page.ComponentName;
    import DescriptorBasedPageComponentBuilder = api.content.page.DescriptorBasedPageComponentBuilder;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import PageView = api.liveedit.PageView;
    import PageViewBuilder = api.liveedit.PageViewBuilder;
    import ItemView = api.liveedit.ItemView;
    import RegionView = api.liveedit.RegionView;
    import ItemViewId = api.liveedit.ItemViewId;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import TextComponentView = api.liveedit.text.TextComponentView;
    import SortableStartEvent = api.liveedit.SortableStartEvent;
    import SortableStopEvent = api.liveedit.SortableStopEvent;
    import PageComponentAddedEvent = api.liveedit.PageComponentAddedEvent;
    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;
    import ItemViewIdProducer = api.liveedit.ItemViewIdProducer;

    export class LiveEditPage {

        private static INSTANCE: LiveEditPage;

        private pageView: PageView;

        private pageRegions: PageRegions;

        private highlighter: LiveEdit.ui.Highlighter;

        private shader: LiveEdit.ui.Shader;

        private cursor: LiveEdit.ui.Cursor;

        static get(): LiveEditPage {
            return LiveEditPage.INSTANCE;
        }

        constructor() {

            api.liveedit.InitializeLiveEditEvent.on((event: api.liveedit.InitializeLiveEditEvent) => {

                api.liveedit.PageItemType.get().setContent(event.getContent());
                api.liveedit.PageItemType.get().setSiteTemplate(event.getSiteTemplate());

                var body = api.dom.Body.getAndLoadExistingChildren();
                //body.traverse( (el: api.dom.Element) => {
                //el.setDraggable(false);
                //});

                this.pageRegions = event.getPageRegions();
                this.pageView = new PageView(new PageViewBuilder().
                    setItemViewProducer(new ItemViewIdProducer()).
                    setPageRegions(event.getPageRegions()).
                    setContent(event.getContent()).
                    setElement(body));

                api.liveedit.PageComponentLoadedEvent.on((event: api.liveedit.PageComponentLoadedEvent) => {

                    if (api.liveedit.layout.LayoutItemType.get().equals(event.getItemView().getType())) {
                        LiveEdit.component.dragdropsort.DragDropSort.createSortableLayout(event.getItemView());
                    }
                });

                this.highlighter = new LiveEdit.ui.Highlighter();
                api.dom.Body.get().appendChild(this.highlighter);

                api.ui.Tooltip.allowMultipleInstances(false);

                this.shader = new LiveEdit.ui.Shader();

                this.cursor = new LiveEdit.ui.Cursor();

                this.registerGlobalListeners();
            });

            LiveEditPage.INSTANCE = this;
        }

        private registerGlobalListeners(): void {

            this.pageView.onMouseEnterView((view: ItemView) => {
                if (this.hasSelectedView() || LiveEdit.component.dragdropsort.DragDropSort.isDragging()) {
                    return;
                }

                this.highlighter.highlightItemView(view);
                this.cursor.displayItemViewCursor(view);
                view.showTooltip();
            });

            this.pageView.onMouseLeaveView((view: ItemView) => {
                if (this.hasSelectedView() || LiveEdit.component.dragdropsort.DragDropSort.isDragging()) {
                    return;
                }

                this.highlighter.hide();
                this.cursor.reset();
                view.hideTooltip();
            });

            ItemViewSelectedEvent.on((event: ItemViewSelectedEvent) => {
                var component = event.getItemView();

                component.hideTooltip();
                // Highlighter should not be shown when type page is selected
                if (component.getType().equals(api.liveedit.PageItemType.get())) {
                    this.highlighter.hide();
                    this.shader.shadeItemView(component);
                    return;
                } else if (component.getType().equals(api.liveedit.image.ImageItemType.get())) {
                    var image = (<api.liveedit.image.ImageComponentView>component).getImage();
                    if (image) {
                        image.getEl().addEventListener("load", () => {
                            this.highlighter.highlightItemView(component);
                            this.shader.shadeItemView(component);
                        });
                    }
                }

                this.highlighter.highlightItemView(component);
                this.shader.shadeItemView(component);
                this.cursor.displayItemViewCursor(component);
            });
            ItemViewDeselectEvent.on(() => {
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
                this.cursor.hide();
            });
            SortableStopEvent.on(() => {
                this.cursor.reset();
            });
            PageComponentRemoveEvent.on(() => {
                this.highlighter.hide();
                this.shader.hide();
            });
            wemjq(window).on('editTextComponent.liveEdit', (event: JQueryEventObject, component: TextComponentView) => {
                this.highlighter.hide();
                this.shader.shadeItemView(component);
            });
            wemjq(window).on('resizeBrowserWindow.liveEdit', () => {
                var selectedView = this.pageView.getSelectedView();
                this.highlighter.highlightItemView(selectedView);
                this.shader.shadeItemView(selectedView);
            });
            LiveEdit.ui.ShaderClickedEvent.on(() => {
                var selectedView = this.pageView.getSelectedView();
                if (selectedView) {
                    selectedView.deselect();
                }
            });
        }

        getByItemId(id: ItemViewId) {
            return this.pageView.getItemViewById(id);
        }

        getItemViewByHTMLElement(htmlElement: HTMLElement) {
            return this.pageView.getItemViewByElement(htmlElement);
        }

        getPageComponentViewByElement(htmlElement: HTMLElement): PageComponentView<PageComponent> {
            return this.pageView.getPageComponentViewByElement(htmlElement);
        }

        getRegionViewByElement(htmlElement: HTMLElement): RegionView {
            return this.pageView.getRegionViewByElement(htmlElement);
        }

        addPageComponentView(pageComponentView: PageComponentView<PageComponent>, toRegion: RegionView, atIndex: number) {

            toRegion.addPageComponentView(pageComponentView, atIndex);

            var closestParentLayoutComponentView = LayoutComponentView.getClosestParentLayoutComponentView(pageComponentView);
            if (closestParentLayoutComponentView) {
                closestParentLayoutComponentView.addPadding();
            }

            new PageComponentAddedEvent().setPageComponentView(pageComponentView).fire();
            pageComponentView.select();
        }

        movePageComponent(pageComponentView: PageComponentView<PageComponent>, regionView: RegionView,
                          precedingComponentView: PageComponentView<PageComponent>) {

            if (pageComponentView.getParentElement().getHTMLElement() == pageComponentView.getHTMLElement().parentElement) {

                console.log("LiveEditPage.movePageComponent parents are the same");
            }
            else {
                console.log("LiveEditPage.movePageComponent parents are NOT the same");
            }

            pageComponentView.moveToRegion(regionView, precedingComponentView);
        }

        duplicatePageComponent(pageComponentView: PageComponentView<PageComponent>) {

            var origin = pageComponentView.getPageComponent();
            var duplicatedPageComponent = origin.duplicateComponent();
            var duplicatedView = pageComponentView.duplicate(duplicatedPageComponent);
            new PageComponentDuplicateEvent(pageComponentView, duplicatedView).fire();
            duplicatedView.select();
        }

        removePageComponentView(pageComponentView: PageComponentView<PageComponent>) {

            var regionView = pageComponentView.getParentItemView();
            regionView.removePageComponentView(pageComponentView);
            new PageComponentRemoveEvent(pageComponentView).fire();
        }

        hasSelectedView(): boolean {
            return this.pageView.hasSelectedView();
        }

        deselectSelectedView() {
            this.pageView.deselectSelectedView();
        }

        createComponent(region: Region, type: PageComponentType, precedingComponentView: PageComponentView<PageComponent>): PageComponent {

            var wantedName = api.util.capitalize(api.util.removeInvalidChars(type.getShortName()));
            var componentName = region.ensureUniqueComponentName(new ComponentName(wantedName));

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
            region.addComponentAfter(component, precedingPageComponent);
            return component;
        }
    }
}