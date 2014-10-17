module LiveEdit {

    import RootDataSet = api.data.RootDataSet;
    import PageComponent = api.content.page.PageComponent;
    import Page = api.content.page.Page;
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
    import DraggingPageComponentViewStartedEvent = api.liveedit.DraggingPageComponentViewStartedEvent;
    import DraggingPageComponentViewCompletedEvent = api.liveedit.DraggingPageComponentViewCompletedEvent;
    import PageComponentAddedEvent = api.liveedit.PageComponentAddedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;
    import TextComponentStartEditingEvent = api.liveedit.text.TextComponentStartEditingEvent;
    import TextComponentEditedEvent = api.liveedit.text.TextComponentEditedEvent;
    import ItemViewIdProducer = api.liveedit.ItemViewIdProducer;

    export class LiveEditPage {

        private static INSTANCE: LiveEditPage;

        private pageView: PageView;

        private highlighter: LiveEdit.ui.Highlighter;

        private shader: LiveEdit.ui.Shader;

        private cursor: LiveEdit.ui.Cursor;

        static get(): LiveEditPage {
            return LiveEditPage.INSTANCE;
        }

        constructor() {

            api.liveedit.InitializeLiveEditEvent.on((event: api.liveedit.InitializeLiveEditEvent) => {

                api.liveedit.PageItemType.get().setContent(event.getContent());
                api.liveedit.PageItemType.get().setSite(event.getSite());

                var body = api.dom.Body.get().loadExistingChildren();
                //body.traverse( (el: api.dom.Element) => {
                //el.setDraggable(false);
                //});

                this.pageView = new PageViewBuilder().
                    setSite(event.getSite()).
                    setItemViewProducer(new ItemViewIdProducer()).
                    setPage(event.getPageModel()).
                    setContent(event.getContent()).
                    setElement(body).
                    build();

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

                this.pageView.toItemViewArray().forEach((itemView: ItemView) => {
                    this.setItemViewListeners(itemView);
                });

                this.registerGlobalListeners();
            });

            LiveEditPage.INSTANCE = this;
        }

        private registerGlobalListeners(): void {

            this.pageView.onItemViewAdded((event: api.liveedit.ItemViewAddedEvent) => {
                this.setItemViewListeners(event.getView());
            });

            ItemViewSelectedEvent.on((event: ItemViewSelectedEvent) => {
                var component = event.getItemView();

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
            ItemViewDeselectEvent.on((event: ItemViewDeselectEvent) => {
                this.highlighter.hide();
                this.shader.hide();
                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getItemView(), TextComponentView)) {
                    LiveEdit.component.dragdropsort.DragDropSort.cancelDragDrop('');
                }
            });
            PageComponentResetEvent.on((event: PageComponentResetEvent) => {
                this.highlighter.highlightItemView(event.getComponentView());
                this.shader.shadeItemView(event.getComponentView());
            });
            DraggingPageComponentViewStartedEvent.on(() => {
                this.highlighter.hide();
                this.shader.hide();
                this.cursor.hide();
            });
            DraggingPageComponentViewCompletedEvent.on(() => {
                this.cursor.reset();
            });
            PageComponentRemoveEvent.on((event: PageComponentRemoveEvent) => {
                this.highlighter.hide();
                this.shader.hide();
            });
            wemjq(window).on('resizeBrowserWindow.liveEdit', () => {
                var selectedView = this.pageView.getSelectedView();
                this.highlighter.highlightItemView(selectedView);
                this.shader.shadeItemView(selectedView);
            });
            LiveEdit.ui.ShaderClickedEvent.on(() => this.deselectSelectedView());

            TextComponentStartEditingEvent.on((event: TextComponentStartEditingEvent) => {
                this.highlighter.hide();
                LiveEdit.component.dragdropsort.DragDropSort.cancelDragDrop(
                    api.liveedit.text.TextItemType.get().getConfig().getCssSelector());
            });

            TextComponentEditedEvent.on((event: TextComponentEditedEvent) => {
                this.shader.shadeItemView(event.getView());
            });
        }

        setItemViewListeners(itemView: ItemView) {
            itemView.onMouseOverView(() => {
                if (this.hasSelectedView() || LiveEdit.component.dragdropsort.DragDropSort.isDragging()) {
                    return;
                }

                this.highlighter.highlightItemView(itemView);
                this.cursor.displayItemViewCursor(itemView);
                itemView.showTooltip();
            });

            itemView.onMouseOutView(() => {
                if (this.hasSelectedView() || LiveEdit.component.dragdropsort.DragDropSort.isDragging()) {
                    return;
                }

                this.highlighter.hide();
                this.cursor.reset();
                itemView.hideTooltip();
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

            new PageComponentAddedEvent().setPageComponentView(pageComponentView).fire();
            pageComponentView.select();
        }

        hasSelectedView(): boolean {
            return this.pageView.hasSelectedView();
        }

        deselectSelectedView() {
            this.pageView.deselectSelectedView();
        }

        createComponent(region: Region, type: PageComponentType, precedingComponentView: PageComponentView<PageComponent>): PageComponent {

            var componentName = new ComponentName(api.util.StringHelper.capitalize(api.util.StringHelper.removeWhitespaces(type.getShortName())));

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