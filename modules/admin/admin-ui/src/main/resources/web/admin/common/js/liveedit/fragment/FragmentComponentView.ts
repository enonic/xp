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
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class FragmentComponentViewBuilder extends ContentBasedComponentViewBuilder<FragmentComponent> {

        constructor() {
            super();
            this.setType(FragmentItemType.get());
            this.setContentTypeName(ContentTypeName.FRAGMENT);
        }
    }

    export class FragmentComponentView extends ContentBasedComponentView<FragmentComponent> {

        protected component: FragmentComponent;

        private fragmentContainsLayout: boolean;

        private fragmentContent: Content;

        private fragmentContentLoadedListeners: {(event: api.liveedit.FragmentComponentLoadedEvent): void}[];

        private fragmentLoadErrorListeners: {(event: api.liveedit.FragmentLoadErrorEvent): void}[];

        private editFragmentAction: api.ui.Action;

        constructor(builder: FragmentComponentViewBuilder) {

            super(<FragmentComponentViewBuilder>builder.setViewer(new FragmentComponentViewer()).setInspectActionRequired(true));

            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.fragmentContainsLayout = false;
            this.fragmentContent = null;
            this.fragmentContentLoadedListeners = [];
            this.fragmentLoadErrorListeners = [];

            this.setPlaceholder(new FragmentPlaceholder(this));

            this.component.onPropertyValueChanged((e: api.content.page.region.ComponentPropertyValueChangedEvent) => {
                if (e.getPropertyName() === FragmentComponent.PROPERTY_FRAGMENT) {
                    this.loadFragmentContent();
                }
            });
            this.loadFragmentContent();

            this.parseContentViews(this);
            this.disableLinks();

            this.handleContentRemovedEvent();
            this.handleContentUpdatedEvent();
        }

        private handleContentRemovedEvent() {
            let contentDeletedListener = (event) => {
                let deleted = event.getDeletedItems().some((deletedItem: api.content.event.ContentDeletedItem) => {
                    return !deletedItem.isPending() && deletedItem.getContentId().equals(this.component.getFragment());
                });
                if (deleted) {
                    this.notifyFragmentLoadError();
                    new api.liveedit.ShowWarningLiveEditEvent('Fragment ' + this.component.getFragment() +
                                                              ' is no longer available').fire();
                    this.convertToBrokenFragmentView();
                }
            };

            ContentDeletedEvent.on(contentDeletedListener);

            this.onRemoved((event) => {
                ContentDeletedEvent.un(contentDeletedListener);
            });
        }

        private handleContentUpdatedEvent() {
            let contentUpdatedListener = (event: ContentUpdatedEvent) => {
                if (event.getContentId().equals(this.component.getFragment())) {
                    new FragmentComponentReloadRequiredEvent(this).fire();
                }
            };

            ContentUpdatedEvent.on(contentUpdatedListener);

            this.onRemoved((event) => {
                ContentUpdatedEvent.un(contentUpdatedListener);
            });
        }

        private convertToBrokenFragmentView() {
            this.getEl().setAttribute('data-portal-placeholder', 'true');
            this.getEl().setAttribute('data-portal-placeholder-error', 'true');
            this.removeChild(this.getFirstChild());
            let errorSpan = new api.dom.SpanEl('data-portal-placeholder-error');
            errorSpan.setHtml('Fragment content could not be found');
            this.prependChild(errorSpan);
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

        getFragmentDisplayName(): string {
            return this.fragmentContent ? this.fragmentContent.getDisplayName() : null;
        }

        private loadFragmentContent() {
            let contentId = this.component.getFragment();
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
                        new api.liveedit.ShowWarningLiveEditEvent('Fragment ' + contentId + ' could not be found').fire();
                    }).done();
                }
            } else {
                this.fragmentContent = null;
                this.notifyFragmentContentLoaded();
            }
        }

        private addFragmentContextMenuActions(contentAndSummary: ContentSummaryAndCompareStatus) {
            if (this.component.isEmpty()) {
                return;
            }

            if (this.editFragmentAction) {
                this.removeContextMenuAction(this.editFragmentAction);
            }

            this.editFragmentAction = new api.ui.Action('Edit in new tab').onExecuted(() => {
                this.deselect();
                new api.content.event.EditContentEvent([contentAndSummary]).fire();
            });

            this.addContextMenuActions([this.editFragmentAction]);
        }

        private parseContentViews(parentElement?: api.dom.Element, parentType?: api.liveedit.ItemType) {
            let children = parentElement.getChildren();
            children.forEach((childElement: api.dom.Element) => {
                let itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    if (api.liveedit.layout.LayoutItemType.get().equals(itemType)) {
                        this.fragmentContainsLayout = true;
                    }

                    // remove component-type attributes to avoid inner components of fragment to be affected by d&d sorting
                    let htmlElement = childElement.getHTMLElement();
                    let hasErrors = !!htmlElement.getAttribute('data-portal-placeholder-error');
                    if (hasErrors) {
                        this.getEl().setAttribute('data-portal-placeholder-error', 'true');
                    }

                    htmlElement.removeAttribute('data-' + ItemType.ATTRIBUTE_TYPE);
                    htmlElement.removeAttribute('data-' + ItemType.ATTRIBUTE_REGION_NAME);
                }

                let isTextComponent = api.liveedit.text.TextItemType.get().equals(parentType);
                if (isTextComponent && childElement.getEl().getTagName().toUpperCase() === 'SECTION') {
                    // convert image urls in text component for web
                    childElement.setHtml(HTMLAreaHelper.prepareImgSrcsInValueForEdit(childElement.getHtml()), false);
                    return;
                }
                this.parseContentViews(childElement, itemType);
            });
        }

        getContentId(): api.content.ContentId {
            return this.component ? this.component.getFragment() : null;
        }

        onFragmentContentLoaded(listener: (event: api.liveedit.FragmentComponentLoadedEvent) => void) {
            this.fragmentContentLoadedListeners.push(listener);
        }

        unFragmentContentLoaded(listener: (event: api.liveedit.FragmentComponentLoadedEvent) => void) {
            this.fragmentContentLoadedListeners = this.fragmentContentLoadedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        notifyFragmentContentLoaded() {
            let event = new api.liveedit.FragmentComponentLoadedEvent(this);
            this.fragmentContentLoadedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onFragmentLoadError(listener: (event: api.liveedit.FragmentLoadErrorEvent) => void) {
            this.fragmentLoadErrorListeners.push(listener);
        }

        unFragmentLoadError(listener: (event: api.liveedit.FragmentLoadErrorEvent) => void) {
            this.fragmentLoadErrorListeners = this.fragmentLoadErrorListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        notifyFragmentLoadError() {
            let event = new api.liveedit.FragmentLoadErrorEvent(this);
            this.fragmentLoadErrorListeners.forEach((listener) => {
                listener(event);
            });
        }
    }
}
