module api.form.formitemset {

    import support = api.form.inputtype.support;

    export class FormItemSetOccurrenceView extends api.form.FormItemOccurrenceView {

        private context: api.form.FormContext;

        private formItemSetOccurrence:FormItemSetOccurrence;

        private formItemSet:api.form.FormItemSet;

        private removeButton:api.dom.AEl;

        private label:FormItemSetLabel;

        private constructedWithData:boolean;

        private dataSet:api.data.DataSet;

        private formItemViews:api.form.FormItemView[] = [];

        private formItemSetOccurrencesContainer:api.dom.DivEl;

        private inputTypeListeners: {[eventName:string]:{(event:api.form.inputtype.support.InputTypeEvent):void}[]} = {};

        private previousErrors:api.form.ValidationRecorder;

        constructor(context: api.form.FormContext, formItemSetOccurrence:FormItemSetOccurrence, formItemSet:api.form.FormItemSet,
                    dataSet:api.data.DataSet) {
            super("form-item-set-occurrence-view", formItemSetOccurrence);
            this.inputTypeListeners[api.form.inputtype.support.InputTypeEvents.ValidityChanged] = [];
            this.context = context;
            this.formItemSetOccurrence = formItemSetOccurrence;
            this.formItemSet = formItemSet;
            this.constructedWithData = dataSet != null;
            this.dataSet = dataSet;
            this.doLayout();
            this.refresh();
        }

        private doLayout() {
            this.removeButton = new api.dom.AEl("remove-button");
            this.appendChild(this.removeButton);
            this.removeButton.setClickListener(() => {
                this.notifyRemoveButtonClicked();
            });

            this.label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(this.label);

            this.formItemSetOccurrencesContainer = new api.dom.DivEl("form-item-set-occurrences-container");
            this.appendChild(this.formItemSetOccurrencesContainer);


            this.formItemViews =  new api.form.FormItemLayer().
                setFormContext(this.context).
                setFormItems(this.formItemSet.getFormItems()).
                setParentElement(this.formItemSetOccurrencesContainer).
                layout(this.dataSet);

            this.formItemViews.forEach((formItemView:api.form.FormItemView) => {
                formItemView.onValidityChanged((event:support.ValidityChangedEvent) => {
                    var validationRecorder:api.form.ValidationRecorder = new api.form.ValidationRecorder();
                    this.validate(validationRecorder);
                    if (this.validityChanged(validationRecorder)) {
                        this.notifyValidityChanged(new support.ValidityChangedEvent(validationRecorder.valid()));
                    }
                });
            });
        }

        getFormItemViews():api.form.FormItemView[] {
            return this.formItemViews;
        }

        refresh() {

            if ( !this.formItemSetOccurrence.oneAndOnly() ) {
                this.label.addClass("drag-control");
            } else {
                this.label.removeClass("drag-control");
            }

            this.getEl().setData("dataId", this.formItemSetOccurrence.getDataId().toString());

            this.removeButton.setVisible(this.formItemSetOccurrence.showRemoveButton());
        }

        public getValueAtPath(path:api.data.DataPath):api.data.Value {
            if( path == null ) {
                throw new Error("To get a value, a path is required");
            }
            if( path.elementCount() == 0 ) {
                throw new Error("Cannot get value from empty path: " + path.toString());
            }

            if( path.elementCount() == 1 ){
                return this.getValue(path.getFirstElement().toDataId());
            }
            else {
                return this.forwardGetValueAtPath(path);
            }
        }

        public getValue(dataId:api.data.DataId):api.data.Value{

            var inputView = this.getInputView( dataId.getName() );
            if( inputView == null ) {
                return null;
            }
            return inputView.getValue(dataId.getArrayIndex());
        }

        private forwardGetValueAtPath(path:api.data.DataPath):api.data.Value{

            var dataId:api.data.DataId = path.getFirstElement().toDataId();
            var formItemSetView = this.getFormItemSetView( dataId.getName() );
            if( formItemSetView == null ) {
                return null;
            }
            var formItemSetOccurrenceView = formItemSetView.getFormItemSetOccurrenceView(dataId.getArrayIndex());
            return formItemSetOccurrenceView.getValueAtPath(path.newWithoutFirstElement());
        }

        public getInputView(name:string):api.form.input.InputView {

            var formItemView = this.getFormItemView(name);
            if( formItemView == null ) {
                return null;
            }
            if( !(formItemView instanceof api.form.input.InputView) ) {
                throw new Error( "Found a FormItemView with name [" + name + "], but it was not an InputView" );
            }
            return <api.form.input.InputView>formItemView;
        }

        public getFormItemSetView(name:string):FormItemSetView {

            var formItemView = this.getFormItemView(name);
            if( formItemView == null ) {
                return null;
            }
            if( !(formItemView instanceof FormItemSetView) ) {
                throw new Error( "Found a FormItemView with name [" + name + "], but it was not an FormItemSetView" );
            }
            return <FormItemSetView>formItemView;
        }

        validate(validationRecorder: api.form.ValidationRecorder) {
            this.formItemViews.forEach((formItemView:api.form.FormItemView) => {
                formItemView.validate(validationRecorder);
            });
        }

        public getFormItemView(name:string):api.form.FormItemView {

            // TODO: Performance could be improved if the views where accessible by name from a map

            for(var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if(name == curr.getFormItem().getName()) {
                    return curr;
                }
            }

            // FormItemView not found - look inside FieldSet-s
            for(var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if(curr instanceof api.form.layout.FieldSetView) {
                    var view = (<api.form.layout.FieldSetView>curr).getFormItemView( name );
                    if( view != null ) {
                        return view;
                    }
                }
            }

            return null;
        }

        getDataSet():api.data.DataSet {

            var dataSet = new api.data.DataSet(this.formItemSet.getName());
            this.formItemViews.forEach((formItemView:api.form.FormItemView) => {
                formItemView.getData().forEach((data:api.data.Data) => {
                    dataSet.addData(data);
                });
            });
            return dataSet;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            var attachments:api.content.attachment.Attachment[] = [];
            this.formItemViews.forEach((formItemView:api.form.FormItemView) => {
                formItemView.getAttachments().forEach( (attachment:api.content.attachment.Attachment) => {
                    attachments.push( attachment );
                } );
            });
            return attachments;
        }

        toggleContainer(show:boolean) {
            if (show) {
                this.formItemSetOccurrencesContainer.show();
            } else {
                this.formItemSetOccurrencesContainer.hide();
            }
        }

        onValidityChanged(listener:(event:support.ValidityChangedEvent)=>void) {
            this.inputTypeListeners[support.InputTypeEvents.ValidityChanged].push(listener);
        }

        unValidityChanged(listener:(event:support.ValidityChangedEvent)=>void) {
            this.inputTypeListeners[support.InputTypeEvents.ValidityChanged].filter((currentListener:(event:support.InputTypeEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyListeners(eventName:support.InputTypeEvents, event:support.InputTypeEvent) {
            this.inputTypeListeners[eventName].forEach((listener:(event:support.InputTypeEvent)=>void) => {
                listener(event);
            });
        }

        private notifyValidityChanged(event:support.ValidityChangedEvent) {
            this.notifyListeners(support.InputTypeEvents.ValidityChanged, event);
        }


        private validityChanged(validationRecorder:api.form.ValidationRecorder):boolean {
            var validityChanged:boolean = this.previousErrors == null || this.previousErrors.valid() != validationRecorder.valid();
            this.previousErrors = validationRecorder;
            return validityChanged;
        }
    }

}