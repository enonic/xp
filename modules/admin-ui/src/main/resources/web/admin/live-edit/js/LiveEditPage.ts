module LiveEdit {

    import PropertyTree = api.data.PropertyTree;
    import Component = api.content.page.region.Component;
    import Page = api.content.page.Page;
    import Regions = api.content.page.region.Regions;
    import Region = api.content.page.region.Region;
    import ComponentType = api.content.page.region.ComponentType;
    import ComponentName = api.content.page.region.ComponentName;
    import DescriptorBasedComponentBuilder = api.content.page.region.DescriptorBasedComponentBuilder;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import ComponentView = api.liveedit.ComponentView;
    import PageView = api.liveedit.PageView;
    import PageViewBuilder = api.liveedit.PageViewBuilder;
    import ItemView = api.liveedit.ItemView;
    import RegionView = api.liveedit.RegionView;
    import ItemViewId = api.liveedit.ItemViewId;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import TextComponentView = api.liveedit.text.TextComponentView;
    import DraggingComponentViewStartedEvent = api.liveedit.DraggingComponentViewStartedEvent;
    import DraggingComponentViewCompletedEvent = api.liveedit.DraggingComponentViewCompletedEvent;
    import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import ComponentRemoveEvent = api.liveedit.ComponentRemovedEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ComponentResetEvent = api.liveedit.ComponentResetEvent;
    import ItemViewIdProducer = api.liveedit.ItemViewIdProducer;
    import Shader = api.liveedit.Shader;
    import Highlighter = api.liveedit.Highlighter;
    import Cursor = api.liveedit.Cursor;

    export class LiveEditPage {

        private static INSTANCE: LiveEditPage;

        private pageView: PageView;

        static get(): LiveEditPage {
            return LiveEditPage.INSTANCE;
        }

        constructor() {

            api.liveedit.InitializeLiveEditEvent.on((event: api.liveedit.InitializeLiveEditEvent) => {

                var liveEditModel = event.getLiveEditModel();

                var body = api.dom.Body.get().loadExistingChildren();

                this.pageView = new PageViewBuilder().
                    setItemViewProducer(new ItemViewIdProducer()).
                    setLiveEditModel(liveEditModel).
                    setElement(body).
                    build();

                new api.liveedit.LiveEditPageViewReadyEvent(this.pageView).fire();

                api.ui.Tooltip.allowMultipleInstances(false);

                this.registerGlobalListeners();
            });

            LiveEditPage.INSTANCE = this;
        }


        private registerGlobalListeners(): void {

            api.liveedit.ComponentLoadedEvent.on((event: api.liveedit.ComponentLoadedEvent) => {

                if (api.liveedit.layout.LayoutItemType.get().equals(event.getItemView().getType())) {
                    LiveEdit.component.dragdropsort.DragDropSort.createSortableLayout(event.getItemView());
                } else {
                    LiveEdit.component.dragdropsort.DragDropSort.refreshSortable();
                }
            });

            ComponentResetEvent.on((event: ComponentResetEvent) => {
                LiveEdit.component.dragdropsort.DragDropSort.refreshSortable();
            });

            DraggingComponentViewStartedEvent.on(() => {
                Highlighter.get().hide();
                Shader.get().hide();
                Cursor.get().hide();

                // dragging anything should exit the text edit mode
                //this.exitTextEditModeIfNeeded();
            });

            DraggingComponentViewCompletedEvent.on(() => {
                Cursor.get().reset();
            });

        }

        getByItemId(id: ItemViewId) {
            return this.pageView.getItemViewById(id);
        }

        getItemViewByHTMLElement(htmlElement: HTMLElement) {
            return this.pageView.getItemViewByElement(htmlElement);
        }

        getComponentViewByElement(htmlElement: HTMLElement): ComponentView<Component> {
            return this.pageView.getComponentViewByElement(htmlElement);
        }

        getRegionViewByElement(htmlElement: HTMLElement): RegionView {
            return this.pageView.getRegionViewByElement(htmlElement);
        }

        addComponentView(componentView: ComponentView<Component>, toRegion: RegionView, atIndex: number) {

            toRegion.addComponentView(componentView, atIndex);

            if (componentView.getType().equals(api.liveedit.text.TextItemType.get())) {
                this.pageView.setTextEditMode(true);
                componentView.giveFocus();
            } else {
                componentView.select();
            }
        }

        createComponent(region: Region, type: ComponentType, precedingComponentView: ComponentView<Component>): Component {

            var componentName = new ComponentName(api.util.StringHelper.capitalize(api.util.StringHelper.removeWhitespaces(type.getShortName())));

            var builder = type.newComponentBuilder();
            builder.setName(componentName);

            if (api.ObjectHelper.iFrameSafeInstanceOf(builder, DescriptorBasedComponentBuilder)) {
                (<DescriptorBasedComponentBuilder<DescriptorBasedComponent>>builder).setConfig(new PropertyTree(api.Client.get().getPropertyIdProvider()));
            }
            var precedingComponent: Component = null;
            if (precedingComponentView) {
                precedingComponent = precedingComponentView.getComponent();
            }
            var component = builder.build();
            region.addComponentAfter(component, precedingComponent);
            return component;
        }
    }
}