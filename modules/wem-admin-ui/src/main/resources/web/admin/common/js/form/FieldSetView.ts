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
            this.formItemViews.forEach((formItemView: FormItemView) => {
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
                if (api.ObjectHelper.iFrameSafeInstanceOf(curr, FieldSetView)) {
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

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            super.onEditContentRequest(listener);
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.onEditContentRequest(listener);
            });
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            super.unEditContentRequest(listener);
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.unEditContentRequest(listener);
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
                recording.flatten(formItemView.validate(silent));
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