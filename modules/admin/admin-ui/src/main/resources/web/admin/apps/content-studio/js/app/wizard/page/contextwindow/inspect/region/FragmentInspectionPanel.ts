module app.wizard.page.contextwindow.inspect.region {

    import FragmentComponent = api.content.page.region.FragmentComponent;
    import ContentSummary = api.content.ContentSummary;
    import ContentId = api.content.ContentId;
    import GetContentSummaryByIdRequest = api.content.GetContentSummaryByIdRequest;
    import ContentComboBox = api.content.ContentComboBox;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import PropertyTree = api.data.PropertyTree;

    export class FragmentInspectionPanel extends ComponentInspectionPanel<FragmentComponent> {

        private fragmentComponent: FragmentComponent;

        private fragmentView: FragmentComponentView;

        private fragmentSelector: ContentComboBox;

        private fragmentSelectorForm: FragmentSelectorForm;

        private handleSelectorEvents: boolean = true;

        private componentPropertyChangedEventHandler: (event: ComponentPropertyChangedEvent) => void;

        constructor() {
            super(<ComponentInspectionPanelConfig>{
                iconClass: api.liveedit.ItemViewIconClassResolver.resolveByType("fragment")
            });
            var loader = new api.content.ContentSummaryLoader();
            loader.setAllowedContentTypeNames([ContentTypeName.FRAGMENT]);
            this.fragmentSelector = ContentComboBox.create().setMaximumOccurrences(1).setLoader(loader).build();

            this.fragmentSelectorForm = new FragmentSelectorForm(this.fragmentSelector, "Fragment");

            this.componentPropertyChangedEventHandler = (event: ComponentPropertyChangedEvent) => {
                // Ensure displayed selector option is removed when fragment is removed
                if (event.getPropertyName() == FragmentComponent.PROPERTY_FRAGMENT) {
                    if (!this.fragmentComponent.hasFragment()) {
                        this.fragmentSelector.setContent(null);
                    }
                }
            };

            this.initSelectorListeners();
            this.appendChild(this.fragmentSelectorForm);
        }

        setComponent(component: FragmentComponent) {
            super.setComponent(component);
        }

        setFragmentComponent(fragmentView: FragmentComponentView) {
            this.fragmentView = fragmentView;
            if (this.fragmentComponent) {
                this.unregisterComponentListeners(this.fragmentComponent);
            }

            this.fragmentComponent = fragmentView.getComponent();
            this.setComponent(this.fragmentComponent);

            var contentId: ContentId = this.fragmentComponent.getFragment();
            if (contentId) {
                var fragment: ContentSummary = this.fragmentSelector.getContent(contentId);
                if (fragment) {
                    this.setSelectorValue(fragment);
                } else {
                    new GetContentSummaryByIdRequest(contentId).sendAndParse().then((fragment: ContentSummary) => {
                        this.setSelectorValue(fragment);
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
                }
            } else {
                this.setSelectorValue(null);
            }

            this.registerComponentListeners(this.fragmentComponent);
        }

        private registerComponentListeners(component: FragmentComponent) {
            component.onPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        private unregisterComponentListeners(component: FragmentComponent) {
            component.unPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        private setSelectorValue(fragment: ContentSummary) {
            this.handleSelectorEvents = false;
            this.fragmentSelector.setContent(fragment);
            this.handleSelectorEvents = true;
        }

        private initSelectorListeners() {

            this.fragmentSelector.onOptionSelected((selectedOption: SelectedOption<ContentSummary>) => {
                if (this.handleSelectorEvents) {
                    var option: Option<ContentSummary> = selectedOption.getOption();
                    var fragmentContent = option.displayValue;
                    this.fragmentComponent.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());
                }
            });

            this.fragmentSelector.onOptionDeselected((option: SelectedOption<ContentSummary>) => {
                if (this.handleSelectorEvents) {
                    this.fragmentComponent.setFragment(null, null);
                }
            });
        }

        getComponentView(): FragmentComponentView {
            return this.fragmentView;
        }

    }
}