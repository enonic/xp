///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/mixin/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/mixin/json/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/content/form/module.ts' />

TestCase("Mixin", {

    "test mixin json": function () {

        var json = {
            "deletable": false,
            "displayName": "Demo: Address",
            "editable": false,
            "iconUrl": "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address",
            "items": [],
            "name": "address"
        };
        var mixinJson:api_schema_mixin_json.MixinJson = json;
        var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(mixinJson);

        assertEquals(mixin.getDisplayName(), "Demo: Address");
        assertEquals(mixin.getIcon(), "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address");
        assertEquals(mixin.getName(), "address");
        assertEquals(mixin.getSchemaKey(), "mixin:demo:address");
        assertEquals(mixin.getFormItems().length, 0);

    },

    "test mixin json with formItemSet": function () {
        var json = {
            "deletable": false,
            "displayName": "Demo: Address",
            "editable": false,
            "iconUrl": "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address",
            "items": [
                {
                    "customText": "custom text",
                    "formItemType": "FormItemSet",
                    "helpText": "help text",
                    "immutable": false,
                    "items": [
                        {
                            "customText": null,
                            "formItemType": "Input",
                            "helpText": null,
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
                        {
                            "customText": null,
                            "formItemType": "Input",
                            "helpText": null,
                            "immutable": false,
                            "indexed": true,
                            "inputType": {
                                "config": null,
                                "name": "TextLine"
                            },
                            "label": "Postal code",
                            "name": "postalCode",
                            "occurrences": {
                                "maximum": 1,
                                "minimum": 1
                            },
                            "validationRegexp": null
                        },
                        {
                            "customText": null,
                            "formItemType": "Input",
                            "helpText": null,
                            "immutable": false,
                            "indexed": true,
                            "inputType": {
                                "config": null,
                                "name": "TextLine"
                            },
                            "label": "Postal place",
                            "name": "postalPlace",
                            "occurrences": {
                                "maximum": 1,
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
                }
            ],
            "name": "address"
        };

        var mixinJson:api_schema_mixin_json.MixinJson = json;
        var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(mixinJson);

        var formItemSet:api_form.FormItemSet = <api_form.FormItemSet>mixin.getFormItems()[0];
        assertEquals(formItemSet.getName(), "address");
        assertEquals(formItemSet.getCustomText(), "custom text");
        assertEquals(formItemSet.getHelpText(), "help text");
        assertEquals(formItemSet.getLabel(), "Address");
    },

    "test mixin json with input": function () {
        var json = {
            "deletable": false,
            "displayName": "Demo: Address",
            "editable": false,
            "iconUrl": "http://localhost:8080/admin/rest/schema/image/Mixin:demo:address",
            "items": [
                {
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
                }
            ],
            "name": "address"
        };

        var mixinJson:api_schema_mixin_json.MixinJson = json;
        var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(mixinJson);

        var input:api_form.Input = <api_form.Input>mixin.getFormItems()[0];
        assertEquals(input.getName(), "street");
        assertEquals(input.getCustomText(), "custom text");
        assertEquals(input.getHelpText(), "help text");
        assertEquals(input.getLabel(), "Street");
    }
})