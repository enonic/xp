module api.form {

    export class FormItemLayer {

        private context: FormContext;

        private formItems: FormItem[];

        private parentEl: api.dom.Element;

        private formItemViews: FormItemView[] = [];

        private parent: FormItemSetOccurrenceView;

        setFormContext(context: FormContext): FormItemLayer {
            this.context = context;
            return this;
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

        layout(dataSet: api.data.DataSet): FormItemView[] {
            this.formItemViews = [];

            this.doLayoutDataSet(dataSet);

            return this.formItemViews;
        }


        private doLayoutDataSet(dataSet: api.data.DataSet) {

            this.formItems.forEach((formItem: FormItem) => {
                if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, FormItemSet)) {

                    var formItemSet: FormItemSet = <FormItemSet>formItem;
                    var formItemSetView = new FormItemSetView(<FormItemSetViewConfig>{
                        context: this.context,
                        formItemSet: formItemSet,
                        parent: this.parent,
                        parentDataSet: dataSet
                    });

                    this.parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, FieldSet)) {

                    var fieldSet: FieldSet = <FieldSet>formItem;
                    var fieldSetView = new FieldSetView(<FieldSetViewConfig>{
                        context: this.context,
                        fieldSet: fieldSet,
                        parent: this.parent,
                        dataSet: dataSet
                    });

                    this.parentEl.appendChild(fieldSetView);
                    this.formItemViews.push(fieldSetView);
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, Input)) {

                    var input: Input = <Input>formItem;

                    var inputView = new InputView(<InputViewConfig>{
                        context: this.context,
                        input: input,
                        parent: this.parent,
                        parentDataSet: dataSet
                    });

                    this.parentEl.appendChild(inputView);
                    this.formItemViews.push(inputView);
                }
            });
        }
    }
}