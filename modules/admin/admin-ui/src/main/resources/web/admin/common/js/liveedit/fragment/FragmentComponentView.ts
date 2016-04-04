module api.liveedit.fragment {

    import ComponentView = api.liveedit.ComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import FragmentComponent = api.content.page.region.FragmentComponent;
    import GetContentByIdRequest = api.content.GetContentByIdRequest;
    import Content = api.content.Content;
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

        constructor(builder: FragmentComponentViewBuilder) {
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.fragmentComponent = builder.component;
            this.fragmentContainsLayout = false;
            this.fragmentContent = null;
            this.fragmentContentLoadedListeners = [];

            super(builder.setPlaceholder(new FragmentPlaceholder(this)).setTooltipViewer(
                new FragmentComponentViewer()).setInspectActionRequired(true));

            this.fragmentComponent.onPropertyValueChanged((e: api.content.page.region.ComponentPropertyValueChangedEvent) => {
                if (e.getPropertyName() === FragmentComponent.PROPERTY_FRAGMENT) {
                    this.loadFragmentContent();
                }
            });
            this.loadFragmentContent();

            this.parseContentViews(this);
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
                    }).catch((reason: any) => {
                        this.fragmentContent = null;
                        this.notifyFragmentContentLoaded();
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

    }
}