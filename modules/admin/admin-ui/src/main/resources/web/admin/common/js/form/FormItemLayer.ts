module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;

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

        layout(propertySet: PropertySet): wemQ.Promise<FormItemView[]> {

            this.formItemViews = [];

            return this.doLayoutPropertySet(propertySet).then(() => {
                return wemQ<FormItemView[]>(this.formItemViews);
            });
        }

        private doLayoutPropertySet(propertySet: PropertySet): wemQ.Promise<void> {

            var layoutPromises: wemQ.Promise<void>[] = [];

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

                    layoutPromises.push(formItemSetView.layout());
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, FieldSet)) {

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
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, Input)) {

                    var input: Input = <Input>formItem;

                    var inputView = new InputView(<InputViewConfig>{
                        context: this.context,
                        input: input,
                        parent: this.parent,
                        parentDataSet: propertySet
                    });
                    this.parentEl.appendChild(inputView);
                    this.formItemViews.push(inputView);

                    layoutPromises.push(inputView.layout());
                }
            });

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