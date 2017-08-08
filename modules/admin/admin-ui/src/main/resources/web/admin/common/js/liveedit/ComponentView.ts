module api.liveedit {

    import Component = api.content.page.region.Component;
    import ComponentPath = api.content.page.region.ComponentPath;
    import ComponentName = api.content.page.region.ComponentName;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import ComponentResetEvent = api.content.page.region.ComponentResetEvent;
    import Content = api.content.Content;
    import FragmentComponent = api.content.page.region.FragmentComponent;
    import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
    import i18n = api.util.i18n;
    import KeyBindings = api.ui.KeyBindings;
    import KeyBinding = api.ui.KeyBinding;
    import ElementHelper = api.dom.ElementHelper;
    import Element = api.dom.Element;

    export class ComponentViewBuilder<COMPONENT extends Component> {

        itemViewProducer: ItemViewIdProducer;

        type: ComponentItemType;

        parentRegionView: RegionView;

        parentElement: api.dom.Element;

        component: COMPONENT;

        element: api.dom.Element;

        positionIndex: number;

        contextMenuActions: api.ui.Action[];

        placeholder: ItemViewPlaceholder;

        viewer: api.ui.Viewer<any>;

        inspectActionRequired: boolean;

        /**
         * Optional. The ItemViewIdProducer of parentRegionView will be used if not set.
         */
        setItemViewProducer(value: ItemViewIdProducer): ComponentViewBuilder<COMPONENT> {
            this.itemViewProducer = value;
            return this;
        }

        setType(value: ComponentItemType): ComponentViewBuilder<COMPONENT> {
            this.type = value;
            return this;
        }

        setParentRegionView(value: RegionView): ComponentViewBuilder<COMPONENT> {
            this.parentRegionView = value;
            return this;
        }

        setParentElement(value: api.dom.Element): ComponentViewBuilder<COMPONENT> {
            this.parentElement = value;
            return this;
        }

        setComponent(value: COMPONENT): ComponentViewBuilder<COMPONENT> {
            this.component = value;
            return this;
        }

        setElement(value: api.dom.Element): ComponentViewBuilder<COMPONENT> {
            this.element = value;
            return this;
        }

        setPositionIndex(value: number): ComponentViewBuilder<COMPONENT> {
            this.positionIndex = value;
            return this;
        }

        setContextMenuActions(actions: api.ui.Action[]): ComponentViewBuilder<COMPONENT> {
            this.contextMenuActions = actions;
            return this;
        }

        setPlaceholder(value: ItemViewPlaceholder): ComponentViewBuilder<COMPONENT> {
            this.placeholder = value;
            return this;
        }

        setInspectActionRequired(value: boolean): ComponentViewBuilder<COMPONENT> {
            this.inspectActionRequired = value;
            return this;
        }

        setViewer(value: api.ui.Viewer<any>): ComponentViewBuilder<COMPONENT> {
            this.viewer = value;
            return this;
        }
    }

    export class ComponentView<COMPONENT extends Component> extends ItemView implements api.Cloneable {

        private parentRegionView: RegionView;

        protected component: COMPONENT;

        private moving: boolean = false;

        private itemViewAddedListeners: {(event: ItemViewAddedEvent): void}[] = [];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent): void}[] = [];

        private propertyChangedListener: (event: ComponentPropertyChangedEvent) => void;

        private resetListener: (event: ComponentResetEvent) => void;

        protected initOnAdd: boolean = true;

        public static debug: boolean = false;

        private keyBinding: KeyBinding[];

        constructor(builder: ComponentViewBuilder<COMPONENT>) {
            super(new ItemViewBuilder().
                    setItemViewIdProducer(builder.itemViewProducer
                        ? builder.itemViewProducer
                        : builder.parentRegionView.getItemViewIdProducer()).
                    setPlaceholder(builder.placeholder).
                    setViewer(builder.viewer).
                    setType(builder.type).
                    setElement(builder.element).
                    setParentView(builder.parentRegionView).
                    setParentElement(builder.parentElement).
                    setContextMenuTitle(new ComponentViewContextMenuTitle(builder.component, builder.type))
            );

            this.parentRegionView = builder.parentRegionView;

            this.addComponentContextMenuActions(builder.inspectActionRequired);

            this.propertyChangedListener = () => this.refreshEmptyState();
            this.resetListener = () => {
                // recreate the component view from scratch
                // if the component has been reset
                this.deselect();
                let clone = this.clone();
                this.replaceWith(clone);
                clone.select();
                clone.hideContextMenu();

                new api.liveedit.ComponentResetEvent(clone, this).fire();
            };

            this.setComponent(builder.component);
            this.onRemoved(event => this.unregisterComponentListeners(this.component));

            this.initKeyBoardBindings();

            // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
            //this.setDraggable(true);
            //this.onDragStart(this.handleDragStart2.bind(this));
            //this.onDrag(this.handleDrag.bind(this));
            //this.onDragEnd(this.handleDragEnd.bind(this));
        }

        //remove(): ComponentView {
        //    super.remove();
        //    this.unregisterComponentListeners(this.component);
        //    return this;
        //}

        private registerComponentListeners(component: COMPONENT) {
            component.onReset(this.resetListener);
            component.onPropertyChanged(this.propertyChangedListener);
        }

        private unregisterComponentListeners(component: COMPONENT) {
            component.unPropertyChanged(this.propertyChangedListener);
            component.unReset(this.resetListener);
        }

        private addComponentContextMenuActions(inspectActionRequired: boolean) {
            let isFragmentContent = this.liveEditModel.getContent().getType().isFragment();
            let parentIsPage = api.ObjectHelper.iFrameSafeInstanceOf(this.getRegionView(), PageView);
            let isTopFragmentComponent = parentIsPage && isFragmentContent;

            let actions: api.ui.Action[] = [];

            if (!isTopFragmentComponent) {
                actions.push(this.createSelectParentAction());
                actions.push(this.createInsertAction());
            }

            if (inspectActionRequired) {
                actions.push(new api.ui.Action(i18n('live.view.inspect')).onExecuted(() => {
                    new ComponentInspectedEvent(this).fire();
                }));
            }

            actions.push(new api.ui.Action(i18n('live.view.reset')).onExecuted(() => {
                this.component.reset();
            }));

            if (!isTopFragmentComponent) {
                actions.push(new api.ui.Action(i18n('live.view.remove')).onExecuted(() => {
                    this.deselect();
                    this.remove();
                }));
                actions.push(new api.ui.Action(i18n('live.view.duplicate')).onExecuted(() => {
                    this.deselect();

                    let duplicatedComponent = <COMPONENT> this.getComponent().duplicate();
                    let duplicatedView = this.duplicate(duplicatedComponent);

                    duplicatedView.showLoadingSpinner();

                    new ComponentDuplicatedEvent(this, duplicatedView).fire();
                }));
            }

            let isFragmentComponent = this instanceof api.liveedit.fragment.FragmentComponentView;

            if (!isFragmentComponent && this.liveEditModel.isFragmentAllowed()) {
                actions.push(new api.ui.Action(i18n('live.view.create.fragment')).onExecuted(() => {
                    this.deselect();
                    this.createFragment().then((content: Content): void => {
                        // replace created fragment in place of source component
                        let fragmentCmpView = <FragmentComponentView> this.createComponentView(
                            api.liveedit.fragment.FragmentItemType.get());
                        fragmentCmpView.getComponent().setFragment(content.getContentId(), content.getDisplayName());
                        this.addComponentView(fragmentCmpView, this.getNewItemIndex());
                        this.remove();
                        new ComponentFragmentCreatedEvent(fragmentCmpView, this.getComponent().getType(), content).fire();
                    });
                }));
            }

            this.addContextMenuActions(actions);
        }

        private initKeyBoardBindings() {
            const removeHandler = () => {
                this.deselect();
                this.remove();
                return true;
            };
            this.keyBinding = [
                new KeyBinding('del', removeHandler),
                new KeyBinding('backspace', removeHandler)
            ];

        }

        select(clickPosition?: Position, menuPosition?: ItemViewContextMenuPosition, isNew: boolean = false,
               rightClicked: boolean = false) {

            Element.fromHtmlElement(<HTMLElement>window.frameElement).giveFocus();

            super.select(clickPosition, menuPosition, isNew, rightClicked);
            api.ui.KeyBindings.get().bindKeys(this.keyBinding);

        }

        deselect(silent?: boolean) {
            api.ui.KeyBindings.get().unbindKeys(this.keyBinding);

            super.deselect(silent);
        }

        remove(): ComponentView<Component> {
            this.unregisterComponentListeners(this.component);

            let parentView = this.getParentItemView();
            if (parentView) {
                parentView.removeComponentView(this);
            }

            super.remove();

            return this;
        }

        getType(): ComponentItemType {
            return <ComponentItemType>super.getType();
        }

        setComponent(component: COMPONENT) {
            if (component) {
                if (this.component) {
                    this.unregisterComponentListeners(this.component);
                }
                this.registerComponentListeners(component);
            }

            this.component = component;
            this.refreshEmptyState();
        }

        getComponent(): COMPONENT {
            return this.component;
        }

        hasComponentPath(): boolean {
            return this.component && this.component.hasPath();
        }

        getComponentPath(): ComponentPath {
            return this.hasComponentPath() ? this.component.getPath() : null;
        }

        getName(): string {
            return this.component && this.component.getName() ? this.component.getName().toString() : null;
        }

        getParentItemView(): RegionView {
            return this.parentRegionView;
        }

        setParentItemView(regionView: RegionView) {
            super.setParentItemView(regionView);
            this.parentRegionView = regionView;
        }

        setMoving(value: boolean) {
            this.moving = value;
        }

        isMoving(): boolean {
            return this.moving;
        }

        clone(): ComponentView<Component> {

            let isFragmentContent = this.liveEditModel.getContent().getType().isFragment();
            let index = isFragmentContent ? 0 : this.getParentItemView().getComponentViewIndex(this);

            let clone = this.getType().createView(
                new CreateItemViewConfig<RegionView,Component>().
                    setParentView(this.getParentItemView()).
                    setParentElement(this.getParentElement()).
                    setData(this.getComponent()).
                    setPositionIndex(index));

            return clone;
        }

        protected duplicate(duplicate: COMPONENT): ComponentView<Component> {

            let parentView = this.getParentItemView();
            let index = parentView.getComponentViewIndex(this);

            let duplicateView = this.getType().createView(
                new CreateItemViewConfig<RegionView,Component>().
                    setParentView(this.getParentItemView()).
                    setParentElement(this.getParentElement()).
                    setData(duplicate).
                    setPositionIndex(index + 1));

            duplicateView.skipInitOnAdd();
            parentView.addComponentView(duplicateView, index + 1);

            return duplicateView;
        }

        private createFragment(): wemQ.Promise<Content> {
            let contentId = this.getPageView().getLiveEditModel().getContent().getContentId();
            let config = this.getPageView().getLiveEditModel().getPageModel().getConfig();

            let request = new api.content.page.region.CreateFragmentRequest(contentId).setConfig(config).setComponent(
                this.getComponent());

            return request.sendAndParse();
        }

        toString() {
            let extra = '';
            if (this.hasComponentPath()) {
                extra = ' : ' + this.getComponentPath().toString();
            }
            return super.toString() + extra;
        }

        replaceWith(replacement: ComponentView<Component>) {
            if (ComponentView.debug) {
                console.log('ComponentView[' + this.toString() + '].replaceWith', this, replacement);
            }
            super.replaceWith(replacement);

            // unbind the old view from the component and bind the new one
            this.unregisterComponentListeners(this.component);
            this.unbindMouseListeners();

            let parentIsPage = api.ObjectHelper.iFrameSafeInstanceOf(this.getParentItemView(), PageView);
            if (parentIsPage) {
                this.getPageView().unregisterFragmentComponentView(this);
                this.getPageView().registerFragmentComponentView(replacement);
            } else {
                let index = this.getParentItemView().getComponentViewIndex(this);

                let parentRegionView = this.parentRegionView;
                this.parentRegionView.unregisterComponentView(this);
                parentRegionView.registerComponentView(replacement, index);
            }
        }

        moveToRegion(toRegionView: RegionView, toIndex: number, dragged?: boolean) {
            if (ComponentView.debug) {
                console.log('ComponentView[' + this.toString() + '].moveToRegion', this, this.parentRegionView, toRegionView);
            }

            this.moving = false;

            if (this.parentRegionView.getRegionPath().equals(toRegionView.getRegionPath()) &&
                toIndex === this.parentRegionView.getComponentViewIndex(this)) {

                if (ComponentView.debug) {
                    console.debug('Dropped in the same region at the same index, no need to move', this.parentRegionView, toRegionView);
                }
                return;
            }

            // Unregister from previous region...
            let parentView = this.getParentItemView();
            if (parentView) {
                parentView.removeComponentView(this);
            }

            // Register with new region...
            toRegionView.addComponentView(this, toIndex, false, dragged);
            if (parentView && this.component) {
                this.registerComponentListeners(this.component);
            }
        }

        onItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners.push(listener);
        }

        unItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners = this.itemViewAddedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        notifyItemViewAdded(view: ItemView, isNew: boolean = false) {
            let event = new ItemViewAddedEvent(view, isNew);
            this.itemViewAddedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners.push(listener);
        }

        unItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners = this.itemViewRemovedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        notifyItemViewRemoved(view: ItemView) {
            let event = new ItemViewRemovedEvent(view);
            this.itemViewRemovedListeners.forEach((listener) => {
                listener(event);
            });
        }

        getNewItemIndex(): number {
            return this.getParentItemView().getComponentViewIndex(this) + 1;
        }

        addComponentView(componentView: ComponentView<Component>, index: number) {
            this.getParentItemView().addComponentView(componentView, index, true);
        }

        getRegionView(): RegionView {
            return this.getParentItemView();
        }

        isEmpty(): boolean {
            return !this.component || this.component.isEmpty();
        }

        private skipInitOnAdd(): void {
            this.initOnAdd = false;
        }

        static findParentRegionViewHTMLElement(htmlElement: HTMLElement): HTMLElement {

            let parentItemView = ItemView.findParentItemViewAsHTMLElement(htmlElement);
            while (!RegionView.isRegionViewFromHTMLElement(parentItemView)) {
                parentItemView = ItemView.findParentItemViewAsHTMLElement(parentItemView);
            }
            return parentItemView;
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragStart2(event: DragEvent) {

            if (event.target === this.getHTMLElement()) {
                event.dataTransfer.effectAllowed = 'move';
                //event.dataTransfer.setData('text/plain', 'This text may be dragged');
                console.log('ComponentView[' + this.getItemId().toNumber() + '].handleDragStart', event, this.getHTMLElement());
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDrag(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log('ComponentView[' + this.getItemId().toNumber() + '].handleDrag', event, this.getHTMLElement());
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragEnd(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log('ComponentView[' + this.getItemId().toNumber() + '].handleDragEnd', event, this.getHTMLElement());
                //this.hideTooltip();
            }
        }
    }
}
