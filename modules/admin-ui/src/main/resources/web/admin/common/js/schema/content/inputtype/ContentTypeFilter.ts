module api.schema.content.inputtype {

    import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
    import InputValidationRecording = api.form.inputtype.InputValidationRecording;
    import InputValidityChangedEvent = api.form.inputtype.InputValidityChangedEvent;
    import ValueChangedEvent = api.form.inputtype.ValueChangedEvent;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Input = api.form.Input;
    import ContentTypeComboBox = api.schema.content.ContentTypeComboBox;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import ModuleKey = api.module.ModuleKey;

    export class ContentTypeFilter extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private input: Input;

        private propertyArray: PropertyArray;

        private combobox: ContentTypeComboBox;

        private validationRecording: InputValidationRecording;

        private layoutInProgress: boolean;

        private context:  ContentInputTypeViewContext<any>;

        constructor(context: ContentInputTypeViewContext<any>) {
            super('content-type-filter');
            this.context = context;
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            this.layoutInProgress = true;

            this.input = input;
            this.propertyArray = propertyArray;

            this.combobox = new ContentTypeComboBox(input.getOccurrences().getMaximum());

            // select properties once when combobox has been loaded first time
            var selectProperties = (contentTypeArray: ContentTypeSummary[]) => {

                propertyArray.forEach((property: Property) => {
                    var contentTypeName = property.getString();
                    this.combobox.getComboBox().setValue(contentTypeName);
                });
                
                this.layoutInProgress = false;
                this.combobox.unLoaded(selectProperties);
            };
            this.combobox.onLoaded(selectProperties);

            this.combobox.onLoaded((contentTypeArray: ContentTypeSummary[]) => {
                this.combobox.getComboBox().removeAllOptions();
                this.filterOptions(contentTypeArray);
            });

            this.combobox.onOptionSelected((event: OptionSelectedEvent<ContentTypeSummary>) => {
                if (this.layoutInProgress) {
                    return;
                }

                var value = new Value(event.getOption().displayValue.getContentTypeName().toString(), ValueTypes.STRING);
                if (this.combobox.countSelected() == 1) { // overwrite initial value
                    this.propertyArray.set(0, value);
                }
                else {
                    this.propertyArray.add(value);
                }

                this.validate(false);
            });

            this.combobox.onOptionDeselected((option: SelectedOption<ContentTypeSummary>) => {
                this.propertyArray.remove(option.getIndex());
                this.validate(false);
            });

            this.appendChild(this.combobox);

            return wemQ<void>(null);
        }

        private getValues(): Value[] {
            return this.combobox.getSelectedDisplayValues().map((contentType: ContentTypeSummary) => {
                return new Value(contentType.getContentTypeName().toString(), ValueTypes.STRING);
            });
        }

        private filterOptions(contentTypeArray: ContentTypeSummary[]) {
            new api.content.GetNearestSiteRequest(this.context.site.getContentId()).sendAndParse().then((parentSite: api.content.site.Site) => {

                var typesAllowedEverywhere: {[key:string]: ContentTypeName} = {};
                [ContentTypeName.UNSTRUCTURED, ContentTypeName.FOLDER, ContentTypeName.SITE,
                    ContentTypeName.SHORTCUT].forEach((contentTypeName: ContentTypeName) => {
                        typesAllowedEverywhere[contentTypeName.toString()] = contentTypeName;
                    });
                var siteModules: {[key:string]: ModuleKey} = {};
                parentSite.getModuleKeys().forEach((moduleKey: ModuleKey) => {
                    siteModules[moduleKey.toString()] = moduleKey;
                });

                var result = contentTypeArray.filter((item: ContentTypeSummary) => {
                    var contentTypeName = item.getContentTypeName();
                    if (item.isAbstract()) {
                        return false;
                    }
                    else if (contentTypeName.isDescendantOfMedia()) {
                        return true;
                    }
                    else if (contentTypeName.isTemplateFolder()) {
                        return true; // template-folder is allowed under site
                    }
                    else if (typesAllowedEverywhere[contentTypeName.toString()]) {
                        return true;
                    }
                    else if (siteModules[contentTypeName.getModuleKey().toString()]) {
                        return true;
                    }
                    else {
                        return false;
                    }

                });

                result.sort(this.contentTypeSummaryComparator);
                this.combobox.getComboBox().setOptions(this.combobox.createOptions(result));
            });
        }

        private contentTypeSummaryComparator(item1: ContentTypeSummary, item2: ContentTypeSummary): number {
            if (item1.getDisplayName().toLowerCase() > item2.getDisplayName().toLowerCase()) {
                return 1;
            } else if (item1.getDisplayName().toLowerCase() < item2.getDisplayName().toLowerCase()) {
                return -1;
            } else if (item1.getName() > item2.getName()) {
                return 1;
            } else if (item1.getName() < item2.getName()) {
                return -1;
            } else {
                return 0;
            }
        }

        validate(silent: boolean = true): InputValidationRecording {

            var recording = new InputValidationRecording(),
                values = this.getValues(),
                occurrences = this.input.getOccurrences();

            recording.setBreaksMinimumOccurrences(occurrences.minimumBreached(values.length));
            recording.setBreaksMaximumOccurrences(occurrences.maximumBreached(values.length));

            if (!silent && recording.validityChanged(this.validationRecording)) {
                this.notifyValidityChanged(new InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.validationRecording = recording;
            return recording;
        }

        giveFocus(): boolean {
            return this.combobox.maximumOccurrencesReached() ? false : this.combobox.giveFocus();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.combobox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.combobox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.combobox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.combobox.unBlur(listener);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ContentTypeFilter", ContentTypeFilter));

}