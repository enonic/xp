module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;

    export class FormItemLayer {

        private context: FormContext;

        private formItems: FormItem[];

        private parentEl: api.dom.Element;

        private formItemViews: FormItemView[] = [];

        private parent: FormItemSetOccurrenceView;

        public static debug: boolean = false;

        constructor(context: FormContext) {
            this.context = context;
        }

        setFormItems(formItems: FormItem[]): FormItemLayer {
            this.formItems = formItems;
            return this;
        }

        setParentElement(parentEl: api.dom.Element): FormItemLayer {
            this.parentEl = parentEl;
            return this;
        }

        setParent(value: FormItemSetOccurrenceView): FormItemLayer {
            this.parent = value;
            return this;
        }

        layout(propertySet: PropertySet, validate: boolean = true): wemQ.Promise<FormItemView[]> {

            this.formItemViews = [];

            return this.doLayoutPropertySet(propertySet, validate).then(() => {
                return wemQ<FormItemView[]>(this.formItemViews);
            });
        }

        private doLayoutPropertySet(propertySet: PropertySet, validate: boolean = true): wemQ.Promise<void> {

            let layoutPromises: wemQ.Promise<void>[] = [];

            const inputs: InputView[] = [];

            this.formItems.forEach((formItem: FormItem) => {

                if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, FormItemSet)) {

                    var formItemSet: FormItemSet = <FormItemSet>formItem;
                    var propertyArray: PropertyArray = propertySet.getPropertyArray(formItemSet.getName());

                    if (!propertyArray || propertyArray.getSize() == 0) {
                        if (!this.context) {
                            this.context = FormContext.create().setShowEmptyFormItemSetOccurrences(false).build();
                        } else {
                            this.context.setShowEmptyFormItemSetOccurrences(false);
                        }
                    }
                    var formItemSetView = new FormItemSetView(<FormItemSetViewConfig>{
                        context: this.context,
                        formItemSet: formItemSet,
                        parent: this.parent,
                        parentDataSet: propertySet
                    });
                    this.parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);

                    layoutPromises.push(formItemSetView.layout(validate));
                } else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, FieldSet)) {

                    var fieldSet: FieldSet = <FieldSet>formItem;
                    var fieldSetView = new FieldSetView(<FieldSetViewConfig>{
                        context: this.context,
                        fieldSet: fieldSet,
                        parent: this.parent,
                        dataSet: propertySet
                    });

                    this.parentEl.appendChild(fieldSetView);
                    this.formItemViews.push(fieldSetView);

                    layoutPromises.push(fieldSetView.layout());
                } else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, Input)) {

                    var input: Input = <Input>formItem;

                    var inputView = new InputView(<InputViewConfig>{
                        context: this.context,
                        input: input,
                        parent: this.parent,
                        parentDataSet: propertySet
                    });
                    this.parentEl.appendChild(inputView);
                    this.formItemViews.push(inputView);

                    inputs.push(inputView);

                    layoutPromises.push(inputView.layout(validate));
                }
            });

            // Bind next focus targets
            if (inputs.length > 1) {
                FocusSwitchEvent.on((event: FocusSwitchEvent) => {
                    const inputTypeView = event.getInputTypeView();
                    const lastIndex = inputs.length - 1;
                    let currentIndex = -1;
                    inputs.map((input) => input.getInputTypeView()).some((input, index) => {
                        // quick equality check
                        if (input.getElement() === inputTypeView.getElement()) {
                            currentIndex = index;
                            return true;
                        }
                        return false;
                    });

                    if (currentIndex >= 0) {
                        const nextIndex = currentIndex === lastIndex ? 0 : currentIndex + 1;
                        inputs[nextIndex].giveFocus();
                    }
                });
            }

            return wemQ.all(layoutPromises).spread<void>(() => {
                return wemQ<void>(null);
            });
        }

        update(propertySet: PropertySet, unchangedOnly?: boolean): wemQ.Promise<void> {
            if (FormItemLayer.debug) {
                console.debug('FormItemLayer.update' + (unchangedOnly ? ' (unchanged only)' : ''), this, propertySet);
            }
            var updatePromises = [];

            this.formItemViews.forEach((formItemView: FormItemView) => {
                updatePromises.push(formItemView.update(propertySet, unchangedOnly));
            });

            return wemQ.all(updatePromises).spread<void>(() => {
                return wemQ<void>(null);
            }).catch(api.DefaultErrorHandler.handle);
        }
    }
}