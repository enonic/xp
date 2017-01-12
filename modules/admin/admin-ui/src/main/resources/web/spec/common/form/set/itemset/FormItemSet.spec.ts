module FormItemSetSpec {

    import FormItemSet = api.form.FormItemSet;
    import FormItemSetJson = api.form.json.FormItemSetJson;
    import FormItemTypeWrapperJson = api.form.json.FormItemTypeWrapperJson;
    import ContentId = api.content.ContentId;

    describe("api.form.FormItemSet", function () {

        let itemSet: FormItemSet;

        beforeEach(function () {
            itemSet = createItemSet(getItemSetJson());
        });

        describe("constructor", function () {

            it("should correctly initialize label", function () {
                expect(itemSet.getLabel()).toEqual("Custom Item Set");
            });

            it("should correctly initialize help text", function () {
                expect(itemSet.getHelpText()).toEqual("Custom Help Text");
            });

            it("should correctly initialize custom text", function () {
                expect(itemSet.getCustomText()).toEqual("Custom text");
            });

            it("should correctly initialize immutable property", function () {
                expect(itemSet.isImmutable()).toBeFalsy();
            });

            it("should correctly initialize occurrences config", function () {
                expect(itemSet.getOccurrences().getMinimum()).toEqual(5);
                expect(itemSet.getOccurrences().getMaximum()).toEqual(7);
            });
        });

        describe("what happens when item set doesn't have items", function () {

            it("should not initialize any form items", function () {
                expect(itemSet.getFormItems().length).toEqual(0);
            });
        });

        describe("what happens when item set has items", function () {
            let itemSetJson;
            let addSetItemSpy;

            beforeEach(function () {
                addSetItemSpy = spyOn(FormItemSet.prototype, 'addFormItem').and.callThrough();
                itemSetJson = getItemSetJsonWithItems();
                itemSet = createItemSet(itemSetJson);
            });

            it("should create correct number of items", function () {
                expect(addSetItemSpy.calls.count()).toEqual(4); // inner set also has 2 items
            });

            it("should create a form item for each item", function () {
                expect(itemSet.getFormItems().length).toEqual(2);
            });

            it("each item should have correct label", function () {
                expect((<any>itemSet.getFormItems()[0]).getLabel()).toEqual("Itemset input");
                expect((<any>itemSet.getFormItems()[1]).getLabel()).toEqual("Level 2 item set");
            });

            it("each item should have correct name", function () {
                expect(itemSet.getFormItems()[0].getName()).toEqual("input1-1");
                expect(itemSet.getFormItems()[1].getName()).toEqual("itemSet lvl 2");
            });

        });

        describe("public classes", function () {

            describe("toFormItemSetJson()", function () {
                let json: FormItemTypeWrapperJson;

                beforeEach(function () {
                    json = itemSet.toFormItemSetJson();
                });

                it("should contain FormItemSet object", function () {
                    expect(json.FormItemSet).toBeDefined();
                });

                it("should correctly map name property", function () {
                    expect(json.FormItemSet.name).toEqual(itemSet.getName());
                });

                it("should correctly map label property", function () {
                    expect(json.FormItemSet.label).toEqual(itemSet.getLabel());
                });

                it("should correctly map immutable property", function () {
                    expect(json.FormItemSet.immutable).toEqual(itemSet.isImmutable());
                });

                it("should correctly map customText property", function () {
                    expect(json.FormItemSet.customText).toEqual(itemSet.getCustomText());
                });

                it("should correctly map helpText property", function () {
                    expect(json.FormItemSet.helpText).toEqual(itemSet.getHelpText());
                });

                it("should correctly map occurrences object", function () {
                    expect(json.FormItemSet.occurrences.minimum).toEqual(itemSet.getOccurrences().getMinimum());
                    expect(json.FormItemSet.occurrences.maximum).toEqual(itemSet.getOccurrences().getMaximum());
                });

                it("should correctly map items object", function () {
                    expect(json.FormItemSet.items).toEqual(itemSet.getFormItems());
                });
            });

            describe("equals()", function () {
                let itemSet2: FormItemSet;
                let itemSetJson: FormItemSetJson;

                beforeEach(function () {
                    itemSetJson = getItemSetJson();
                });

                it("should return false when comparing to an object of different type", function () {
                    expect(itemSet.equals(new ContentId('test-id'))).toBeFalsy();
                });

                it("should return false when comparing to an object with a different name", function () {
                    itemSetJson.name = itemSet.getName() + " new";
                    itemSet2 = createItemSet(itemSetJson);

                    expect(itemSet.equals(itemSet2)).toBeFalsy();
                });

                it("should return false when comparing to an object with a different label", function () {
                    itemSetJson.label = itemSet.getLabel() + " new";
                    itemSet2 = createItemSet(itemSetJson);

                    expect(itemSet.equals(itemSet2)).toBeFalsy();
                });

                it("should return false when comparing to an object with a different help text", function () {
                    itemSetJson.helpText = itemSet.getHelpText() + " new";
                    itemSet2 = createItemSet(itemSetJson);

                    expect(itemSet.equals(itemSet2)).toBeFalsy();
                });

                it("should return false when comparing to an object with different immutable value", function () {
                    itemSetJson.immutable = !itemSet.isImmutable();
                    itemSet2 = createItemSet(itemSetJson);

                    expect(itemSet.equals(itemSet2)).toBeFalsy();
                });

                it("should return false when occurrence configs are different", function () {
                    itemSetJson.occurrences.minimum = itemSet.getOccurrences().getMinimum() + 1;
                    itemSet2 = createItemSet(itemSetJson);

                    expect(itemSet.equals(itemSet2)).toBeFalsy();
                });

                it("should return false when items are different", function () {
                    itemSet2 = createItemSet(getItemSetJsonWithItems());

                    expect(itemSet.equals(itemSet2)).toBeFalsy();
                });

                it("should return true when comparing with an identical object", function () {
                    itemSet2 = createItemSet(itemSetJson);
                    expect(itemSet.equals(itemSet2)).toBeTruthy();
                });
            });
        });

    });

    export function createItemSet(json: FormItemSetJson): FormItemSet {
        return new FormItemSet(json);
    }

    export function getItemSetJsonWithItems(): FormItemSetJson {
        let json: FormItemSetJson = getItemSetJson();

        json.items = getItemSetItemsJson();

        return json;
    }

    export function getItemSetJson(): FormItemSetJson {
        return {
            name: 'itemSet',
            label: 'Custom Item Set',
            helpText: 'Custom Help Text',
            immutable: false,
            customText: "Custom text",
            occurrences: {
                minimum: 5,
                maximum: 7
            },
            items: []
        };
    }

    export function getItemSetItemsJson(): FormItemTypeWrapperJson[] {
        return [
            {
                Input: {
                    label: 'Itemset input',
                    name: 'input1-1',
                    inputType: 'TextLine',
                    customText: 'Input one custom text',
                    helpText: 'Input one help text',
                    occurrences: {
                        minimum: 1,
                        maximum: 2
                    }
                }
            },
            {
                FormItemSet: {
                    label: 'Level 2 item set',
                    name: 'itemSet lvl 2',
                    helpText: 'Itemset help text',
                    occurrences: {
                        minimum: 1,
                        maximum: 1
                    },
                    items: [{
                        Input: {
                            label: 'Itemset input one',
                            name: 'input2-1',
                            inputType: 'TextLine',
                            customText: 'Input one custom text',
                            helpText: 'Input one help text',
                            occurrences: {
                                minimum: 1,
                                maximum: 2
                            }
                        }
                    }, {
                        Input: {
                            label: 'Itemset input two',
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
            }
        ];
    }

}
