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
    import GetContentByIdRequest = api.content.GetContentByIdRequest;
    import Content = api.content.Content;
    import LayoutComponentType = api.content.page.region.LayoutComponentType;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import QueryExpr = api.query.expr.QueryExpr;
    import CompareExpr = api.query.expr.CompareExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import ValueExpr = api.query.expr.ValueExpr;

    export class FragmentInspectionPanel extends ComponentInspectionPanel<FragmentComponent> {

        private fragmentComponent: FragmentComponent;

        private fragmentView: FragmentComponentView;

        private fragmentSelector: ContentComboBox;

        private fragmentSelectorForm: FragmentSelectorForm;

        private handleSelectorEvents: boolean = true;

        private componentPropertyChangedEventHandler: (event: ComponentPropertyChangedEvent) => void;

        private loader: api.content.ContentSummaryLoader;

        constructor() {
            super(<ComponentInspectionPanelConfig>{
                iconClass: api.liveedit.ItemViewIconClassResolver.resolveByType("fragment")
            });
            this.loader = new api.content.ContentSummaryLoader();
            this.loader.setAllowedContentTypeNames([ContentTypeName.FRAGMENT]);
            this.fragmentSelector = ContentComboBox.create().setMaximumOccurrences(1).setLoader(this.loader).build();

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

        setModel(liveEditModel: LiveEditModel) {
            super.setModel(liveEditModel);

            this.loader.setQueryExpr(this.createParentSiteFragmentsOnlyQuery());
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

                    if (this.isInsideLayout()) {
                        new GetContentByIdRequest(fragmentContent.getContentId()).sendAndParse().done((content: Content) => {
                            let fragmentComponent = content.getPage() ? content.getPage().getFragment() : null;

                            if (fragmentComponent &&
                                api.ObjectHelper.iFrameSafeInstanceOf(fragmentComponent.getType(), LayoutComponentType)) {
                                this.fragmentSelector.clearSelection();
                                api.notify.showWarning("Layout within layout not allowed");

                            } else {
                                this.fragmentComponent.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());
                            }
                        });
                    } else {
                        this.fragmentComponent.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());
                    }
                }
            });

            this.fragmentSelector.onOptionDeselected((option: SelectedOption<ContentSummary>) => {
                if (this.handleSelectorEvents) {
                    this.fragmentComponent.reset();
                }
            });
        }

        private isInsideLayout(): boolean {
            let parentRegion = this.fragmentView.getParentItemView();
            if (!parentRegion) {
                return false;
            }
            let parent = parentRegion.getParentItemView();
            if (!parent) {
                return false;
            }
            return api.ObjectHelper.iFrameSafeInstanceOf(parent.getType(), api.liveedit.layout.LayoutItemType);
        }

        private createParentSiteFragmentsOnlyQuery(): QueryExpr {
            var sitePath = this.liveEditModel.getSiteModel().getSite().getPath().toString();
            var compareExpr: CompareExpr = CompareExpr.like(new FieldExpr("_path"), ValueExpr.string("/content" + sitePath + "/*" ));
            return new QueryExpr(compareExpr);
        }

        getComponentView(): FragmentComponentView {
            return this.fragmentView;
        }

    }
}