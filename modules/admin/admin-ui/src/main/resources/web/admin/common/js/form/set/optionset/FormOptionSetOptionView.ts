module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Occurrences = api.form.Occurrences;

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

        private checkboxEnabledStatusHandler: () => void = (() => {
            this.setCheckBoxDisabled()
        }).bind(this);

        constructor(config: FormOptionSetOptionViewConfig) {
            super(<FormItemViewConfig> {
                className: "form-option-set-option-view",
                context: config.context,
                formItem: config.formOptionSetOption,
                parent: config.parent //null
            });

            this.parentDataSet = config.parentDataSet;

            this.formOptionSetOption = config.formOptionSetOption;

            this.addClass(this.formOptionSetOption.getPath().getElements().length % 2 ? "even" : "odd");

            this.formItemLayer = new FormItemLayer(config.context);
        }

        toggleHelpText(show?: boolean) {
            this.formItemLayer.toggleHelpText(show);
        }

        public layout(validate: boolean = true): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.optionItemsContainer = new api.dom.DivEl("option-items-container");
            this.appendChild(this.optionItemsContainer);

            var optionItemsPropertySet = this.getSetFromArray(this.getOptionItemsPropertyArray(this.parentDataSet));

            var layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.
                setFormItems(this.formOptionSetOption.getFormItems()).
                setParentElement(this.optionItemsContainer).
                setParent(this.getParent()).
                layout(optionItemsPropertySet, validate && this.getThisPropertyFromSelectedOptionsArray() != null);

            layoutPromise.then((formItemViews: FormItemView[]) => {

                if (this.isOptionSetExpandedByDefault() || this.getThisPropertyFromSelectedOptionsArray() != null) {
                    this.expand();
                }

                if (this.isOptionSetExpandedByDefault() && this.getThisPropertyFromSelectedOptionsArray() == null) {
                    this.disableFormItems();
                }
                
                if(this.getThisPropertyFromSelectedOptionsArray() != null) {
                    this.addClass("selected");
                }

                if(this.formOptionSetOption.getFormItems().length > 0) {
                    this.addClass("expandable");
                }
                
                this.prependChild(this.makeSelectionButton());

                this.formItemViews = formItemViews;

                this.onValidityChanged((event: RecordingValidityChangedEvent) => {
                    this.toggleClass("invalid", !event.isValid());
                })

                if (validate) {
                    this.validate(true);
                }

                deferred.resolve(null);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }

        private getSetFromArray(propertyArray: PropertyArray): PropertySet {
            var dataSet = propertyArray.getSet(0);
            if (!dataSet) {
                dataSet = propertyArray.addSet();
            }
            return dataSet;
        }

        private getOptionItemsPropertyArray(propertySet: PropertySet): PropertyArray {
            var propertyArray = propertySet.getPropertyArray(this.getName());
            if (!propertyArray) {
                propertyArray = PropertyArray.create().
                    setType(ValueTypes.DATA).
                    setName(this.getName()).
                    setParent(this.parentDataSet).
                    build();
                propertySet.addPropertyArray(propertyArray);
            }
            return propertyArray;
        }

        private getSelectedOptionsArray(): PropertyArray {
            return this.parentDataSet.getPropertyArray(this.formOptionSetOption.getParent().getName() + "_selection");
        }

        private getThisPropertyFromSelectedOptionsArray(): Property {
            var result: Property = null;
            this.getSelectedOptionsArray().forEach((property: api.data.Property, i: number) => {
                if (property.getString() == this.getName()) {
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
            var selectedProperty = this.getSelectedOptionsArray().get(0),
                checked = !!selectedProperty && selectedProperty.getString() == this.getName(),
                button = new api.ui.RadioButton(this.formOptionSetOption.getLabel(), "", this.getParent().getEl().getId(), checked),
                subscribedOnDeselect = false;

            button.onChange(() => {
                var selectedProperty = this.getSelectedOptionsArray().get(0);
                if (!selectedProperty) {
                    selectedProperty = this.getSelectedOptionsArray().set(0, new Value(this.getName(), new api.data.ValueTypeString()));
                    this.subscribeOnRadioDeselect(selectedProperty);
                    subscribedOnDeselect = true;
                } else {
                    selectedProperty.setValue(new Value(this.getName(), new api.data.ValueTypeString()))
                    if (!subscribedOnDeselect) {
                        this.subscribeOnRadioDeselect(selectedProperty);
                        subscribedOnDeselect = true;
                    }
                }
                this.selectHandle(button.getFirstChild());
                this.notifySelectionChanged();
            });
            if (!!selectedProperty) {
                this.subscribeOnRadioDeselect(selectedProperty);
                subscribedOnDeselect = true;
            }
            return button;
        }

        private subscribeOnRadioDeselect(property: Property) {
            var radioDeselectHandler = (event: api.data.PropertyValueChangedEvent) => {
                if (event.getPreviousValue().getString() == this.getName()) {
                    this.deselectHandle();
                }
            }
            property.onPropertyValueChanged(radioDeselectHandler);
        }

        private makeSelectionCheckbox(): api.ui.Checkbox {
            var checked = this.getThisPropertyFromSelectedOptionsArray() != null,
                button = api.ui.Checkbox.create().setLabelPosition(api.ui.LabelPosition.RIGHT).
                setLabelText(this.formOptionSetOption.getLabel()).
                setChecked(checked).
                build();

            this.checkbox = button;

            button.onChange(() => {
                if (button.isChecked()) {
                    this.getSelectedOptionsArray().add(new Value(this.getName(), new api.data.ValueTypeString()));
                    this.selectHandle(button.getFirstChild());
                    this.notifySelectionChanged();
                } else {
                    var property = this.getThisPropertyFromSelectedOptionsArray();
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
            var checkBoxShouldBeDisabled = (checked != null ? !checked : !this.checkbox.isChecked()) && this.isSelectionLimitReached();

            if (this.checkbox.isDisabled() != checkBoxShouldBeDisabled) {
                this.checkbox.setDisabled(checkBoxShouldBeDisabled, "disabled");
            }
        }

        private selectHandle(input: api.dom.Element) {
            this.expand();
            this.enableFormItems();
            api.dom.FormEl.moveFocusToNextFocusable(input, "input, select");
            this.addClass("selected");
        }

        private deselectHandle() {
            this.expand(this.isOptionSetExpandedByDefault());
            this.disableAndResetAllFormItems();
            this.cleanValidationForThisOption();
            this.removeClass("selected");
        }

        private cleanValidationForThisOption() {
            var regExp = /-view(\s|$)/;

            wemjq(this.getEl().getHTMLElement()).find(".invalid").filter(function () {
                return regExp.test(this.className);
            }).each((index, elem) => {
                wemjq(elem).removeClass("invalid");
                wemjq(elem).find(".validation-viewer ul").html("");
            });

            this.removeClass("invalid");
        }

        private isOptionSetExpandedByDefault(): boolean {
            return (<FormOptionSet>this.formOptionSetOption.getParent()).isExpanded();
        }

        private expand(condition?: boolean) {
            this.toggleClass("expanded", condition == undefined ? true : condition);
        }

        private enableFormItems() {
            wemjq(this.getEl().getHTMLElement()).find(".option-items-container input, .option-items-container button").
                each((index, elem) => {
                    elem.removeAttribute("disabled");
                });
        }

        private disableFormItems() {
            wemjq(this.getEl().getHTMLElement()).find(".option-items-container input, .option-items-container button").
                each((index, elem) => {
                    elem.setAttribute("disabled", "true");
                });
        }

        private disableAndResetAllFormItems(): void {
            this.disableFormItems();

            var array = this.getOptionItemsPropertyArray(this.parentDataSet);
            array.getSet(0).reset();
            this.update(this.parentDataSet);
        }

        private isSelectionLimitReached(): boolean {
            return this.getSelectedOptionsArray().getSize() >= this.getMultiselection().getMaximum();
        }

        private isRadioSelection(): boolean {
            return this.getMultiselection().getMinimum() == 1 && this.getMultiselection().getMaximum() == 1;
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
            var propertyArray = this.getOptionItemsPropertyArray(propertySet);
            return this.formItemLayer.update(this.getSetFromArray(propertyArray), unchangedOnly).then(() => {
                if (!this.isRadioSelection()) {
                    this.subscribeCheckboxOnPropertyEvents();
                }
            });
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

            var result = true;
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

            var recording = new ValidationRecording();

            this.formItemViews.forEach((formItemView: FormItemView)=> {
                recording.flatten(formItemView.validate(silent));
            });

            this.toggleClass("invalid", !recording.isValid());
            
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
                return listener == currentListener;
            });
        }

        private notifySelectionChanged() {
            this.selectionChangedListeners.forEach((listener: () => void) => listener());
        }

        giveFocus(): boolean {

            var focusGiven = false;
            if (this.formItemViews.length > 0) {
                for (var i = 0; i < this.formItemViews.length; i++) {
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