module api.liveedit.fragment {

    import ComponentView = api.liveedit.ComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import FragmentComponent = api.content.page.region.FragmentComponent;
    import GetContentByIdRequest = api.content.resource.GetContentByIdRequest;
    import Content = api.content.Content;
    import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
    import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
    import HTMLAreaHelper = api.util.htmlarea.editor.HTMLAreaHelper;

    export class FragmentComponentViewBuilder extends ComponentViewBuilder<FragmentComponent> {

        constructor() {
            super();
            this.setType(FragmentItemType.get());
        }
    }

    export class FragmentComponentView extends ComponentView<FragmentComponent> {

        private fragmentComponent: FragmentComponent;

        private fragmentContainsLayout: boolean;

        private fragmentContent: Content;

        private fragmentContentLoadedListeners: {(event: api.liveedit.FragmentComponentLoadedEvent): void}[];

        private fragmentLoadErrorListeners: {(event: api.liveedit.FragmentLoadErrorEvent): void}[];

        constructor(builder: FragmentComponentViewBuilder) {
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.fragmentComponent = builder.component;
            this.fragmentContainsLayout = false;
            this.fragmentContent = null;
            this.fragmentContentLoadedListeners = [];
            this.fragmentLoadErrorListeners = [];

            super(builder.setPlaceholder(new FragmentPlaceholder(this)).setViewer(
                new FragmentComponentViewer()).setInspectActionRequired(true));

            this.fragmentComponent.onPropertyValueChanged((e: api.content.page.region.ComponentPropertyValueChangedEvent) => {
                if (e.getPropertyName() === FragmentComponent.PROPERTY_FRAGMENT) {
                    this.loadFragmentContent();
                }
            });
            this.loadFragmentContent();

            this.parseContentViews(this);

            this.handleContentRemovedEvent();
            this.handleContentUpdatedEvent();
        }

        private handleContentRemovedEvent() {
            var contentDeletedListener = (event) => {
                var deleted = event.getDeletedItems().some((deletedItem: api.content.event.ContentDeletedItem) => {
                    return !deletedItem.isPending() && deletedItem.getContentId().equals(this.fragmentComponent.getFragment());
                })
                if (deleted) {
                    this.notifyFragmentLoadError();
                    new api.liveedit.ShowWarningLiveEditEvent("Fragment " + this.fragmentComponent.getFragment() +
                                                              " is no longer available").fire();
                    this.convertToBrokenFragmentView();
                }
            }

            ContentDeletedEvent.on(contentDeletedListener);

            this.onRemoved((event) => {
                ContentDeletedEvent.un(contentDeletedListener);
            });
        }

        private handleContentUpdatedEvent() {
            var contentUpdatedListener = (event: ContentUpdatedEvent) => {
                if (event.getContentId().equals(this.fragmentComponent.getFragment())) {
                    new FragmentComponentReloadRequiredEvent(this).fire();
                }
            }

            ContentUpdatedEvent.on(contentUpdatedListener);

            this.onRemoved((event) => {
                ContentUpdatedEvent.un(contentUpdatedListener);
            });
        }

        private convertToBrokenFragmentView() {
            this.getEl().setAttribute("data-portal-placeholder", "true");
            this.getEl().setAttribute("data-portal-placeholder-error", "true");
            this.removeChild(this.getFirstChild());
            var errorSpan = new api.dom.SpanEl("data-portal-placeholder-error");
            errorSpan.setHtml("Fragment content could not be found");
            this.prependChild(errorSpan);
        }

        isEmpty(): boolean {
            return !this.fragmentComponent || this.fragmentComponent.isEmpty();
        }

        containsLayout(): boolean {
            return this.fragmentContainsLayout;
        }

        getFragmentRootComponent(): api.content.page.region.Component {
            if (this.fragmentContent) {
                let page = this.fragmentContent.getPage();
                if (page) {
                    return page.getFragment();
                }
            }
            return null;
        }

