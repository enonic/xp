module FormOptionSetSpec {

    import FormOptionSet = api.form.FormOptionSet;
    import FormOptionSetJson = api.form.json.FormOptionSetJson;
    import FormOptionSetOptionJson = api.form.json.FormOptionSetOptionJson;
    import FormItemTypeWrapperJson = api.form.json.FormItemTypeWrapperJson;
    import ContentId = api.content.ContentId;
    import FormOptionSetOption = api.form.FormOptionSetOption;

    describe('api.form.FormOptionSet', function () {

        let optionSet: FormOptionSet;

        beforeEach(function () {
            optionSet = createOptionSet(getOptionSetJson());
        });

        describe('constructor', function () {

            it('should correctly initialize label', function () {
                expect(optionSet.getLabel()).toEqual('Custom Option Set');
            });

            it('should correctly initialize help text', function () {
                expect(optionSet.getHelpText()).toEqual('Custom Help Text');
            });

            it('should correctly initialize expanded property', function () {
                expect(optionSet.isExpanded()).toBeTruthy();
            });

            it('should correctly initialize occurrences config', function () {
                expect(optionSet.getOccurrences().getMinimum()).toEqual(1);
                expect(optionSet.getOccurrences().getMaximum()).toEqual(7);
            });

            it('should correctly initialize multiselection config', function () {
                expect(optionSet.getMultiselection().getMinimum()).toEqual(1);
                expect(optionSet.getMultiselection().getMaximum()).toEqual(3);
            });
        });

        describe("what happens when option set doesn't have options", function () {

            it('should not initialize any form items', function () {
                expect(optionSet.getFormItems().length).toEqual(0);
            });

            it('should not initialize any options', function () {
                expect(optionSet.getOptions().length).toEqual(0);
            });
        });

        describe('what happens when option set has options', function () {
            let optionSetJson;
            let addSetOptionSpy;

            beforeEach(function () {
                addSetOptionSpy = spyOn(FormOptionSet.prototype, 'addSetOption').and.callThrough();
                optionSetJson = getOptionSetJsonWithOptions();
                optionSet = createOptionSet(optionSetJson);
            });

            it('should create correct number of options', function () {
                expect(addSetOptionSpy.calls.count()).toEqual(3);
                expect(optionSet.getOptions().length).toEqual(3);
            });

            it('each option should have correct label', function () {
                optionSet.getOptions().forEach(function (option: FormOptionSetOption, index: number) {
                    expect(option.getLabel()).toEqual(optionSetJson.options[index].label);
                });
            });

            it('each option should have correct name', function () {
                optionSet.getOptions().forEach(function (option: FormOptionSetOption, index: number) {
                    expect(option.getName()).toEqual(optionSetJson.options[index].name);
                });
            });

            it('should create a form item for each option', function () {
                expect(optionSet.getFormItems().length).toEqual(3);
            });

            it('items inside options must be created as form items', function () {
                optionSet.getOptions().forEach(function (option: FormOptionSetOption, index: number) {
                    if (optionSetJson.options[index].items) {
                        expect(option.getFormItems().length).toEqual(optionSetJson.options[index].items.length);
                    }
                });
            });

            it('empty options should not have any form items', function () {
                optionSet.getOptions().forEach(function (option: FormOptionSetOption, index: number) {
                    if (!optionSetJson.options[index].items) {
                        expect(option.getFormItems().length).toEqual(0);
                    }
                });
            });
        });

        describe('public classes', function () {

            describe('toFormOptionSetJson()', function () {
                let json: FormItemTypeWrapperJson;

                beforeEach(function () {
                    json = optionSet.toFormOptionSetJson();
                });

                it('should contain FormOptionSet object', function () {
                    expect(json.FormOptionSet).toBeDefined();
                });

                it('should correctly map name property', function () {
                    expect(json.FormOptionSet.name).toEqual(optionSet.getName());
                });

                it('should correctly map label property', function () {
                    expect(json.FormOptionSet.label).toEqual(optionSet.getLabel());
                });

                it('should correctly map expanded property', function () {
                    expect(json.FormOptionSet.expanded).toEqual(optionSet.isExpanded());
                });

                it('should correctly map helpText property', function () {
                    expect(json.FormOptionSet.helpText).toEqual(optionSet.getHelpText());
                });

                it('should correctly map occurrences object', function () {
                    expect(json.FormOptionSet.occurrences.minimum).toEqual(optionSet.getOccurrences().getMinimum());
                    expect(json.FormOptionSet.occurrences.maximum).toEqual(optionSet.getOccurrences().getMaximum());
                });

                it('should correctly map multiselection object', function () {
                    expect(json.FormOptionSet.multiselection.minimum).toEqual(optionSet.getMultiselection().getMinimum());
                    expect(json.FormOptionSet.multiselection.maximum).toEqual(optionSet.getMultiselection().getMaximum());
                });

                it('should correctly map options object', function () {
                    expect(json.FormOptionSet.options).toEqual(optionSet.getOptions());
                });
            });

            describe('equals()', function () {
                let optionSet2: FormOptionSet;
                let optionSetJson: FormOptionSetJson;

                beforeEach(function () {
                    optionSetJson = getOptionSetJson();
                });

                it('should return false when comparing to an object of different type', function () {
                    expect(optionSet.equals(new ContentId('test-id'))).toBeFalsy();
                });

                it('should return false when comparing to an object with a different name', function () {
                    optionSetJson.name = optionSet.getName() + ' new';
                    optionSet2 = createOptionSet(optionSetJson);

                    expect(optionSet.equals(optionSet2)).toBeFalsy();
                });

                it('should return false when comparing to an object with a different label', function () {
                    optionSetJson.label = optionSet.getLabel() + ' new';
                    optionSet2 = createOptionSet(optionSetJson);

                    expect(optionSet.equals(optionSet2)).toBeFalsy();
                });

                it('should return false when comparing to an object with a different help text', function () {
                    optionSetJson.label = optionSet.getHelpText() + ' new';
                    optionSet2 = createOptionSet(optionSetJson);

                    expect(optionSet.equals(optionSet2)).toBeFalsy();
                });

                it('should return false when comparing to an object with different expand value', function () {
                    optionSetJson.expanded = !optionSet.isExpanded();
                    optionSet2 = createOptionSet(optionSetJson);

                    expect(optionSet.equals(optionSet2)).toBeFalsy();
                });

                it('should return false when occurrence configs are different', function () {
                    optionSetJson.occurrences.minimum = optionSet.getOccurrences().getMinimum() + 1;
                    optionSet2 = createOptionSet(optionSetJson);

                    expect(optionSet.equals(optionSet2)).toBeFalsy();
                });

                it('should return false when multiselection configs are different', function () {
                    optionSetJson.multiselection.maximum = optionSet.getMultiselection().getMaximum() + 1;
                    optionSet2 = createOptionSet(optionSetJson);

                    expect(optionSet.equals(optionSet2)).toBeFalsy();
                });

                it('should return false when options are different', function () {
                    optionSet2 = createOptionSet(getOptionSetJsonWithOptions());

                    expect(optionSet.equals(optionSet2)).toBeFalsy();
                });

                it('should return true when comparing with an identical object', function () {
                    optionSet2 = createOptionSet(optionSetJson);
                    expect(optionSet.equals(optionSet2)).toBeTruthy();
                });
            });
        });

    });

    export function createOptionSet(json: FormOptionSetJson): FormOptionSet {
        return new FormOptionSet(json);
    }

    export function getOptionSetJsonWithOptions(expanded?: boolean): FormOptionSetJson {
        let json: FormOptionSetJson = getOptionSetJson(expanded);

        json.options = getOptionsJson();

        return json;
    }

    export function getOptionSetJson(expanded: boolean = true): FormOptionSetJson {
        return {
            name: 'optionSet',
            label: 'Custom Option Set',
            helpText: 'Custom Help Text',
            expanded: expanded,
            occurrences: {
                minimum: 1,
                maximum: 7
            },
            multiselection: {
                minimum: 1,
                maximum: 3
            },
            options: []
        };
    }

    export function getOptionsJson(): FormOptionSetOptionJson[] {
        return [
            {
                name: 'option1',
                label: 'Option 1',
                defaultOption: false,
                items: [{
                    Input: {
                        label: 'Option one input',
                        name: 'input1',
                        inputType: 'TextLine',
                        customText: 'Input one custom text',
                        helpText: 'Input one help text',
                        occurrences: {
                            minimum: 1,
                            maximum: 2
                        }
                    }
                }]
            }, {
                name: 'option2',
                label: 'Option 2',
                defaultOption: true,
                items: [{
                    FormItemSet: {
                        label: 'Option two item set',
                        name: 'itemSet1',
                        helpText: 'Option two fieldset help text',
                        occurrences: {
                            minimum: 1,
                            maximum: 1
                        },
                        items: [{
                            Input: {
                                label: 'Option two input one',
                                name: 'input2-1',
                                inputType: 'TextLine',
                                customText: 'Input two custom text',
                                helpText: 'Input two help text',
                                occurrences: {
                                    minimum: 1,
                                    maximum: 2
                                }
                            }
                        }, {
                            Input: {
                                label: 'Option two input two',
                                name: 'input2-2',
                                inputType: 'Checkbox',
                                customText: 'Checkbox two custom text',
                                helpText: 'Checkbox two help text',
                                occurrences: {
                                    minimum: 1,
                                    maximum: 2
                                }
                            }
                        }]
                    }
                }]
            }, {
                name: 'option3',
                label: 'Option 3'
            }
        ];
    }
}
