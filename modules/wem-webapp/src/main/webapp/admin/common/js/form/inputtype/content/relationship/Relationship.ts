module api.form.inputtype.content.relationship {

    export interface RelationshipConfig {

        relationshipType: {
            name: string
        };
    }

    export class Relationship extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<RelationshipConfig>;

        private input: api.form.Input;

        private comboBox: api.ui.combobox.ComboBox<api.content.ContentSummary>;

        private selectedOptionsView: RelationshipSelectedOptionsView;

        private contentSummaryLoader: api.form.inputtype.content.ContentSummaryLoader;

        private contentRequestsAllowed: boolean;

        constructor(config?: api.form.inputtype.InputTypeViewConfig<RelationshipConfig>) {
            super(true, "relationship");
            this.config = config;
            this.contentSummaryLoader = new api.form.inputtype.content.ContentSummaryLoader();
            this.contentSummaryLoader.addListener({
                onLoading: () => {
                    this.comboBox.setLabel("Searching...");
                },
                onLoaded: (contentSummaries: api.content.ContentSummary[]) => {
                    var options = this.createOptions(contentSummaries);
                    this.comboBox.setOptions(options);
                }
            });

            // requests aren't allowed until allowed contentTypes are specified
            this.contentRequestsAllowed = false;

            var name = new api.schema.relationshiptype.RelationshipTypeName("default");
            if (config.inputConfig.relationshipType.name != null) {
                name = new api.schema.relationshiptype.RelationshipTypeName(config.inputConfig.relationshipType.name);
            }
            new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(name).
                sendAndParse()
                .done((relationshipType: api.schema.relationshiptype.RelationshipType) => {
                    this.updateInputIcon(relationshipType);
                    this.contentSummaryLoader.setAllowedContentTypes(relationshipType.getAllowedToTypes());
                    this.contentRequestsAllowed = true;
                    this.loadOptions("");
                })
            ;
        }

        getHTMLElement(): HTMLElement {
            return super.getHTMLElement();
        }

        isManagingAdd(): boolean {
            return true;
        }

        addFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            throw new Error("Relationship manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            throw new Error("Relationship manages occurrences self");
        }

        maximumOccurrencesReached(): boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelected());
        }

        createAndAddOccurrence() {
            throw new Error("Relationship manages occurrences self");
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.input = input;

            this.selectedOptionsView = new RelationshipSelectedOptionsView();
            this.comboBox = this.createComboBox(input);

            if (properties != null) {
                properties.forEach((property: api.data.Property) => {
                    new api.content.GetContentByIdRequest(new api.content.ContentId(property.getString()))
                        .setExpand(api.content.ContentResourceRequest.EXPAND_SUMMARY)
                        .send()
                        .done((jsonResponse: api.rest.JsonResponse<api.content.json.ContentSummaryJson>) => {
                            var contentSummary = new api.content.ContentSummary(jsonResponse.getResult());
                            this.comboBox.selectOption({
                                value: contentSummary.getId(),
                                displayValue: contentSummary
                            });
                        });
                });
            }

            this.appendChild(this.comboBox);
            this.appendChild(this.selectedOptionsView);
        }

        private createComboBox(input: api.form.Input): api.ui.combobox.ComboBox<api.content.ContentSummary> {
            var comboboxConfig = <api.ui.combobox.ComboBoxConfig<api.content.ContentSummary>>{
                rowHeight: 50,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                hideComboBoxWhenMaxReached: true
            };
            var comboBox = new api.ui.combobox.ComboBox<api.content.ContentSummary>(input.getName(), comboboxConfig);

            this.loadOptions("");

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loadOptions(newValue);
                },
                onOptionSelected: null
            });

            return comboBox;
        }

        getValues(): api.data.Value[] {

            var values: api.data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option: api.ui.combobox.Option<api.content.ContentSummary>) => {
                var value = new api.data.Value(option.value, api.data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(validationRecorder: api.form.ValidationRecorder) {

            // TODO:
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (api.content.ContentId.isValidContentId(value.asString())) {
                return false;
            } else {
                return true;
            }
        }

        private updateInputIcon(relationshipType: api.schema.relationshiptype.RelationshipType) {

            this.comboBox.setInputIconUrl(relationshipType.getIconUrl());
        }

        private loadOptions(searchString: string) {
            if (!this.contentRequestsAllowed || !this.comboBox) {
                return;
            }

            this.contentSummaryLoader.search(searchString);
        }

        private createOptions(contents: api.content.ContentSummary[]): api.ui.combobox.Option<api.content.ContentSummary>[] {
            var options = [];
            contents.forEach((content: api.content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private optionFormatter(row: number, cell: number, content: api.content.ContentSummary, columnDef: any,
                                dataContext: api.ui.combobox.Option<api.content.ContentSummary>): string {
            var img = new api.dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(content.getIconUrl());

            var contentSummary = new api.dom.DivEl();
            contentSummary.setClass("content-summary");

            var displayName = new api.dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", content.getDisplayName());
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var path = new api.dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", content.getPath().toString());
            path.getEl().setInnerHtml(content.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }

    }

    api.form.input.InputTypeManager.register("Relationship", Relationship);
}