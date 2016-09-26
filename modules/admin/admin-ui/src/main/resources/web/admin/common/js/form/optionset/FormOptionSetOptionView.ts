module api.form.optionset {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface FormOptionSetOptionViewConfig {

        context: FormContext;

        formOptionSetOption: FormOptionSetOption;

        parent: FormOptionSetOccurrenceView;

        parentDataSet: PropertySet;
    }

    export class FormOptionSetOptionView extends FormItemView {

        private formOptionSetOption: FormOptionSetOption;

        private parentDataSet: PropertySet;

        private collapseButton: api.dom.AEl;

        private optionItemsContainer: api.dom.DivEl;

        private formItemViews: FormItemView[] = [];

        private formItemLayer: FormItemLayer;

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

        public layout(validate: boolean = true): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.optionItemsContainer = new api.dom.DivEl("option-items-container");
            this.appendChild(this.optionItemsContainer);

            var optionItemsPropertySet = this.getSetFromArray(this.getOptionItemsPropertyArray(this.parentDataSet));

            var layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.
                setFormItems(this.formOptionSetOption.getFormItems()).
                setParentElement(this.optionItemsContainer).
                setParent(this.getParent()).
                layout(optionItemsPropertySet, validate);

            layoutPromise.then((formItemViews: FormItemView[]) => {

                if ((<FormOptionSet>this.formOptionSetOption.getParent()).isExpanded() ||
                    this.getThisPropertyFromSelectedOptionsArray() != null) {
                    this.addClass("expanded");
                }

                if (optionItemsPropertySet.getSize() > 0) {
                    this.collapseButton = this.makeCollapseButton();
                    this.prependChild(this.collapseButton);
                }

                this.prependChild(this.makeSelectionButton());

                this.formItemViews = formItemViews;
                if (validate) {
                    this.validate(true);
                }

                this.refresh();
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

        private getName(): string {
            return this.formOptionSetOption.getName();
        }

        private makeSelectionButton(): api.dom.FormInputEl {
            if (this.parentOptionSetAllowsMultiSelection()) {
                return this.makeSelectionCheckbox();
            } else {
                return this.makeSelectionRadioButton();
            }
        }

        private makeSelectionRadioButton(): api.ui.RadioButton {
            var selectedProperty = this.getSelectedOptionsArray().get(0);
            var checked = !!selectedProperty && selectedProperty.getString() == this.getName(); // this.formOptionSetOption.isDefaultOption()
            var button = new api.ui.RadioButton(this.formOptionSetOption.getLabel(), "", this.getParent().getEl().getId(), checked);
            button.onChange(() => {
                this.getSelectedOptionsArray().set(0, new Value(this.getName(), new api.data.ValueTypeString()));
            });
            return button;
        }

        private makeSelectionCheckbox(): api.ui.Checkbox {
            var checked = this.getThisPropertyFromSelectedOptionsArray() != null; // this.formOptionSetOption.isDefaultOption()
            var button = api.ui.Checkbox.create().setLabelPosition(api.ui.LabelPosition.RIGHT).
                setLabelText(this.formOptionSetOption.getLabel()).
                setChecked(checked).
                build();
            button.onChange(() => {
                if (button.isChecked()) {
                    this.getSelectedOptionsArray().add(new Value(this.getName(), new api.data.ValueTypeString()));
                } else {
                    var property = this.getThisPropertyFromSelectedOptionsArray();
                    if (!!property) {
                        this.getSelectedOptionsArray().remove(property.getIndex());
                    }
                }
            });
            return button;
        }

        private parentOptionSetAllowsMultiSelection(): boolean {
            return (<FormOptionSet>this.formOptionSetOption.getParent()).getMultiselection().getMaximum() > 1;
        }

        private makeCollapseButton(): api.dom.AEl {
            var collapseButton = new api.dom.AEl("collapse-button");

            collapseButton.onClicked((event: MouseEvent) => {
                this.toggleClass("expanded");
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return collapseButton;
        }

        update(propertySet: api.data.PropertySet, unchangedOnly?: boolean): Q.Promise<void> {
            this.parentDataSet = propertySet;
            var propertyArray = this.getOptionItemsPropertyArray(propertySet);
            return this.formItemLayer.update(this.getSetFromArray(propertyArray), unchangedOnly);
        }

        broadcastFormSizeChanged() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.broadcastFormSizeChanged();
            });
        }

        refresh() {
            //this.collapseButton.setVisible(this.formOptionSetOccurrences.getOccurrences().length > 0);
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


        validate(silent: boolean = true, viewToSkipValidation: FormItemOccurrenceView = null): ValidationRecording {

            var recording = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView)=> {
                recording.flatten(formItemView.validate(silent));
            });

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

        private renderValidationErrors(recording: ValidationRecording) {
            if (recording.isValid()) {
                this.removeClass("invalid");
                this.addClass("valid");
            }
            else {
                this.removeClass("valid");
                this.addClass("invalid");
            }
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