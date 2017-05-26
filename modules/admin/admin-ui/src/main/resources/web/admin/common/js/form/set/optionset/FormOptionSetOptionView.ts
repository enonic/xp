module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Occurrences = api.form.Occurrences;
    import PropertyValueChangedEvent = api.data.PropertyValueChangedEvent;

    export interface FormOptionSetOptionViewConfig {

        context: FormContext;

        formOptionSetOption: FormOptionSetOption;

        parent: FormOptionSetOccurrenceView;

        parentDataSet: PropertySet;
    }

    export class FormOptionSetOptionView extends FormItemView {

        private formOptionSetOption: FormOptionSetOption;

        private parentDataSet: PropertySet;

        private optionItemsContainer: api.dom.DivEl;

        private formItemViews: FormItemView[] = [];

        private formItemLayer: FormItemLayer;

        private selectionChangedListeners: {(): void}[] = [];

        private checkbox: api.ui.Checkbox;

        private requiresClean: boolean;

        private isOptionSetExpandedByDefault: boolean;

        protected helpText: HelpTextContainer;

        private checkboxEnabledStatusHandler: () => void = (() => {
            this.setCheckBoxDisabled();
        }).bind(this);

        private radioDeselectHandler: (event: PropertyValueChangedEvent) => void = ((event: PropertyValueChangedEvent) => {
            if (event.getPreviousValue().getString() === this.getName()) {
                this.deselectHandle();
            }
        }).bind(this);

        private subscribedOnDeselect: boolean = false;

        constructor(config: FormOptionSetOptionViewConfig) {
            super(<FormItemViewConfig> {
                className: 'form-option-set-option-view',
                context: config.context,
                formItem: config.formOptionSetOption,
                parent: config.parent //null
            });

            this.parentDataSet = config.parentDataSet;

            this.formOptionSetOption = config.formOptionSetOption;

            this.isOptionSetExpandedByDefault = (<FormOptionSet>config.formOptionSetOption.getParent()).isExpanded();

            this.addClass(this.formOptionSetOption.getPath().getElements().length % 2 ? 'even' : 'odd');

            this.formItemLayer = new FormItemLayer(config.context);

            this.requiresClean = false;
        }

        toggleHelpText(show?: boolean) {
            this.formItemLayer.toggleHelpText(show);
            if (!!this.helpText) {
                this.helpText.toggleHelpText(show);
            }
        }

        public layout(validate: boolean = true): wemQ.Promise<void> {
            let deferred = wemQ.defer<void>();

            if (this.formOptionSetOption.getHelpText()) {
                this.helpText = new HelpTextContainer(this.formOptionSetOption.getHelpText());

                this.appendChild(this.helpText.getHelpText());

                this.toggleHelpText(this.formOptionSetOption.isHelpTextOn());
            }

            this.optionItemsContainer = new api.dom.DivEl('option-items-container');
            this.appendChild(this.optionItemsContainer);

            let optionItemsPropertySet = this.getOptionItemsPropertyArray(this.parentDataSet).getSet(0);

            let layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.setFormItems(
                this.formOptionSetOption.getFormItems()).setParentElement(this.optionItemsContainer).setParent(this.getParent()).layout(
                optionItemsPropertySet, validate && this.getThisPropertyFromSelectedOptionsArray() != null);

            layoutPromise.then((formItemViews: FormItemView[]) => {

                this.updateViewState();

                if (this.formOptionSetOption.getFormItems().length > 0) {
                    this.addClass('expandable');
                }

                this.prependChild(this.makeSelectionButton());

                this.formItemViews = formItemViews;

                this.onValidityChanged((event: RecordingValidityChangedEvent) => {
                    this.toggleClass('invalid', !event.isValid());
                });

                if (validate) {
                    this.validate(true);
                }

                this.formItemViews.forEach((formItemView: FormItemView) => {
                    formItemView.onEditContentRequest((content: api.content.ContentSummary) => {
                        let summaryAndStatus = api.content.ContentSummaryAndCompareStatus.fromContentSummary(content);
                        new api.content.event.EditContentEvent([summaryAndStatus]).fire();
                    });
                });

                api.content.event.BeforeContentSavedEvent.on(() => {
                    if (this.getThisPropertyFromSelectedOptionsArray() == null && this.requiresClean) {
                        this.resetAllFormItems();
                        this.cleanValidationForThisOption();
                        this.requiresClean = false;
                    } else if(this.isChildOfDeselectedParent()) {
                        this.removeNonDefaultOptionFromSelectionArray();
                    }
                });

                deferred.resolve(null);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }

        private getOptionItemsPropertyArray(propertySet: PropertySet): PropertyArray {
            let propertyArray = propertySet.getPropertyArray(this.getName());
            if (!propertyArray) {
                propertyArray =
                    PropertyArray.create().setType(ValueTypes.DATA).setName(this.getName()).setParent(this.parentDataSet).build();
                propertyArray.addSet();
                propertySet.addPropertyArray(propertyArray);
            }
            return propertyArray;
        }

        private getSelectedOptionsArray(): PropertyArray {
            return this.parentDataSet.getPropertyArray('_selected');
        }

        private getThisPropertyFromSelectedOptionsArray(): Property {
            let result: Property = null;
            this.getSelectedOptionsArray().forEach((property: api.data.Property, i: number) => {
                if (property.getString() === this.getName()) {
                    result = property;
                }
            });
            return result;
        }

        getName(): string {
            return this.formOptionSetOption.getName();
        }

        private makeSelectionButton(): api.dom.FormInputEl {
            if (this.isRadioSelection()) {
                return this.makeSelectionRadioButton();
            } else {
                return this.makeSelectionCheckbox();
            }
        }

        private makeSelectionRadioButton(): api.ui.RadioButton {
            const selectedProperty = this.getSelectedOptionsArray().get(0);
            const checked = !!selectedProperty && selectedProperty.getString() === this.getName();
            const button = new api.ui.RadioButton(this.formOptionSetOption.getLabel(), '', this.getParent().getEl().getId(), checked);

            button.onChange(() => {
                let selectedProp = this.getSelectedOptionsArray().get(0);
                if (!selectedProp) {
                    selectedProp = this.getSelectedOptionsArray().set(0, new Value(this.getName(), new api.data.ValueTypeString()));
                    this.subscribeOnRadioDeselect(selectedProp);
                    this.subscribedOnDeselect = true;
                } else {
                    selectedProp.setValue(new Value(this.getName(), new api.data.ValueTypeString()));
                    if (!this.subscribedOnDeselect) {
                        this.subscribeOnRadioDeselect(selectedProp);
                        this.subscribedOnDeselect = true;
                    }
                }
                this.selectHandle(button.getFirstChild());
                this.notifySelectionChanged();

                if (api.BrowserHelper.isFirefox() && !this.topEdgeIsVisible(button.getFirstChild())) {
                    wemjq(this.getHTMLElement()).closest('.form-panel').scrollTop(
                        this.calcDistToTopOfScrollableArea(button.getFirstChild()));
                }
            });

            if (selectedProperty) {
                this.subscribeOnRadioDeselect(selectedProperty);
                this.subscribedOnDeselect = true;
            }

            return button;
        }

        private topEdgeIsVisible(el: api.dom.Element): boolean {
            return this.calcDistToTopOfScrollableArea(el) > 0;
        }

        private calcDistToTopOfScrollableArea(el: api.dom.Element): number {
            return el.getEl().getOffsetTop() - this.getToolbarOffsetTop();
        }

        private getToolbarOffsetTop(delta: number = 0): number {
            let toolbar = wemjq(this.getHTMLElement()).closest('.form-panel').find('.wizard-step-navigator-and-toolbar');
            let stickyToolbarHeight = toolbar.outerHeight(true);
            let offset = toolbar.offset();
            let stickyToolbarOffset = offset ? offset.top : 0;

            return stickyToolbarOffset + stickyToolbarHeight + delta;
        }

        private subscribeOnRadioDeselect(property: Property) {
            property.unPropertyValueChanged(this.radioDeselectHandler);
            property.onPropertyValueChanged(this.radioDeselectHandler);
        }

        private makeSelectionCheckbox(): api.ui.Checkbox {
            let checked = this.getThisPropertyFromSelectedOptionsArray() != null;
            let button = api.ui.Checkbox.create()
                                        .setLabelText(this.formOptionSetOption.getLabel())
                                        .setChecked(checked)
                                        .build();

            this.checkbox = button;

            button.onChange(() => {
                if (button.isChecked()) {
                    this.getSelectedOptionsArray().add(new Value(this.getName(), new api.data.ValueTypeString()));
                    this.selectHandle(button.getFirstChild());
                    this.notifySelectionChanged();
                } else {
                    let property = this.getThisPropertyFromSelectedOptionsArray();
                    if (!!property) {
                        this.getSelectedOptionsArray().remove(property.getIndex());
                    }
                    this.deselectHandle();
                    this.notifySelectionChanged();
                }
            });

            this.setCheckBoxDisabled(checked);
            this.subscribeCheckboxOnPropertyEvents();

            return button;
        }

        private subscribeCheckboxOnPropertyEvents() {
            // as we call this method on each update() call - let's ensure there are no extra handlers binded
            this.getSelectedOptionsArray().unPropertyAdded(this.checkboxEnabledStatusHandler);
            this.getSelectedOptionsArray().unPropertyRemoved(this.checkboxEnabledStatusHandler);

            this.getSelectedOptionsArray().onPropertyAdded(this.checkboxEnabledStatusHandler);
            this.getSelectedOptionsArray().onPropertyRemoved(this.checkboxEnabledStatusHandler);
        }

        private setCheckBoxDisabled(checked?: boolean) {
            let checkBoxShouldBeDisabled = (checked != null ? !checked : !this.checkbox.isChecked()) && this.isSelectionLimitReached();

            if (this.checkbox.isDisabled() !== checkBoxShouldBeDisabled) {
                this.checkbox.setDisabled(checkBoxShouldBeDisabled, 'disabled');
            }
        }

        private selectHandle(input: api.dom.Element) {
            let thisElSelector = `div[id='${this.getEl().getId()}']`;
            this.expand();
            this.enableFormItems();

            this.optionItemsContainer.show();
            api.dom.FormEl.moveFocusToNextFocusable(input,
                thisElSelector + ' input, ' + thisElSelector + ' select, ' + thisElSelector + ' textarea');
            this.addClass('selected');
        }

        private deselectHandle() {
            this.expand(this.isOptionSetExpandedByDefault);
            this.disableFormItems();

            if(!this.isOptionSetExpandedByDefault) {
                this.optionItemsContainer.hide();
            }
            this.cleanValidationForThisOption();
            this.cleanSelectionMessageForThisOption();
            this.removeClass('selected');
            this.requiresClean = true;
        }

        private removeNonDefaultOptionFromSelectionArray() {
            if(this.formOptionSetOption.isDefaultOption()) {
                return;
            }

            if (this.isRadioSelection()) {
                const selectedProperty = this.getSelectedOptionsArray().get(0);
                const checked = !!selectedProperty && selectedProperty.getString() === this.getName();
                if(checked) {
                    this.getSelectedOptionsArray().remove(selectedProperty.getIndex());
                    this.removeClass('selected');
                }
            } else if(this.checkbox.isChecked()) {
                let property = this.getThisPropertyFromSelectedOptionsArray();
                if (!!property) {
                    this.getSelectedOptionsArray().remove(property.getIndex());
                }
                this.checkbox.setChecked(false, true);
                this.removeClass('selected');
            }
        }

        private isChildOfDeselectedParent(): boolean {
            return wemjq(this.getEl().getHTMLElement()).parents('.form-option-set-option-view').not('.selected').length > 0;
        }

        private cleanValidationForThisOption() {
            let regExp = /-view(\s|$)/;

            wemjq(this.getEl().getHTMLElement()).find('.invalid').filter(function () {
                return regExp.test(this.className);
            }).each((index, elem) => {
                wemjq(elem).removeClass('invalid');
                wemjq(elem).find('.validation-viewer ul').html('');
            });

            this.removeClass('invalid');
        }

        private cleanSelectionMessageForThisOption() {
            wemjq(this.getEl().getHTMLElement()).find('.selection-message').addClass('empty');
        }

        private expand(condition: boolean = true) {
            this.toggleClass('expanded', condition);
        }

        private enableFormItems() {
            wemjq(this.getEl().getHTMLElement()).find('.option-items-container input, .option-items-container button').each(
                (index, elem) => {
                    elem.removeAttribute('disabled');
                });
        }

        private disableFormItems() {
            wemjq(this.getEl().getHTMLElement()).find('.option-items-container input, .option-items-container button').each(
                (index, elem) => {
                    elem.setAttribute('disabled', 'true');
                });
        }

        private resetAllFormItems(): void {

            const array = this.getOptionItemsPropertyArray(this.parentDataSet);
            array.getSet(0).forEach((property) => {
                this.removeNonDataProperties(property);
            });

            this.update(this.parentDataSet);
        }

        private removeNonDataProperties(property: Property) {
            if (property.getType().equals(ValueTypes.DATA)) {
                property.getPropertySet().forEach((prop) => {
                    this.removeNonDataProperties(prop);
                });
            } else if(property.getName() != '_selected') {
                property.getParent().removeProperty(property.getName(), property.getIndex());
            }
        }

        private isSelectionLimitReached(): boolean {
            return this.getMultiselection().getMaximum() !== 0 &&
                   this.getMultiselection().getMaximum() <= this.getSelectedOptionsArray().getSize();
        }

        private isRadioSelection(): boolean {
            return this.getMultiselection().getMinimum() === 1 && this.getMultiselection().getMaximum() === 1;
        }

        private getMultiselection(): Occurrences {
            return (<FormOptionSet>this.formOptionSetOption.getParent()).getMultiselection();
        }

        reset() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.reset();
            });
        }

        update(propertySet: api.data.PropertySet, unchangedOnly?: boolean): Q.Promise<void> {
            this.parentDataSet = propertySet;
            const propertyArray = this.getOptionItemsPropertyArray(propertySet);

            return this.formItemLayer.update(propertyArray.getSet(0), unchangedOnly).then(() => {
                if (!this.isRadioSelection()) {
                    this.subscribeCheckboxOnPropertyEvents();
                } else {
                    if (this.getThisPropertyFromSelectedOptionsArray() == null) {
                        wemjq(this.getHTMLElement()).find('input:radio').first().prop('checked', false);
                    }
                    this.subscribedOnDeselect = false;
                }

                this.updateViewState();
            });
        }

        private updateViewState() {
            this.expand(this.isOptionSetExpandedByDefault || this.getThisPropertyFromSelectedOptionsArray() != null);

            if (!this.getThisPropertyFromSelectedOptionsArray()) {
                if (this.isOptionSetExpandedByDefault) {
                    this.disableFormItems();
                }
                this.cleanValidationForThisOption();
            }

            this.toggleClass('selected', !!this.getThisPropertyFromSelectedOptionsArray());
        }

        broadcastFormSizeChanged() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.broadcastFormSizeChanged();
            });
        }

        public displayValidationErrors(value: boolean) {
            this.formItemViews.forEach((view: FormItemView) => {
                view.displayValidationErrors(value);
            });
        }

        public setHighlightOnValidityChange(highlight: boolean) {
            this.formItemViews.forEach((view: FormItemView) => {
                view.setHighlightOnValidityChange(highlight);
            });
        }

        hasValidUserInput(): boolean {
            let result = true;
            this.formItemViews.forEach((formItemView: FormItemView) => {
                if (!formItemView.hasValidUserInput()) {
                    result = false;
                }
            });

            return result;
        }

        validate(silent: boolean = true): ValidationRecording {

            if (this.getThisPropertyFromSelectedOptionsArray() == null) {
                return new ValidationRecording();
            }

            let recording = new ValidationRecording();

            this.formItemViews.forEach((formItemView: FormItemView)=> {
                recording.flatten(formItemView.validate(silent));
            });

            this.toggleClass('invalid', !recording.isValid());

            return recording;
        }

        onValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.formItemViews.forEach((formItemView: FormItemView)=> {
                formItemView.onValidityChanged(listener);
            });
        }

        unValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.formItemViews.forEach((formItemView: FormItemView)=> {
                formItemView.unValidityChanged(listener);
            });
        }

        onSelectionChanged(listener: ()=> void) {
            this.selectionChangedListeners.push(listener);
        }

        unSelectionChanged(listener: ()=> void) {
            this.selectionChangedListeners.filter((currentListener: () => void) => {
                return listener === currentListener;
            });
        }

        private notifySelectionChanged() {
            this.selectionChangedListeners.forEach((listener: () => void) => listener());
        }

        giveFocus(): boolean {
            let focusGiven = false;
            if (this.formItemViews.length > 0) {
                for (let i = 0; i < this.formItemViews.length; i++) {
                    if (this.formItemViews[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onFocus(listener);
            });
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unFocus(listener);
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onBlur(listener);
            });
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unBlur(listener);
            });
        }
    }
}
