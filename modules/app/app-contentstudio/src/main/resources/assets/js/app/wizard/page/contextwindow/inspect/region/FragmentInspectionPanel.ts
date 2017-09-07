import '../../../../../../api.ts';
import {ComponentInspectionPanel, ComponentInspectionPanelConfig} from './ComponentInspectionPanel';
import {FragmentSelectorForm} from './FragmentSelectorForm';

import FragmentComponent = api.content.page.region.FragmentComponent;
import ContentSummary = api.content.ContentSummary;
import ContentId = api.content.ContentId;
import GetContentSummaryByIdRequest = api.content.resource.GetContentSummaryByIdRequest;
import ContentTypeName = api.schema.content.ContentTypeName;
import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
import Option = api.ui.selector.Option;
import SelectedOption = api.ui.selector.combobox.SelectedOption;
import GetContentByIdRequest = api.content.resource.GetContentByIdRequest;
import Content = api.content.Content;
import LayoutComponentType = api.content.page.region.LayoutComponentType;
import QueryExpr = api.query.expr.QueryExpr;
import FieldExpr = api.query.expr.FieldExpr;
import ValueExpr = api.query.expr.ValueExpr;
import FragmentDropdown = api.content.page.region.FragmentDropdown;
import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
import LiveEditModel = api.liveedit.LiveEditModel;
import Component = api.content.page.region.Component;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import i18n = api.util.i18n;

export class FragmentInspectionPanel extends ComponentInspectionPanel<FragmentComponent> {

    private fragmentComponent: FragmentComponent;

    private fragmentView: FragmentComponentView;

    private fragmentSelector: FragmentDropdown;

    private fragmentForm: FragmentSelectorForm;

    private handleSelectorEvents: boolean = true;

    private componentPropertyChangedEventHandler: (event: ComponentPropertyChangedEvent) => void;

    private contentUpdatedListener: (event: any) => void;

    constructor() {
        super(<ComponentInspectionPanelConfig>{
            iconClass: api.liveedit.ItemViewIconClassResolver.resolveByType('fragment')
        });
    }

    setModel(liveEditModel: LiveEditModel) {
        super.setModel(liveEditModel);
        if(this.fragmentSelector) {
            this.fragmentSelector.setModel(liveEditModel);
        }
        this.layout();

    }

    private layout() {

        this.removeChildren();

        this.fragmentSelector = new FragmentDropdown(this.liveEditModel);
        this.fragmentForm = new FragmentSelectorForm(this.fragmentSelector, i18n('field.fragment'));

        this.fragmentSelector.load();

        this.componentPropertyChangedEventHandler = (event: ComponentPropertyChangedEvent) => {
            // Ensure displayed selector option is removed when fragment is removed
            if (event.getPropertyName() === FragmentComponent.PROPERTY_FRAGMENT) {
                if (!this.fragmentComponent.hasFragment()) {
                    // this.fragmentSelector.setContent(null);
                    this.fragmentSelector.setSelection(null);
                }
            }
        };

        this.handleContentUpdatedEvent();
        this.initSelectorListeners();
        this.appendChild(this.fragmentForm);
    }

    private handleContentUpdatedEvent() {
        if (!this.contentUpdatedListener) {
            this.contentUpdatedListener = (event: ContentUpdatedEvent) => {
                // update currently selected option if this is the one updated
                if (this.fragmentComponent && event.getContentId().equals(this.fragmentComponent.getFragment())) {
                    this.fragmentSelector.getSelectedOption().displayValue = event.getContentSummary();
                }
            };
            ContentUpdatedEvent.on(this.contentUpdatedListener);

            this.onRemoved((event) => {
                ContentUpdatedEvent.un(this.contentUpdatedListener);
            });
        }
    }

    setFragmentComponent(fragmentView: FragmentComponentView) {
        this.fragmentView = fragmentView;
        if (this.fragmentComponent) {
            this.unregisterComponentListeners(this.fragmentComponent);
        }

        this.fragmentComponent = fragmentView.getComponent();
        this.setComponent(this.fragmentComponent);

        const contentId: ContentId = this.fragmentComponent.getFragment();
        if (contentId) {
            const fragment: ContentSummary = this.fragmentSelector.getSelection(contentId);
            if (fragment) {
                this.setSelectorValue(fragment);
            } else {
                new GetContentSummaryByIdRequest(contentId).sendAndParse().then((receivedFragment: ContentSummary) => {
                    this.setSelectorValue(receivedFragment);
                }).catch((reason: any) => {
                    if (this.isNotFoundError(reason)) {
                        this.setSelectorValue(null);
                    } else {
                        api.DefaultErrorHandler.handle(reason);
                    }
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
        if (fragment) {
            let option = this.fragmentSelector.getOptionByValue(fragment.getId().toString());
            if (!option) {
                this.fragmentSelector.addFragmentOption(fragment);
            }
        }
        this.fragmentSelector.setSelection(fragment);
        this.handleSelectorEvents = true;
    }

    private initSelectorListeners() {

        this.fragmentSelector.onOptionSelected((selectedOption: OptionSelectedEvent<ContentSummary>) => {
            if (this.handleSelectorEvents) {
                let option: Option<ContentSummary> = selectedOption.getOption();
                let fragmentContent = option.displayValue;

                if (this.isInsideLayout()) {
                    new GetContentByIdRequest(fragmentContent.getContentId()).sendAndParse().done((content: Content) => {
                        let fragmentComponent = content.getPage() ? content.getPage().getFragment() : null;

                        if (fragmentComponent &&
                            api.ObjectHelper.iFrameSafeInstanceOf(fragmentComponent.getType(), LayoutComponentType)) {
                            api.notify.showWarning(i18n('notify.nestedLayouts'));

                        } else {
                            this.fragmentComponent.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());
                        }
                    });
                } else {
                    this.fragmentComponent.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());
                }
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

    getComponentView(): FragmentComponentView {
        return this.fragmentView;
    }

}