        private loadFragmentContent() {
            var contentId = this.fragmentComponent.getFragment();
            if (contentId) {
                if (!this.fragmentContent || !contentId.equals(this.fragmentContent.getContentId())) {
                    new GetContentByIdRequest(contentId).sendAndParse().then((content: Content)=> {
                        this.fragmentContent = content;
                        this.notifyFragmentContentLoaded();
                        new api.liveedit.FragmentComponentLoadedEvent(this).fire();
                    }).catch((reason: any) => {
                        this.fragmentContent = null;
                        this.notifyFragmentContentLoaded();
                        this.notifyFragmentLoadError();
                        new api.liveedit.ShowWarningLiveEditEvent("Fragment " + contentId + " could not be found").fire();
                    }).done();
                }
            } else {
                this.fragmentContent = null;
                this.notifyFragmentContentLoaded();
            }
        }

        protected getComponentContextMenuActions(actions: api.ui.Action[], liveEditModel: LiveEditModel): api.ui.Action[] {
            if (this.fragmentComponent && !this.fragmentComponent.isEmpty()) {
                actions.push(new api.ui.Action("Edit in new tab").onExecuted(() => {
                    this.deselect();
                    new GetContentByIdRequest(this.fragmentComponent.getFragment()).sendAndParse().then((content: Content)=> {
                        var contentAndSummary = api.content.ContentSummaryAndCompareStatus.fromContentSummary(content);
                        new api.content.event.EditContentEvent([contentAndSummary]).fire();
                    });
                }));
            }
            return actions;
        }

        private parseContentViews(parentElement?: api.dom.Element, parentType?: api.liveedit.ItemType) {
            var children = parentElement.getChildren();
            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    if (api.liveedit.layout.LayoutItemType.get().equals(itemType)) {
                        this.fragmentContainsLayout = true;
                    }

                    // remove component-type attributes to avoid inner components of fragment to be affected by d&d sorting
                    var htmlElement = childElement.getHTMLElement();
                    htmlElement.removeAttribute("data-" + ItemType.ATTRIBUTE_TYPE);
                    htmlElement.removeAttribute("data-" + ItemType.ATTRIBUTE_REGION_NAME);
                }

                var isTextComponent = api.liveedit.text.TextItemType.get().equals(parentType);
                if (isTextComponent && childElement.getEl().getTagName().toUpperCase() == 'SECTION') {
                    // convert image urls in text component for web
                    childElement.setHtml(HTMLAreaHelper.prepareImgSrcsInValueForEdit(childElement.getHtml()), false);
                    return;
                }
                this.parseContentViews(childElement, itemType);
            });
        }

        getContentId(): api.content.ContentId {
            return this.fragmentComponent ? this.fragmentComponent.getFragment() : null;
        }

        onFragmentContentLoaded(listener: (event: api.liveedit.FragmentComponentLoadedEvent) => void) {
            this.fragmentContentLoadedListeners.push(listener);
        }

        unFragmentContentLoaded(listener: (event: api.liveedit.FragmentComponentLoadedEvent) => void) {
            this.fragmentContentLoadedListeners = this.fragmentContentLoadedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        notifyFragmentContentLoaded() {
            var event = new api.liveedit.FragmentComponentLoadedEvent(this);
            this.fragmentContentLoadedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onFragmentLoadError(listener: (event: api.liveedit.FragmentLoadErrorEvent) => void) {
            this.fragmentLoadErrorListeners.push(listener);
        }

        unFragmentLoadError(listener: (event: api.liveedit.FragmentLoadErrorEvent) => void) {
            this.fragmentLoadErrorListeners = this.fragmentLoadErrorListeners.filter((curr) => {
                return curr != listener;
            })
        }

        notifyFragmentLoadError() {
            var event = new api.liveedit.FragmentLoadErrorEvent(this);
            this.fragmentLoadErrorListeners.forEach((listener) => {
                listener(event);
            });
        }
    }
}