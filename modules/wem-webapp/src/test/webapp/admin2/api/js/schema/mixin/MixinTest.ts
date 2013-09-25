///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/mixin/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/mixin/json/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/content/form/module.ts' />

TestCase("MixinTest", {

    "test mixin json": function () {

        var json = {
            "deletable": false,
            "displayName": "Demo: Address Mixin",
            "editable": false,
            "formItemSet": {
                "customText": "custom text",
                "formItemType": "FormItemSet",
                "helpText": "help text",
                "immutable": false,
                "items": [
                    {
                        "customText": "",
                        "formItemType": "Input",
                        "helpText": "",
                        "immutable": false,
                        "indexed": true,
                        "inputType": {
                            "config": null,
                            "name": "TextLine"
                        },
                        "label": "Street",
                        "name": "street",
                        "occurrences": {
                            "maximum": 2,
                            "minimum": 0
                        },
                        "validationRegexp": null
                    }
                ],
                "label": "Address",
                "name": "address",
                "occurrences": {
                    "maximum": 0,
                    "minimum": 0
                }
            },
            "iconUrl": "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address",
            "input": null,
            "layout": null,
            "mixinReferenceJson": null,
            "module": "demo",
            "name": "address"
        };

        var mixinJson:api_schema_mixin_json.MixinJson = json;
        var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(mixinJson);

        assertEquals(mixin.getDisplayName(), "Demo: Address Mixin");
        assertEquals(mixin.getIcon(), "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address");
        assertEquals(mixin.getModuleName(), "demo");
        assertEquals(mixin.getName(), "address");
        assertEquals(mixin.getSchemaKey(), "mixin:demo:address");

    },

    "test mixin json with formItemSet": function () {
        var json = {
            "deletable": false,
            "displayName": "Demo: Address Mixin",
            "editable": false,
            "formItemSet": {
                "customText": "custom text",
                "formItemType": "FormItemSet",
                "helpText": "help text",
                "immutable": false,
                "items": [
                    {
                        "customText": "",
                        "formItemType": "Input",
                        "helpText": "",
                        "immutable": false,
                        "indexed": true,
                        "inputType": {
                            "config": null,
                            "name": "TextLine"
                        },
                        "label": "Street",
                        "name": "street",
                        "occurrences": {
                            "maximum": 2,
                            "minimum": 0
                        },
                        "validationRegexp": null
                    }
                ],
                "label": "Address",
                "name": "address",
                "occurrences": {
                    "maximum": 0,
                    "minimum": 0
                }
            },
            "iconUrl": "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address",
            "input": null,
            "layout": null,
            "mixinReferenceJson": null,
            "module": "demo",
            "name": "address"
        };

        var mixinJson:api_schema_mixin_json.MixinJson = json;
        var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(mixinJson);

        var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>mixin.getFormItem();
        assertEquals(formItemSet.getName(), "address");
        assertEquals(formItemSet.getCustomText(), "custom text");
        assertEquals(formItemSet.getHelpText(), "help text");
        assertEquals(formItemSet.getLabel(), "Address");
    },

    "test mixin json with input": function () {
        var json = {
            "deletable": false,
            "displayName": "Demo: Address Mixin",
            "editable": false,
            "input": {
                "customText": "custom text",
                "formItemType": "Input",
                "helpText": "help text",
                "immutable": false,
                "indexed": true,
                "inputType": {
                    "config": null,
                    "name": "TextLine"
                },
                "label": "Street",
                "name": "street",
                "occurrences": {
                    "maximum": 2,
                    "minimum": 0
                },
                "validationRegexp": null

            },
            "iconUrl": "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address",
            "layout": null,
            "formItemSet": null,
            "mixinReferenceJson": null,
            "module": "demo",
            "name": "address"
        };

        var mixinJson:api_schema_mixin_json.MixinJson = json;
        var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(mixinJson);

        var input:api_schema_content_form.Input = <api_schema_content_form.Input>mixin.getFormItem();
        assertEquals(input.getName(), "street");
        assertEquals(input.getCustomText(), "custom text");
        assertEquals(input.getHelpText(), "help text");
        assertEquals(input.getLabel(), "Street");
    }
})