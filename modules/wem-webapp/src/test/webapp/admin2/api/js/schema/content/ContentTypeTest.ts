///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/util/UriHelper.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/notify/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/rest/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/content/form/json/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/content/form/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/content/json/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/content/module.ts' />

TestCase("ContentType", {

    "test getDisplayName when displayName field does not exist in json": function () {

        var formJson:api_form_json.FormItemJson[] = [
            {
                formItemType: "Input",
                name: "myRequiredInput",
                label: "My Required Input",
                occurrences: {
                    minimum: 1,
                    maximum: 1
                },
                customText: "Custom text",
                helpText: "Help text",
                immutable: true,
                indexed: true,
                validationRegexp: "regex",
                inputTypeConfig: {
                    someConfig: "someValue"
                },
                inputType: {
                    name: "myRequiredInput"
                }
            }
            ,
            {
                formItemType: "FormItemSet",
                name: "mySet",
                label: "My set",
                occurrences: {
                    minimum: 0,
                    maximum: 0
                },
                customText: "Custom text",
                helpText: "Custom text",
                immutable: true,
                items: [
                    {
                        formItemType: "Input",
                        name: "myInput",
                        label: "My Input",
                        occurrences: {
                            minimum: 0,
                            maximum: 1
                        },
                        customText: null,
                        helpText: null,
                        immutable: true,
                        indexed: true,
                        validationRegexp: null,
                        inputTypeConfig: null,
                        inputType: {
                            name: "myInput"
                        }
                    }
                ]
            }
        ];

        var json:api_schema_content_json.ContentTypeJson = <api_schema_content_json.ContentTypeJson>{
            name: "mytype",
            abstract: false,
            allowChildContent: true,
            contentDisplayNameScript: "myFunction();",
            displayName: "My Content type",
            final: false,
            iconUrl: "http://localhost/myicon.png",
            superType: "",
            createdTime: "2013-08-23T12:55:09.162Z",
            modifiedTime: "2013-08-23T13:55:09.162Z",
            owner: "user:system:root",
            modifier: "user:system:anonymous",
            form: formJson
        };
        var contentType:api_schema_content.ContentType = new api_schema_content.ContentType(json);

        assertEquals("mytype", contentType.getName());
        assertEquals("My Content type", contentType.getDisplayName());
        assertEquals("myFunction();", contentType.getContentDisplayNameScript());
        assertEquals("http://localhost/myicon.png", contentType.getIconUrl());
        assertEquals(false, contentType.isAbstract());
        assertEquals(false, contentType.isFinal());
        assertEquals(true, contentType.isAllowChildContent());
        assertEquals("2013-08-23T12:55:09.162Z", contentType.getCreatedTime().toISOString());
        assertEquals("2013-08-23T13:55:09.162Z", contentType.getModifiedTime().toISOString());
        assertEquals("user:system:root", contentType.getOwner());
        assertEquals("user:system:anonymous", contentType.getModifier());

        var form:api_form.Form = contentType.getForm();
        var myRequiredInput = form.getInputByName("myRequiredInput")
        assertEquals("My Required Input", myRequiredInput.getLabel());
        assertEquals(1, myRequiredInput.getOccurrences().getMinimum());
        assertEquals(1, myRequiredInput.getOccurrences().getMaximum());
        assertEquals("Custom text", myRequiredInput.getCustomText());
        assertEquals("Help text", myRequiredInput.getHelpText());
    }
});

