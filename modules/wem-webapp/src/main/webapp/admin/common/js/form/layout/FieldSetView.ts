module api.form.layout {

    export class FieldSetView extends LayoutView {

        private fieldSet: api.form.FieldSet;

        private formItemViews: api.form.FormItemView[] = [];

        constructor(context: api.form.FormContext, fieldSet: api.form.FieldSet, dataSet?: api.data.DataSet) {
            super(context, fieldSet, "field-set-view");

            this.fieldSet = fieldSet;
            this.doLayout(dataSet);
        }

        getData(): api.data.Data[] {
            var dataArray: api.data.Data[] = [];
            this.formItemViews.forEach((formItemView: api.form.FormItemView) => {
                dataArray = dataArray.concat(formItemView.getData());
            });
            return dataArray;
        }

        public getFormItemView(name: string): api.form.FormItemView {

            // TODO: Performance could be improved if the views where accessible by name from a map

            for (var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if (name == curr.getFormItem().getName()) {
                    return curr;
                }
            }

            // FormItemView not found - look inside FieldSet-s
            for (var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if (curr instanceof FieldSetView) {
                    var view = (<FieldSetView>curr).getFormItemView(name);
                    if (view != null) {
                        return view;
                    }
                }
            }

            return null;
        }

        private doLayout(dataSet: api.data.DataSet) {

            var label = new FieldSetLabel(this.fieldSet);
            this.appendChild(label);

            var wrappingDiv = new api.dom.DivEl("field-set-container");
            this.appendChild(wrappingDiv);

            this.formItemViews = new api.form.FormItemLayer().
                setFormContext(this.getContext()).
                setFormItems(this.fieldSet.getFormItems()).
                setParentElement(wrappingDiv).
                layout(dataSet);
        }

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            super.addEditContentRequestListener(listener);
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.addEditContentRequestListener(listener);
            });
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            super.removeEditContentRequestListener(listener);
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.removeEditContentRequestListener(listener);
            });
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

        validate(silent:boolean = true) : ValidationRecording {

            var recording = new ValidationRecording();
            this.formItemViews.forEach((formItemView: api.form.FormItemView)=> {
                var currRecording = formItemView.validate(silent);
                recording.flatten(currRecording);
            });

            return recording;
        }
    }
}