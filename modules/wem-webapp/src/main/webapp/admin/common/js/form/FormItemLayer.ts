module api.form {

    export class FormItemLayer {

        private context: FormContext;

        private formItems: api.form.FormItem[];

        private parentEl: api.dom.Element;

        private formItemViews: FormItemView[] = [];

        private parent: api.form.formitemset.FormItemSetOccurrenceView;

        setFormContext(context: FormContext): FormItemLayer {
            this.context = context;
            return this;
        }

        setFormItems(formItems: api.form.FormItem[]): FormItemLayer {
            this.formItems = formItems;
            return this;
        }

        setParentElement(parentEl: api.dom.Element): FormItemLayer {
            this.parentEl = parentEl;
            return this;
        }

        setParent(value: api.form.formitemset.FormItemSetOccurrenceView): FormItemLayer {
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
                if (formItem instanceof FormItemSet) {

                    var formItemSet: FormItemSet = <FormItemSet>formItem;
                    var formItemSetView = new api.form.formitemset.FormItemSetView(<api.form.formitemset.FormItemSetViewConfig>{
                        context: this.context,
                        formItemSet: formItemSet,
                        parent: this.parent,
                        parentDataSet: dataSet
                    });

                    this.parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof FieldSet) {

                    var fieldSet: FieldSet = <FieldSet>formItem;
                    var fieldSetView = new api.form.layout.FieldSetView(<api.form.layout.FieldSetViewConfig>{
                        context: this.context,
                        fieldSet: fieldSet,
                        parent: this.parent,
                        dataSet: dataSet
                    });

                    this.parentEl.appendChild(fieldSetView);
                    this.formItemViews.push(fieldSetView);
                }
                else if (formItem instanceof Input) {

                    var input: Input = <Input>formItem;

                    var inputView = new api.form.input.InputView(<api.form.input.InputViewConfig>{
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