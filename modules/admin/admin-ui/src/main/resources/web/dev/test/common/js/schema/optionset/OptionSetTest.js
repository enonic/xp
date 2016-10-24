describe("api.schema.optionset.OptionSetTest", function () {

    var FormOptionSetOption = api.form.FormOptionSetOption;
    var Input = api.form.Input;

    describe("checkbox option set", function () {

        var checkboxSet = createOptionSet('Expanded Checkbox Option Set', true, 1, 3);

        verifyFormItems(checkboxSet);

        it("should be expanded", function () {
            expect(checkboxSet.isExpanded()).toBeTruthy();
        });

        it("should have a label", function () {
            expect(checkboxSet.getLabel()).toEqual('Expanded Checkbox Option Set');
        });

        it("should have a help text", function () {
            expect(checkboxSet.getHelpText()).toEqual('Option set help text');
        });

        verifyItemViews(checkboxSet, true, false);

    });

    describe("radio option set", function () {

        var radioSet = createOptionSet('Collapsed Radio Option Set', false, 1, 1);

        verifyFormItems(radioSet);

        it("should be collapsed", function () {
            expect(radioSet.isExpanded()).toBeFalsy();
        });

        it("should have a label", function () {
            expect(radioSet.getLabel()).toEqual('Collapsed Radio Option Set');
        });

        it("should have a help text", function () {
            expect(radioSet.getHelpText()).toEqual('Option set help text');
        });

        verifyItemViews(radioSet, false, true);

    });

    function createOptionSet(title, expanded, min, max) {
        return new api.form.FormOptionSet({
            name: 'optionSet1',
            label: title,
            helpText: 'Option set help text',
            expanded: expanded,
            occurrences: {
                minimum: 1,
                maximum: 3
            },
            // 1-1 for radio
            // any other for checkbox
            multiselection: {
                minimum: min,
                maximum: max
            },
            options: [
                {
                    name: 'option1',
                    label: 'Option 1',
                    defaultOption: false,
                    items: [{
                        Input: {
                            name: 'input1',
                            inputType: 'TextLine',
                            config: {},
                            customText: 'Input one custom text',
                            helpText: 'Input one help text',
                            occurrences: {
                                minimum: 1,
                                maximum: 2
                            }
                        }
                    }]
                },
                {
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
                            items: [
                                {
                                    Input: {
                                        name: 'input2-1',
                                        inputType: 'TextLine',
                                        customText: 'Input two custom text',
                                        helpText: 'Input two help text',
                                        occurrences: {
                                            minimum: 1,
                                            maximum: 2
                                        }
                                    }
                                },
                                {
                                    Input: {
                                        name: 'input2-2',
                                        inputType: 'Checkbox',
                                        customText: 'Checkbox two custom text',
                                        helpText: 'Checkbox two help text',
                                        occurrences: {
                                            minimum: 1,
                                            maximum: 2
                                        }
                                    }
                                }
                            ]
                        }
                    }]
                },
                {
                    name: 'option3',
                    label: 'Option 3'
                }
            ]
        });
    }

    function createPropertySet() {
        var tree = new api.data.PropertyTree();
        var set = tree.addPropertySet('optionSet1');

        var optionSet1 = set.addPropertySet("option1");
        optionSet1.addString("input1", "Option 1 value from data");

        var optionSet2 = set.addPropertySet("option2");
        var itemSet1 = optionSet2.addPropertySet('itemSet1');
        itemSet1.addString("input2-1", "Option 2 value from data");
        itemSet1.addBoolean("input2-2", true);

        return tree.getRoot();
    }

    function verifyFormItems(optionSet) {
        it("should initialize form items properly", function () {

            var items = optionSet.getFormItems();
            expect(items.length).toEqual(3);

            var option1 = items[0];
            expect(option1.getName()).toEqual('option1');
            expect(option1.getLabel()).toEqual('Option 1');
            expect(option1 instanceof FormOptionSetOption).toBeTruthy();

            var option1Items = option1.getFormItems();
            expect(option1Items.length).toEqual(1);

            var option1Input = option1Items[0];
            expect(option1Input instanceof Input).toBeTruthy();
            expect(option1Input.getName()).toEqual('input1');
            expect(option1Input.getHelpText()).toEqual('Input one help text');

            var option2 = items[1];
            expect(option2.getName()).toEqual('option2');
            expect(option2.getLabel()).toEqual('Option 2');
            expect(option2 instanceof FormOptionSetOption).toBeTruthy();

            var option2Items = option2.getFormItems();
            expect(option2Items.length).toEqual(1);
            expect(option2Items[0] instanceof api.form.FormItemSet).toBeTruthy();

            var formItemSetItems = option2Items[0].getFormItems();
            expect(formItemSetItems.length).toEqual(2);

            var option2Input = formItemSetItems[0];
            expect(option2Input instanceof Input).toBeTruthy();
            expect(option2Input.getName()).toEqual('input2-1');
            expect(option2Input.getHelpText()).toEqual('Input two help text');

            var option2Checkbox = formItemSetItems[1];
            expect(option2Checkbox instanceof Input).toBeTruthy();
            expect(option2Checkbox.getName()).toEqual('input2-2');
            expect(option2Checkbox.getHelpText()).toEqual('Checkbox two help text');

            var option3 = items[2];
            expect(option3.getName()).toEqual('option3');
            expect(option3.getLabel()).toEqual('Option 3');
            expect(option3 instanceof FormOptionSetOption).toBeTruthy();
            expect(option3.getFormItems().length).toEqual(0);
        });
    }

    function verifyItemViews(optionSet, setExpanded, isRadio) {

        var propertySet = createPropertySet();

        var view = new api.form.FormOptionSetView({
            context: api.form.FormContext.create().setShowEmptyFormItemSetOccurrences(true).build(),
            formOptionSet: optionSet,
            parentDataSet: propertySet
        });

        it('should render item views properly', function (done) {

            view.layout().then(function () {

                // Need to append it do DOM to do click on the element
                var testDiv = document.createElement('div');
                testDiv.style.display = 'none';
                testDiv.appendChild(view.getHTMLElement());
                document.body.appendChild(testDiv);

                var option1, option2, option3, text1Id, text2Id, check2Id, isExpanded;

                if (isRadio) {
                    option1 = view.findChildById('api.ui.RadioButton-1', true);
                    option2 = view.findChildById('api.ui.RadioButton-2', true);
                    option3 = view.findChildById('api.ui.RadioButton', true);
                    text1Id = 'api.ui.text.TextInput-2';
                    text2Id = 'api.ui.text.TextInput-3';
                    check2Id = 'api.ui.Checkbox-4';
                    isExpanded = false;
                } else {
                    option1 = view.findChildById('api.ui.Checkbox-2', true);
                    option2 = view.findChildById('api.ui.Checkbox-3', true);
                    option3 = view.findChildById('api.ui.Checkbox-1', true);
                    text1Id = 'api.ui.text.TextInput';
                    text2Id = 'api.ui.text.TextInput-1';
                    check2Id = 'api.ui.Checkbox';
                    isExpanded = true;
                }

                expect(option1).toBeTruthy();
                expect(option1.getLabel()).toEqual('Option 1');
                expect(option1.isChecked()).toBeFalsy();

                var option1View = option1.getParentElement();
                expect(option1View.getEl().hasClass('expanded')).toEqual(isExpanded);

                var option1ItemViews = option1View.getLastChild().getChildren();
                expect(option1ItemViews.length).toEqual(1);
                var radio1TextInput = option1ItemViews[0].findChildById(text1Id, true);
                expect(radio1TextInput).toBeTruthy();
                expect(radio1TextInput.getValue()).toEqual('Option 1 value from data');

                expect(option2).toBeTruthy();
                expect(option2.getLabel()).toEqual('Option 2');
                expect(option2.isChecked()).toBeTruthy();

                var option2View = option2.getParentElement();
                // defaultOption is always expanded no matter if set is expanded or not
                expect(option2View.getEl().hasClass('expanded')).toEqual(true);
                var option2FormItemView = option2View.getLastChild().getChildren();
                expect(option2FormItemView.length).toEqual(1);
                var radio2TextInput = option2FormItemView[0].findChildById(text2Id, true);
                expect(radio2TextInput).toBeTruthy();
                expect(radio2TextInput.getValue()).toEqual('Option 2 value from data');
                var radio2Checkbox = option2FormItemView[0].findChildById(check2Id, true);
                expect(radio2Checkbox).toBeTruthy();
                expect(radio2Checkbox.isChecked()).toBeTruthy();

                expect(option3).toBeTruthy();
                expect(option3.getLabel()).toEqual('Option 3');
                expect(option3.isChecked()).toBeFalsy();

                var option3View = option3.getParentElement();
                expect(option3View.getEl().hasClass('expanded')).toEqual(isExpanded);
                var option3ItemViews = option3View.getLastChild().getChildren();
                expect(option3ItemViews.length).toEqual(0);

                // Now click first option to see that others behave correctly
                var option1CollapseButton = option1View.getChildren()[0].getChildren()[0];
                option1CollapseButton.getHTMLElement().click();
                // Clicked option changes state
                expect(option1View.getEl().hasClass('expanded')).toEqual(setExpanded || !isExpanded);
                // Previously selected changes state if radio only
                expect(option2View.getEl().hasClass('expanded')).toEqual(setExpanded || !isRadio);
                // Previously unselected doesn't change state
                expect(option3View.getEl().hasClass('expanded')).toEqual(setExpanded || isExpanded);


                document.body.removeChild(testDiv);
                done();
            });

        });
    }


});