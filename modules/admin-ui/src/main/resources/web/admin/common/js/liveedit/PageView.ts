module api.liveedit {

    import PropertyTree = api.data.PropertyTree;
    import Content = api.content.Content;
    import Page = api.content.page.Page;
    import PageModel = api.content.page.PageModel;
    import PageMode = api.content.page.PageMode;
    import PageModeChangedEvent = api.content.page.PageModeChangedEvent;
    import Site = api.content.site.Site;
    import Regions = api.content.page.region.Regions;
    import Component = api.content.page.region.Component;
    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.region.RegionPath;
    import ComponentPath = api.content.page.region.ComponentPath;

    export class PageViewBuilder {

        liveEditModel: LiveEditModel;

        itemViewProducer: ItemViewIdProducer;

        element: api.dom.Body;

        setLiveEditModel(value: LiveEditModel): PageViewBuilder {
            this.liveEditModel = value;
            return this;
        }

        setItemViewProducer(value: ItemViewIdProducer): PageViewBuilder {
            this.itemViewProducer = value;
            return this;
        }

        setElement(value: api.dom.Body): PageViewBuilder {
            this.element = value;
            return this;
        }

        build(): PageView {
            return new PageView(this);
        }
    }

    export class PageView extends ItemView {

        private pageModel: PageModel;

        private regionViews: RegionView[];

        private viewsById: {[s:number] : ItemView;};

        private ignorePropertyChanges: boolean;

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        private unlockedScreenActions: api.ui.Action[];

        private itemViewAddedListener: (event: ItemViewAddedEvent) => void;

        private itemViewRemovedListener: (event: ItemViewRemovedEvent) => void;

        public static debug;

        private propertyChangedListener: (event: api.PropertyChangedEvent) => void;

        private pageModeChangedListener: (event: PageModeChangedEvent) => void;

        private lockedContextMenu: api.liveedit.ItemViewContextMenu;

        private registerPageModel(pageModel: PageModel, resetAction: api.ui.Action) {
            if (PageView.debug) {
                console.log('PageView.registerPageModel', pageModel);
            }
            this.propertyChangedListener = (event: api.PropertyChangedEvent) => {
                // don't parse on regions change during reset, because it'll be done when page is loaded later
                if (event.getPropertyName() === PageModel.PROPERTY_REGIONS && !this.ignorePropertyChanges) {
                    this.parseItemViews();
                }
                this.refreshEmptyState();
            };
            pageModel.onPropertyChanged(this.propertyChangedListener);

            this.pageModeChangedListener = (event: PageModeChangedEvent) => {
                var resetEnabled = event.getNewMode() != PageMode.AUTOMATIC && event.getNewMode() != PageMode.NO_CONTROLLER;
                if (PageView.debug) {
                    console.log('PageView.pageModeChangedListener setting reset enabled', resetEnabled);
                }
                resetAction.setEnabled(resetEnabled);
            };
            pageModel.onPageModeChanged(this.pageModeChangedListener);
        }

        private unregisterPageModel(pageModel: PageModel) {
            if (PageView.debug) {
                console.log('PageView.unregisterPageModel', pageModel);
            }
            pageModel.unPropertyChanged(this.propertyChangedListener);
            pageModel.unPageModeChanged(this.pageModeChangedListener);
        }

        constructor(builder: PageViewBuilder) {
// move super() call here?!
            this.liveEditModel = builder.liveEditModel;
            this.pageModel = builder.liveEditModel.getPageModel();

            this.regionViews = [];
            this.viewsById = {};
            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];
            this.ignorePropertyChanges = false;
            PageView.debug = true;

            var resetAction = new api.ui.Action('Reset');
            resetAction.onExecuted(() => {
                if (PageView.debug) {
                    console.log('PageView.reset');
                }
                this.setIgnorePropertyChanges(true);
                this.pageModel.reset(this);
                this.setIgnorePropertyChanges(false);
            });
            this.unlockedScreenActions = [resetAction];

            if (this.pageModel.getMode() == PageMode.AUTOMATIC || this.pageModel.getMode() == PageMode.NO_CONTROLLER) {
                resetAction.setEnabled(false);
            }

            this.registerPageModel(this.pageModel, resetAction);

            this.itemViewAddedListener = (event: ItemViewAddedEvent) => {
                // register the view and all its child views (i.e layout with regions)
                var itemView = event.getView();
                itemView.toItemViewArray().forEach((itemView: ItemView) => {
                    this.registerItemView(itemView);
                });

                // adding anything except text should exit the text edit mode
                if (itemView.getType().equals(api.liveedit.text.TextItemType.get())) {
                    if (!this.isTextEditMode()) {
                        this.setTextEditMode(true);
                    }
                    itemView.giveFocus();
                } else {
                    if (this.isTextEditMode()) {
                        this.setTextEditMode(false);
                    }
                    itemView.select();
                }

            };
            this.itemViewRemovedListener = (event: ItemViewRemovedEvent) => {
                // register the view and all its child views (i.e layout with regions)
                event.getView().toItemViewArray().forEach((itemView: ItemView) => {
                    this.unregisterItemView(itemView);
                });
            };

            super(new ItemViewBuilder().
                setLiveEditModel(builder.liveEditModel).
                setItemViewIdProducer(builder.itemViewProducer).
                setPlaceholder(new PagePlaceholder(this)).
                setTooltipViewer(new api.content.ContentSummaryViewer()).
                setType(PageItemType.get()).
                setElement(builder.element).
                setParentElement(builder.element.getParentElement()).
                setContextMenuActions(this.unlockedScreenActions).
                setContextMenuTitle(new PageViewContextMenuTitle(builder.liveEditModel.getContent())));

            this.addClass('page-view');

            this.setTooltipObject(builder.liveEditModel.getContent());

            this.parseItemViews();

            this.appendChild(this.createTextModeToolbar());

            this.refreshEmptyState();

            // lock page by default for every content that has not been modified except for page template
            if (!this.liveEditModel.getContent().isPageTemplate() && !this.isPageModified(this.pageModel)) {
                this.setLocked(true);
            }

            this.listenToMouseEvents();

            this.onRemoved(event => this.unregisterPageModel(this.pageModel));
        }

        private setIgnorePropertyChanges(value: boolean) {
            this.ignorePropertyChanges = value;
        }

        private isPageModified(pageModel: PageModel): boolean {
            // default template regions differing from page regions means it has been modified
            return pageModel.getDefaultPageTemplate().isPage() &&
                   !pageModel.getDefaultPageTemplate().getRegions().equals(pageModel.getRegions());
        }

        showTooltip() {
            if (!this.isTextEditMode() && !this.isLocked()) {
                super.showTooltip();
            }
        }

        highlight() {
            var isDragging = DragAndDrop.get().isDragging();
            if (!this.isTextEditMode() && !this.isLocked() && !isDragging) {
                super.highlight();
            }
        }

        showCursor() {
            if (!this.isTextEditMode() && !this.isLocked()) {
                super.showCursor();
            }
        }

        shade() {
            if (!this.isEmpty()) {
                super.shade();
            }
        }

        unshade() {
            if (!this.isLocked()) {
                super.unshade();
            }
        }

        private listenToMouseEvents() {
            Shader.get().onUnlockClicked((event: MouseEvent) => {
                if (this.isLocked()) {
                    this.setLocked(false);
                }
            })

            this.onMouseOverView(() => {
                var isDragging = DragAndDrop.get().isDragging();
                if (isDragging && this.lockedContextMenu) {
                    if (this.lockedContextMenu.isVisible()) {
                        this.lockedContextMenu.hide();
                    }
                }
            });
        }

        select(clickPosition?: Position, menuPosition?: ItemViewContextMenuPosition) {
            super.select(clickPosition, menuPosition);

            new PageSelectedEvent(this).fire();
        }

        showContextMenu(clickPosition?: Position, menuPosition?: ItemViewContextMenuPosition) {
            if (!this.isLocked()) {
                super.showContextMenu(clickPosition, menuPosition);
            }
        }

        createLockedContextMenu() {
            return new api.liveedit.ItemViewContextMenu(this.getContextMenuTitle(), this.getLockedMenuActions());
        }

        getLockedMenuActions(): api.ui.Action[] {
            var unlockAction = new api.ui.Action("Customize");

            unlockAction.onExecuted(() => {
                this.setLocked(false);
            });

            return [unlockAction];
        }

        selectLocked(pos: Position) {
            this.setLockVisible(true);
            this.lockedContextMenu.showAt(pos.x, pos.y);

            new ItemViewSelectedEvent(this, pos).fire();
            new PageSelectedEvent(this).fire();
        }

        deselectLocked() {
            this.setLockVisible(false);
            this.lockedContextMenu.hide();

            new ItemViewDeselectEvent(this).fire();
        }

        handleShaderClick(event: MouseEvent) {
            if (this.isLocked()) {
                if (!this.lockedContextMenu) {
                    this.lockedContextMenu = this.createLockedContextMenu();
                }
                if (this.lockedContextMenu.isVisible()) {
                    this.deselectLocked();
                }
                else {
                    this.selectLocked({x: event.pageX, y: event.pageY});
                }
            }
            else if (this.isSelected()) {
                this.deselect();
            }
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();

            if (this.isTextEditMode()) {
                this.setTextEditMode(false);
            } else {
                super.handleClick(event);
            }
        }

        hideContextMenu() {
            if (this.lockedContextMenu) {
                this.lockedContextMenu.hide();
            }
            return super.hideContextMenu();
        }

        isLocked() {
            return this.hasClass('locked');
        }

        setLockVisible(visible: boolean) {
            this.toggleClass('force-locked', visible);
        }

        setLocked(locked: boolean) {
            this.toggleClass('locked', locked);

            this.hideContextMenu();

            if (locked) {
                this.shade();

                new PageLockedEvent(this).fire();
            } else {
                this.unshade();

                if (!this.pageModel.isPageTemplate() || this.pageModel.getMode() == PageMode.AUTOMATIC) {
                    this.pageModel.initializePageFromDefault(this);
                }

                new PageUnlockedEvent(this).fire();
            }
        }

        private createTextModeToolbar() {
            var toolbar = new api.dom.DivEl('text-edit-toolbar');
            var wrapper = new api.dom.DivEl('wrapper');
            wrapper.setHtml('Text Edit Mode');
            var closeButton = new api.ui.button.CloseButton('no-bg');
            closeButton.onClicked((event: MouseEvent) => {
                event.stopPropagation();
                event.preventDefault();

                this.setTextEditMode(false);
            });
            wrapper.appendChild(closeButton);
            toolbar.appendChild(wrapper);
            return toolbar;
        }


        isTextEditMode(): boolean {
            return this.hasClass('text-edit-mode');
        }

        setTextEditMode(flag: boolean) {
            this.toggleClass('text-edit-mode', flag);

            var textItemViews = this.getItemViewsByType(api.liveedit.text.TextItemType.get());

            var textView: api.liveedit.text.TextComponentView;
            textItemViews.forEach((view: ItemView) => {
                textView = <api.liveedit.text.TextComponentView> view;
                if (textView.isEditMode() != flag) {
                    textView.setEditMode(flag);
                }
            });

            if (flag) {
                new PageTextModeStartedEvent(this).fire();
            }
        }

        hasTargetWithinTextComponent(target: HTMLElement) {
            var textItemViews = this.getItemViewsByType(api.liveedit.text.TextItemType.get());
            var result: boolean = false;

            var textView: api.liveedit.text.TextComponentView;
            textItemViews.forEach((view: ItemView) => {
                textView = <api.liveedit.text.TextComponentView> view;
                if (textView.getEl().contains(target)) {
                    result = true;
                    return;
                }
            });

            return result;
        }

        isEmpty(): boolean {
            return !this.pageModel || this.pageModel.getMode() == PageMode.NO_CONTROLLER;
        }

        getName(): string {
            return this.liveEditModel.getContent() ? this.liveEditModel.getContent().getDisplayName() : "[No name]";
        }

        getParentItemView(): ItemView {
            return null;
        }

        setParentItemView(itemView: ItemView) {
            throw new Error("PageView is the topmost item view and cannot have a parent");
        }

        private registerRegionView(regionView: RegionView) {
            this.regionViews.push(regionView);

            regionView.onItemViewAdded(this.itemViewAddedListener);
            regionView.onItemViewRemoved(this.itemViewRemovedListener);
        }

        unregisterRegionView(regionView: RegionView) {
            var index = this.regionViews.indexOf(regionView);
            if (index > -1) {
                this.regionViews.splice(index, 1);

                regionView.unItemViewAdded(this.itemViewAddedListener);
                regionView.unItemViewRemoved(this.itemViewRemovedListener);
            }
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.regionViews.forEach((regionView: RegionView) => {
                var itemViews = regionView.toItemViewArray();
                array = array.concat(itemViews);
            });
            return array;
        }

        hasSelectedView(): boolean {
            return !!this.getSelectedView();
        }

        getSelectedView(): ItemView {
            for (var id in this.viewsById) {
                if (this.viewsById.hasOwnProperty(id) && this.viewsById[id].isSelected()) {
                    return this.viewsById[id];
                }
            }
            return null;
        }

        getItemViewById(id: ItemViewId): ItemView {
            api.util.assertNotNull(id, "value cannot be null");
            return this.viewsById[id.toNumber()];
        }

        getItemViewsByType(type: ItemType): ItemView[] {
            var views: ItemView[] = [];
            for (var key in this.viewsById) {
                if (this.viewsById.hasOwnProperty(key)) {
                    var view = this.viewsById[key];
                    if (type.equals(view.getType())) {
                        views.push(view);
                    }
                }
            }
            return views;
        }

        getItemViewByElement(element: HTMLElement): ItemView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            var itemView = this.getItemViewById(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());

            return itemView;
        }

        getRegionViewByElement(element: HTMLElement): RegionView {

            var itemView = this.getItemViewByElement(element);

            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, RegionView)) {
                return <RegionView>itemView;
            }
            return null;
        }

        getComponentViewByElement(element: HTMLElement): ComponentView<Component> {

            var itemView = this.getItemViewByElement(element);

            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                return <ComponentView<Component>> itemView;
            }

            return null;
        }

        getRegionViewByPath(path: RegionPath): RegionView {

            for (var i = 0; i < this.regionViews.length; i++) {
                var regionView = this.regionViews[i];

                if (path.hasParentComponentPath()) {
                    var componentView = this.getComponentViewByPath(path.getParentComponentPath());
                    if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, api.liveedit.layout.LayoutComponentView)) {
                        var layoutView = <api.liveedit.layout.LayoutComponentView>componentView;
                        layoutView.getRegionViewByName(path.getRegionName());
                    }
                }
                else {
                    if (path.getRegionName() == regionView.getRegionName()) {
                        return regionView;
                    }
                }
            }

            return null;
        }

        getComponentViewByPath(path: ComponentPath): ComponentView<Component> {

            var firstLevelOfPath = path.getFirstLevel();

            for (var i = 0; i < this.regionViews.length; i++) {
                var regionView = this.regionViews[i];
                if (firstLevelOfPath.getRegionName() == regionView.getRegionName()) {
                    if (path.numberOfLevels() == 1) {
                        return regionView.getComponentViewByIndex(firstLevelOfPath.getComponentIndex());
                    }
                    else {
                        var layoutView: api.liveedit.layout.LayoutComponentView = <api.liveedit.layout.LayoutComponentView>regionView.getComponentViewByIndex(firstLevelOfPath.getComponentIndex());
                        return layoutView.getComponentViewByPath(path.removeFirstLevel());
                    }
                }
            }

            return null;
        }


        private registerItemView(view: ItemView) {

            if (PageView.debug) {
                console.debug("PageView.registerItemView: " + view.toString());
            }

            this.viewsById[view.getItemId().toNumber()] = view;

            this.notifyItemViewAdded(view);
        }

        private unregisterItemView(view: ItemView) {

            if (PageView.debug) {
                console.debug("PageView.unregisterItemView: " + view.toString());
            }

            delete this.viewsById[view.getItemId().toNumber()];

            this.notifyItemViewRemoved(view);
        }

        private parseItemViews() {
            // unregister existing views
            for (var itemView in this.viewsById) {
                if (this.viewsById.hasOwnProperty(itemView)) {
                    this.unregisterItemView(this.viewsById[itemView]);
                }
            }

            // unregister existing regions
            this.regionViews.forEach((regionView: RegionView)=> {
                this.unregisterRegionView(regionView)
            });

            this.regionViews = [];
            this.viewsById = {};

            this.doParseItemViews();

            // register everything that was parsed
            this.toItemViewArray().forEach((itemView: ItemView) => {
                this.registerItemView(itemView);
            });
        }

        deselectChildViews() {
            for (var itemView in this.viewsById) {
                var view = this.viewsById[itemView];
                if (api.ObjectHelper.iFrameSafeInstanceOf(view, ItemView) && view.isSelected()) {
                    view.deselect();
                }
            }
        }

        private doParseItemViews(parentElement?: api.dom.Element) {

            var pageRegions = this.liveEditModel.getPageModel().getRegions();
            if (!pageRegions) {
                return;
            }
            var children = parentElement ? parentElement.getChildren() : this.getChildren();

            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                var isRegionView = api.ObjectHelper.iFrameSafeInstanceOf(childElement, RegionView);
                var region, regionName, regionView;

                if (isRegionView) {
                    regionName = RegionItemType.getRegionName(childElement);
                    region = pageRegions.getRegionByName(regionName);
                    if (region) {
                        // reuse existing region view
                        regionView = <RegionView> childElement;
                        // update view's data
                        regionView.setRegion(region);
                        // register it again because we unregistered everything before parsing
                        this.registerRegionView(regionView);
                    }

                } else if (itemType && RegionItemType.get().equals(itemType)) {
                    regionName = RegionItemType.getRegionName(childElement);
                    region = pageRegions.getRegionByName(regionName);

                    if (region) {
                        regionView = new RegionView(new RegionViewBuilder().
                            setLiveEditModel(this.liveEditModel).
                            setParentView(this).
                            setRegion(region).
                            setElement(childElement));

                        this.registerRegionView(regionView);
                    }

                } else {
                    this.doParseItemViews(childElement);
                }
            });
        }

        onItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners.push(listener);
        }

        unItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners = this.itemViewAddedListeners.filter((current) => (current != listener));
        }

        private notifyItemViewAdded(itemView: ItemView) {
            var event = new ItemViewAddedEvent(itemView);
            this.itemViewAddedListeners.forEach((listener) => listener(event));
        }

        onItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners.push(listener);
        }

        unItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners = this.itemViewRemovedListeners.filter((current) => (current != listener));
        }

        private notifyItemViewRemoved(itemView: ItemView) {
            var event = new ItemViewRemovedEvent(itemView);
            this.itemViewRemovedListeners.forEach((listener) => listener(event));
        }
    }
}