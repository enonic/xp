module api.liveedit {

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
    import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import SetController = api.content.page.SetController;
    import i18n = api.util.i18n;
    import CreatePageTemplateRequest = api.content.page.CreatePageTemplateRequest;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import EditContentEvent = api.content.event.EditContentEvent;

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

        private fragmentView: ComponentView<Component>;

        private viewsById: { [s: number]: ItemView; };

        private ignorePropertyChanges: boolean;

        private itemViewAddedListeners: { (event: ItemViewAddedEvent): void }[];

        private itemViewRemovedListeners: { (event: ItemViewRemovedEvent): void }[];

        private pageLockedListeners: { (locked: boolean): void }[];

        private resetAction: api.ui.Action;

        private itemViewAddedListener: (event: ItemViewAddedEvent) => void;

        private itemViewRemovedListener: (event: ItemViewRemovedEvent) => void;

        private scrolledListener: (event: WheelEvent) => void;

        public static debug: boolean;

        private propertyChangedListener: (event: api.PropertyChangedEvent) => void;

        private pageModeChangedListener: (event: PageModeChangedEvent) => void;

        private customizeChangedListener: (value: boolean) => void;

        private lockedContextMenu: api.liveedit.ItemViewContextMenu;

        private disableContextMenu: boolean;

        private closeTextEditModeButton: api.dom.Element;

        private editorToolbar: api.dom.DivEl;

        private highlightingAllowed: boolean;

        private nextClickDisabled: boolean;

        constructor(builder: PageViewBuilder) {

            super(new ItemViewBuilder()
                .setLiveEditModel(builder.liveEditModel)
                .setItemViewIdProducer(builder.itemViewProducer)
                .setViewer(new api.content.ContentSummaryViewer())
                .setType(PageItemType.get())
                .setElement(builder.element)
                .setContextMenuTitle(new PageViewContextMenuTitle(builder.liveEditModel.getContent())));

            this.setPlaceholder(new PagePlaceholder(this));

            this.addPageContextMenuActions();

            this.pageModel = builder.liveEditModel.getPageModel();

            this.registerPageModel();

            this.regionViews = [];
            this.viewsById = {};
            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];
            this.pageLockedListeners = [];
            this.ignorePropertyChanges = false;
            this.disableContextMenu = false;
            this.highlightingAllowed = true;
            this.nextClickDisabled = false;

            this.addClassEx('page-view');

            this.initListeners();

            this.parseItemViews();

            this.closeTextEditModeButton = this.createCloseTextEditModeEl();

            this.appendChild(this.closeTextEditModeButton);

            this.refreshEmptyState();

            // lock page by default for every content that has not been modified except for page template
            let isCustomized = this.liveEditModel.getPageModel().isCustomized();
            let isFragment = !!this.fragmentView;
            if (!this.pageModel.isPageTemplate() && !isCustomized && !isFragment) {
                this.setLocked(true);
            }

        }

        private registerPageModel() {
            if (PageView.debug) {
                console.log('PageView.registerPageModel', this.pageModel);
            }
            this.propertyChangedListener = (event: api.PropertyChangedEvent) => {
                // don't parse on regions change during reset, because it'll be done when page is loaded later
                if (event.getPropertyName() === PageModel.PROPERTY_REGIONS && !this.ignorePropertyChanges) {
                    this.parseItemViews();
                }
                this.refreshEmptyState();
            };
            this.pageModel.onPropertyChanged(this.propertyChangedListener);

            this.pageModeChangedListener = (event: PageModeChangedEvent) => {
                let resetEnabled = event.getNewMode() !== PageMode.AUTOMATIC && event.getNewMode() !== PageMode.NO_CONTROLLER;
                if (PageView.debug) {
                    console.log('PageView.pageModeChangedListener setting reset enabled', resetEnabled);
                }
                this.resetAction.setEnabled(resetEnabled);
            };
            this.pageModel.onPageModeChanged(this.pageModeChangedListener);

            this.customizeChangedListener = ((value) => {
                if (this.isLocked() && value) {
                    this.setLocked(false);
                }
            });
            this.pageModel.onCustomizeChanged(this.customizeChangedListener);
        }

        private unregisterPageModel(pageModel: PageModel) {
            if (PageView.debug) {
                console.log('PageView.unregisterPageModel', pageModel);
            }
            pageModel.unPropertyChanged(this.propertyChangedListener);
            pageModel.unPageModeChanged(this.pageModeChangedListener);
            pageModel.unCustomizeChanged(this.customizeChangedListener);
        }

        private addPageContextMenuActions() {
            let pageModel = this.liveEditModel.getPageModel();
            let inspectAction = new api.ui.Action(i18n('live.view.inspect')).onExecuted(() => {
                new PageInspectedEvent().fire();
            });

            this.resetAction = new api.ui.Action(i18n('live.view.reset')).onExecuted(() => {
                if (PageView.debug) {
                    console.log('PageView.reset');
                }
                this.setIgnorePropertyChanges(true);
                pageModel.reset(this);
                this.setIgnorePropertyChanges(false);
            });

            if (pageModel.getMode() === PageMode.AUTOMATIC || pageModel.getMode() === PageMode.NO_CONTROLLER) {
                this.resetAction.setEnabled(false);
            }

            this.addContextMenuActions([inspectAction, this.resetAction]);
        }

        private initListeners() {

            this.scrolledListener = (event: WheelEvent) => {
                this.toggleStickyToolbar();
            };

            this.itemViewAddedListener = (event: ItemViewAddedEvent) => {
                // register the view and all its child views (i.e layout with regions)
                let itemView = event.getView();
                itemView.toItemViewArray().forEach((value: ItemView) => {
                    this.registerItemView(value);
                });

                // adding anything except text should exit the text edit mode
                if (itemView.getType().equals(api.liveedit.text.TextItemType.get())) {
                    if (!this.isTextEditMode()) {
                        this.setTextEditMode(true);
                    } else {
                        (<api.liveedit.text.TextComponentView>itemView).setEditMode(true);
                        this.closeTextEditModeButton.toggleClass('active', true);
                    }
                    new ItemViewSelectedEvent(itemView, null, event.isNew(), true).fire();
                    itemView.giveFocus();
                } else {
                    if (this.isTextEditMode()) {
                        this.setTextEditMode(false);
                    }
                    itemView.select(null, ItemViewContextMenuPosition.NONE, event.isNew());
                }
            };
            this.itemViewRemovedListener = (event: ItemViewRemovedEvent) => {
                // register the view and all its child views (i.e layout with regions)
                event.getView().toItemViewArray().forEach((itemView: ItemView) => {
                    this.unregisterItemView(itemView);
                });
            };

            this.listenToMouseEvents();

            this.onRemoved(event => this.unregisterPageModel(this.pageModel));

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (this.isTextEditMode()) {
                    this.updateVerticalSpaceForEditorToolbar();
                }
            });
        }

        private createCloseTextEditModeEl(): api.dom.Element {
            let closeButton = new api.dom.AEl('close-edit-mode-button icon-close2');
            closeButton.onClicked((event: MouseEvent) => {
                this.setTextEditMode(false);
                event.stopPropagation();
                return false;
            });
            return closeButton;
        }

        private isPageScrolled() {
            return this.getEl().getScrollTop() > 0 || this.getEl().getParent().getScrollTop() > 0;
        }

        private toggleStickyToolbar() {
            if (!this.isPageScrolled()) {
                this.editorToolbar.removeClass('sticky-toolbar');
            } else if (!this.editorToolbar.hasClass('sticky-toolbar')) {
                this.editorToolbar.addClass('sticky-toolbar');
            }
        }

        appendContainerForTextToolbar() {
            if (!this.hasToolbarContainer()) {
                this.editorToolbar = new api.dom.DivEl('cke-toolbar-container').setId('cke-toolbar-container').setContentEditable(true);
                this.appendChild(this.editorToolbar);
                this.addClass('has-toolbar-container');
            }
        }

        hasToolbarContainer(): boolean {
            return this.hasClass('has-toolbar-container');
        }

        getEditorToolbarContainerId(): string {
            if (this.editorToolbar) {
                return this.editorToolbar.getId();
            }

            return null;
        }

        getEditorToolbarHeight(): number {
            if (!this.editorToolbar) {
                return 0;
            }

            if (!this.editorToolbar.hasClass('visisble')) {
                return 0;
            }

            return this.editorToolbar.getEl().getHeightWithMargin();
        }

        private setIgnorePropertyChanges(value: boolean) {
            this.ignorePropertyChanges = value;
        }

        /*
         highlight() {
         // Don't highlight page
         }

         unhighlight() {
         // Don't highlight page on hover
         }
         */
        highlightSelected() {
            let isDragging = DragAndDrop.get().isDragging();
            if (!this.isTextEditMode() && !this.isLocked() && !isDragging) {
                super.highlightSelected();
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
            });

            this.onMouseOverView(() => {
                let isDragging = DragAndDrop.get().isDragging();
                if (isDragging && this.lockedContextMenu) {
                    if (this.lockedContextMenu.isVisible()) {
                        this.lockedContextMenu.hide();
                    }
                }
            });
        }

        select(clickPosition?: Position, menuPosition?: ItemViewContextMenuPosition, isNew: boolean = false,
               rightClicked: boolean = false) {
            super.select(clickPosition, menuPosition, false, rightClicked);

            new PageSelectedEvent(this).fire();
        }

        selectWithoutMenu(isNew: boolean = false) {
            super.selectWithoutMenu(isNew);

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
            let unlockAction = new api.ui.Action(i18n('live.view.page.customize'));

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

            new ItemViewDeselectedEvent(this).fire();
        }

        handleShaderClick(event: MouseEvent) {
            if (this.isLocked()) {
                if (!this.lockedContextMenu) {
                    this.lockedContextMenu = this.createLockedContextMenu();
                }
                if (this.lockedContextMenu.isVisible()) {
                    this.deselectLocked();
                } else {
                    this.selectLocked({x: event.pageX, y: event.pageY});
                }
            } else if (!this.isSelected() || event.which === 3) {
                this.handleClick(event);
            } else {
                this.deselect();
            }
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();

            if (this.isTextEditMode()) {
                if (!this.isTextEditorToolbarClicked(event) && !this.isTextEditorDialogClicked(event)) {
                    this.setTextEditMode(false);
                }
            } else {
                super.handleClick(event);
            }
        }

        private isTextEditorToolbarClicked(event: MouseEvent) {
            let target = <HTMLElement> event.target;
            if (!!target) {
                let parent = <HTMLElement> target.parentElement;
                return (target.id.indexOf('cke') >= 0 || target.className.indexOf('cke') >= 0 ||
                        parent.id.indexOf('cke') >= 0 || parent.className.indexOf('cke') >= 0);
            }
            return false;
        }

        private isTextEditorDialogClicked(event: MouseEvent) {
            let target = <HTMLElement> event.target;
            while (target) {
                if (target.classList.contains(api.util.htmlarea.dialog.ModalDialog.CLASS_NAME)) {
                    return true;
                }
                target = target.parentElement;
            }
            return false;
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

                if (!(this.pageModel.isPageTemplate() || this.pageModel.isCustomized())) {
                    this.pageModel.setCustomized(true);
                    this.pageModel.setTemplateContoller();
                }

                new PageUnlockedEvent(this).fire();
            }

            this.notifyPageLockChanged(locked);
        }

        isTextEditMode(): boolean {
            return this.hasClass('text-edit-mode');
        }

        setTextEditMode(flag: boolean) {
            this.setHighlightingAllowed(!flag);
            this.toggleClass('text-edit-mode', flag);

            let textItemViews = this.getItemViewsByType(api.liveedit.text.TextItemType.get());

            let textView: api.liveedit.text.TextComponentView;
            textItemViews.forEach((view: ItemView) => {
                textView = <api.liveedit.text.TextComponentView> view;
                if (textView.isEditMode() !== flag) {
                    textView.setEditMode(flag);
                    this.closeTextEditModeButton.toggleClass('active', flag);
                }
            });

            if (this.editorToolbar) {
                this.editorToolbar.toggleClass('visible', flag);
            }

            if (flag) {
                this.addVerticalSpaceForEditorToolbar();

                this.onScrolled(this.scrolledListener);
                new PageTextModeStartedEvent(this).fire();
            } else {
                this.removeVerticalSpaceForEditorToolbar();
                this.unScrolled(this.scrolledListener);

                api.liveedit.Highlighter.get().updateLastHighlightedItemView();
                api.liveedit.SelectedHighlighter.get().updateLastHighlightedItemView();
            }
        }

        private addVerticalSpaceForEditorToolbar() {
            this.getPageView().getEl().setPosition('relative');
            this.updateVerticalSpaceForEditorToolbar();
            this.toggleStickyToolbar();
        }

        getPageView(): PageView {
            return this;
        }

        private updateVerticalSpaceForEditorToolbar() {
            let result = this.getEditorToolbarWidth();

            if (!!result) {
                this.getPageView().getEl().setTop(this.getEditorToolbarWidth() + 'px');
            } else {
                this.waitUntilEditorToolbarShown();
            }

        }

        private waitUntilEditorToolbarShown() {
            let intervalId;
            let toolbarHeight;
            let attempts = 0;

            intervalId = setInterval(() => {
                attempts++;
                toolbarHeight = this.getEditorToolbarWidth();
                if (!!toolbarHeight) {
                    this.getPageView().getEl().setTop(toolbarHeight + 'px');
                    clearInterval(intervalId);
                } else if (attempts > 10) {
                    clearInterval(intervalId);
                }
            }, 50);

        }

        private removeVerticalSpaceForEditorToolbar() {
            this.getEl().setPosition('');
            this.getEl().setTop('');
        }

        private getEditorToolbarWidth(): number {
            return wemjq(`.cke-toolbar-container .cke_reset_all:not([style*='display: none']) .cke_top`).outerHeight();
        }

        hasTargetWithinTextComponent(target: HTMLElement) {
            let textItemViews = this.getItemViewsByType(api.liveedit.text.TextItemType.get());
            let result: boolean = false;

            let textView: api.liveedit.text.TextComponentView;
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
            return !this.pageModel || this.pageModel.getMode() === PageMode.NO_CONTROLLER;
        }

        private isContentEmpty() {
            let content = this.liveEditModel.getContent();
            return (!content || api.util.StringHelper.isEmpty(content.getDisplayName()));
        }

        getName(): string {
            let pageTemplateDisplayName = api.content.page.PageTemplateDisplayName;
            if (this.pageModel.hasTemplate()) {
                return this.pageModel.getTemplate().getDisplayName();
            }
            if (this.pageModel.isPageTemplate() && this.pageModel.getController()) {
                return this.pageModel.getController().getDisplayName();
            }
            if (this.pageModel.isCustomized()) {
                return this.pageModel.hasController()
                    ? this.pageModel.getController().getDisplayName()
                    : pageTemplateDisplayName[pageTemplateDisplayName.Custom];
            }
            if (this.pageModel.getMode() === PageMode.AUTOMATIC) {
                return this.pageModel.getDefaultPageTemplate().getDisplayName();
            }

            return pageTemplateDisplayName[pageTemplateDisplayName.Automatic];
        }

        getIconUrl(content: api.content.Content): string {
            return '';
        }

        getIconClass(): string {
            let largeIconCls = ' icon-large';

            if (this.pageModel.hasTemplate()) {
                return 'icon-newspaper' + largeIconCls;
            }
            if (this.pageModel.isPageTemplate() && this.pageModel.getController()) {
                return 'icon-file' + largeIconCls;
            }
            if (this.pageModel.isCustomized()) {
                return 'icon-cog' + largeIconCls;
            }

            return 'icon-wand' + largeIconCls;
        }

        getParentItemView(): ItemView {
            return null;
        }

        setParentItemView(itemView: ItemView) {
            throw new Error(i18n('live.view.page.error.noparent'));
        }

        private registerRegionView(regionView: RegionView) {
            this.regionViews.push(regionView);

            regionView.onItemViewAdded(this.itemViewAddedListener);
            regionView.onItemViewRemoved(this.itemViewRemovedListener);
        }

        unregisterRegionView(regionView: RegionView) {
            let index = this.regionViews.indexOf(regionView);
            if (index > -1) {
                this.regionViews.splice(index, 1);

                regionView.unItemViewAdded(this.itemViewAddedListener);
                regionView.unItemViewRemoved(this.itemViewRemovedListener);
            }
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }

        getModel(): PageModel {
            return this.pageModel;
        }

        getFragmentView(): ComponentView<Component> {
            return this.fragmentView;
        }

        toItemViewArray(): ItemView[] {

            let array: ItemView[] = [];
            array.push(this);
            this.regionViews.forEach((regionView: RegionView) => {
                let itemViews = regionView.toItemViewArray();
                array = array.concat(itemViews);
            });
            return array;
        }

        hasSelectedView(): boolean {
            return !!this.getSelectedView();
        }

        getSelectedView(): ItemView {
            for (let id in this.viewsById) {
                if (this.viewsById.hasOwnProperty(id) && this.viewsById[id].isSelected()) {
                    return this.viewsById[id];
                }
            }
            return null;
        }

        getItemViewById(id: ItemViewId): ItemView {
            api.util.assertNotNull(id, i18n('live.view.itemview.error.idisnull'));
            return this.viewsById[id.toNumber()];
        }

        getItemViewsByType(type: ItemType): ItemView[] {
            let views: ItemView[] = [];
            for (let key in this.viewsById) {
                if (this.viewsById.hasOwnProperty(key)) {
                    let view = this.viewsById[key];
                    if (type.equals(view.getType())) {
                        views.push(view);
                    }
                }
            }
            return views;
        }

        getItemViewByElement(element: HTMLElement): ItemView {
            api.util.assertNotNull(element, i18n('live.view.itemview.error.elementisnull'));

            let itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            let itemView = this.getItemViewById(itemId);
            api.util.assertNotNull(itemView, i18n('live.view.itemview.error.notfound', itemId.toString()));

            return itemView;
        }

        getRegionViewByElement(element: HTMLElement): RegionView {

            let itemView = this.getItemViewByElement(element);

            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, RegionView)) {
                return <RegionView>itemView;
            }
            return null;
        }

        getComponentViewByElement(element: HTMLElement): ComponentView<Component> {

            let itemView = this.getItemViewByElement(element);

            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                return <ComponentView<Component>> itemView;
            }

            return null;
        }

        getRegionViewByPath(path: RegionPath): RegionView {

            for (let i = 0; i < this.regionViews.length; i++) {
                let regionView = this.regionViews[i];

                if (path.hasParentComponentPath()) {
                    let componentView = this.getComponentViewByPath(path.getParentComponentPath());
                    if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, api.liveedit.layout.LayoutComponentView)) {
                        let layoutView = <api.liveedit.layout.LayoutComponentView>componentView;
                        layoutView.getRegionViewByName(path.getRegionName());
                    }
                } else {
                    if (path.getRegionName() === regionView.getRegionName()) {
                        return regionView;
                    }
                }
            }

            return null;
        }

        getComponentViewByPath(path: ComponentPath): ComponentView<Component> {
            if (!path) {
                return this.fragmentView;
            }

            let firstLevelOfPath = path.getFirstLevel();

            for (let i = 0; i < this.regionViews.length; i++) {
                let regionView = this.regionViews[i];
                if (firstLevelOfPath.getRegionName() === regionView.getRegionName()) {
                    if (path.numberOfLevels() === 1) {
                        return regionView.getComponentViewByIndex(firstLevelOfPath.getComponentIndex());
                    } else {
                        const view = regionView.getComponentViewByIndex(firstLevelOfPath.getComponentIndex());
                        const layoutView: api.liveedit.layout.LayoutComponentView = <api.liveedit.layout.LayoutComponentView>view;
                        return layoutView.getComponentViewByPath(path.removeFirstLevel());
                    }
                }
            }

            return null;
        }

        private registerItemView(view: ItemView) {

            if (PageView.debug) {
                console.debug('PageView.registerItemView: ' + view.toString());
            }

            this.viewsById[view.getItemId().toNumber()] = view;

            this.notifyItemViewAdded(view);
        }

        private unregisterItemView(view: ItemView) {

            if (PageView.debug) {
                console.debug('PageView.unregisterItemView: ' + view.toString());
            }

            delete this.viewsById[view.getItemId().toNumber()];

            this.notifyItemViewRemoved(view);
        }

        private parseItemViews() {
            // unregister existing views
            for (let itemView in this.viewsById) {
                if (this.viewsById.hasOwnProperty(itemView)) {
                    this.unregisterItemView(this.viewsById[itemView]);
                }
            }

            // unregister existing regions
            this.regionViews.forEach((regionView: RegionView) => {
                this.unregisterRegionView(regionView);
            });

            this.regionViews = [];
            this.viewsById = {};

            if (this.liveEditModel.getPageModel().getMode() === PageMode.FRAGMENT) {
                this.doParseFragmentItemViews();
            } else {
                this.doParseItemViews();
                // register everything that was parsed
                this.toItemViewArray().forEach((value: ItemView) => {
                    this.registerItemView(value);
                });
            }

        }

        private doParseItemViews(parentElement?: api.dom.Element) {

            let pageRegions = this.liveEditModel.getPageModel().getRegions();
            if (!pageRegions) {
                return;
            }
            let children = parentElement ? parentElement.getChildren() : this.getChildren();

            children.forEach((childElement: api.dom.Element) => {
                let itemType = ItemType.fromElement(childElement);
                let isRegionView = api.ObjectHelper.iFrameSafeInstanceOf(childElement, RegionView);
                let region;
                let regionName;
                let regionView;

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
                        regionView =
                            new RegionView(new RegionViewBuilder()
                                .setLiveEditModel(this.liveEditModel)
                                .setParentView(this).setRegion(region)
                                .setElement(childElement));

                        this.registerRegionView(regionView);
                    }

                } else {
                    this.doParseItemViews(childElement);
                }
            });
        }

        private doParseFragmentItemViews(parentElement?: api.dom.Element) {

            let fragment = this.liveEditModel.getPageModel().getPage().getFragment();
            if (!fragment) {
                return;
            }
            let children = parentElement ? parentElement.getChildren() : this.getChildren();

            children.forEach((childElement: api.dom.Element) => {
                let itemType = ItemType.fromElement(childElement);
                let component: Component = this.pageModel.getPage().getFragment();
                let componentView: ComponentView<Component>;

                if (itemType && itemType.isComponentType()) {
                    if (component) {
                        const itemViewConfig = new CreateItemViewConfig<PageView, Component>()
                            .setParentView(this)
                            .setData(component)
                            .setElement(childElement)
                            .setParentElement(parentElement ? parentElement : this);
                        componentView = <ComponentView<Component>> itemType.createView(itemViewConfig);

                        this.registerFragmentComponentView(componentView);
                    }
                } else {
                    this.doParseFragmentItemViews(childElement);
                }
            });
        }

        unregisterFragmentComponentView(componentView: ComponentView<Component>) {
            componentView.unItemViewAdded(this.itemViewAddedListener);
            componentView.unItemViewRemoved(this.itemViewRemovedListener);

            componentView.toItemViewArray().forEach((itemView: ItemView) => {
                this.unregisterItemView(itemView);
            });
        }

        registerFragmentComponentView(componentView: ComponentView<Component>) {
            componentView.onItemViewAdded(this.itemViewAddedListener);
            componentView.onItemViewRemoved(this.itemViewRemovedListener);

            componentView.toItemViewArray().forEach((value: ItemView) => {
                this.registerItemView(value);
            });

            if (componentView instanceof api.liveedit.layout.LayoutComponentView) {
                componentView.getRegions().forEach((regionView) => {
                    this.registerRegionView(regionView);
                });
            }

            this.fragmentView = componentView;
        }

        onItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners.push(listener);
        }

        unItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners = this.itemViewAddedListeners.filter((current) => (current !== listener));
        }

        private notifyItemViewAdded(itemView: ItemView) {
            let event = new ItemViewAddedEvent(itemView);
            this.itemViewAddedListeners.forEach((listener) => listener(event));
        }

        onItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners.push(listener);
        }

        unItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners = this.itemViewRemovedListeners.filter((current) => (current !== listener));
        }

        private notifyItemViewRemoved(itemView: ItemView) {
            let event = new ItemViewRemovedEvent(itemView);
            this.itemViewRemovedListeners.forEach((listener) => listener(event));
        }

        onPageLocked(listener: (event: any) => void) {
            this.pageLockedListeners.push(listener);
        }

        unPageLocked(listener: (event: any) => void) {
            this.pageLockedListeners = this.pageLockedListeners.filter((current) => (current !== listener));
        }

        private notifyPageLockChanged(locked: boolean) {
            this.pageLockedListeners.forEach((listener) => {
                listener(locked);
            });
        }

        setDisabledContextMenu(value: boolean) {
            this.disableContextMenu = value;
        }

        isDisabledContextMenu(): boolean {
            return this.disableContextMenu;
        }

        setHighlightingAllowed(value: boolean) {
            this.highlightingAllowed = value;
        }

        setNextClickDisabled(value: boolean) {
            this.nextClickDisabled = value;
        }

        isHighlightingAllowed(): boolean {
            return this.highlightingAllowed;
        }

        isNextClickDisabled(): boolean {
            return this.nextClickDisabled;
        }
    }
}
