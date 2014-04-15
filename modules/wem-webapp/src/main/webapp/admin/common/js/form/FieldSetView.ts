module api.form {

    export interface FieldSetViewConfig {

        context: FormContext;

        fieldSet: FieldSet;

        parent: FormItemSetOccurrenceView;

        dataSet?: api.data.DataSet;
    }

    export class FieldSetView extends LayoutView {

        private fieldSet: FieldSet;

        private formItemViews: FormItemView[] = [];

        constructor(config: FieldSetViewConfig) {
            super(<LayoutViewConfig>{
                context: config.context,
                layout: config.fieldSet,
                parent: config.parent,
                className: "field-set-view"
            });

            this.fieldSet = config.fieldSet;
            this.doLayout(config.dataSet);
        }

        broadcastFormSizeChanged() {
            this.formItemViews.forEach((formItemView:FormItemView) => {
                formItemView.broadcastFormSizeChanged();
            });
        }

        public getFormItemView(name: string): FormItemView {

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

            this.formItemViews = new FormItemLayer().
                setFormContext(this.getContext()).
                setFormItems(this.fieldSet.getFormItems()).
                setParentElement(wrappingDiv).
                setParent(this.getParent()).
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

        validate(silent: boolean = true): ValidationRecording {

            var recording = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView)=> {
                var currRecording = formItemView.validate(silent);
                recording.flatten(currRecording);
            });

            return recording;
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {

            this.formItemViews.forEach((formItemView: FormItemView)=> {
                formItemView.onValidityChanged(listener);
            });
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.formItemViews.forEach((formItemView: FormItemView)=> {
                formItemView.unValidityChanged(listener);
            });
        }
    }
}