module ContentSummaryAndCompareStatusSpec {

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummary = api.content.ContentSummary;
    import CompareStatus = api.content.CompareStatus;
    import UploadItem = api.ui.uploader.UploadItem;
    import FineUploaderFile = api.ui.uploader.FineUploaderFile;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;

    describe("api.content.ContentSummaryAndCompareStatus", function () {
        let contentSummary: ContentSummary;

        beforeEach(() => {
            contentSummary = createContentSummary();
        });

        describe("static methods", function () {


            it("creates ContentSummaryAndCompareStatus from ContentSummary", function () {
                let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                    contentSummary);

                expect(contentSummaryAndCompareStatus.getContentSummary()).toBeDefined();
                expect(contentSummaryAndCompareStatus.getCompareStatus()).toBeUndefined();
                expect(contentSummaryAndCompareStatus.getUploadItem()).toBeUndefined();
            });

            it("creates ContentSummaryAndCompareStatus from ContentSummary and CompareStatus", function () {
                let compareStatus: CompareStatus = CompareStatus.NEWER;
                let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentAndCompareStatus(
                    contentSummary, compareStatus);

                expect(contentSummaryAndCompareStatus.getContentSummary()).toBeDefined();
                expect(contentSummaryAndCompareStatus.getCompareStatus()).toEqual(CompareStatus.NEWER);
                expect(contentSummaryAndCompareStatus.getUploadItem()).toBeUndefined();
            });

            it("creates ContentSummaryAndCompareStatus from UploadItem", function () {
                let uploadItem: UploadItem<ContentSummary> = new UploadItem<ContentSummary>(<FineUploaderFile>{name: "name"});
                let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                    uploadItem);

                expect(contentSummaryAndCompareStatus.getContentSummary()).toBeUndefined();
                expect(contentSummaryAndCompareStatus.getCompareStatus()).toBeUndefined();
                expect(contentSummaryAndCompareStatus.getUploadItem()).toBeDefined();
            });
        });

        describe("public methods", function () {

            describe("getContentId()", function () {
                it("returns contentId object if ContentSummary is set", function () {
                    let uploadItem: UploadItem<ContentSummary> = createUploadItem();
                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                        contentSummary);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem);

                    expect(contentSummaryAndCompareStatus1.getContentId()).toEqual(contentSummary.getContentId());
                    expect(contentSummaryAndCompareStatus2.getContentId()).toBeNull();
                });
            });

            describe("getId()", function () {
                it("returns id of ContentSummary if set", function () {
                    let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                        contentSummary);

                    expect(contentSummaryAndCompareStatus.getId()).toEqual(contentSummary.getId());
                });

                it("returns id of UploadItem if set", function () {
                    let uploadItem: UploadItem<ContentSummary> = createUploadItem();
                    let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem);

                    expect(contentSummaryAndCompareStatus.getId()).toEqual("id");
                });
            });

            describe("setUploadItem()", function () {
                it("sets ContentSummary on item uploaded", function () {
                    let uploadItem: UploadItem<ContentSummary> = createUploadItem();
                    let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem);

                    expect(contentSummaryAndCompareStatus.getContentSummary()).toBeUndefined();

                    uploadItem.setModel(contentSummary);

                    expect(contentSummaryAndCompareStatus.getContentSummary()).toEqual(contentSummary);
                });

                it("sets ContentSummary when setting UploadItem with already uploaded item", function () {
                    let uploadItem: UploadItem<ContentSummary> = createUploadItem();
                    uploadItem.setModel(contentSummary);
                    let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem);

                    expect(contentSummaryAndCompareStatus.getContentSummary()).toEqual(contentSummary);
                });
            });

            describe("getPath()", function () {
                it("returns path of ContentSummary if set", function () {
                    expect(ContentSummaryAndCompareStatus.fromContentSummary(contentSummary).getPath()).toEqual(contentSummary.getPath());
                    expect(ContentSummaryAndCompareStatus.fromUploadItem(createUploadItem()).getPath()).toBeNull();
                })
            });

            describe("getType()", function () {
                it("returns type of ContentSummary if set", function () {
                    expect(ContentSummaryAndCompareStatus.fromContentSummary(contentSummary).getType()).toEqual(contentSummary.getType());
                    expect(ContentSummaryAndCompareStatus.fromUploadItem(createUploadItem()).getType()).toBeNull();
                })
            });

            describe("getDisplayName()", function () {
                it("returns displayName of ContentSummary if set", function () {
                    expect(ContentSummaryAndCompareStatus.fromContentSummary(contentSummary).getDisplayName()).toEqual(
                        contentSummary.getDisplayName());
                    expect(ContentSummaryAndCompareStatus.fromUploadItem(createUploadItem()).getDisplayName()).toBeNull();
                })
            });

            describe("getIconUrl()", function () {
                it("returns iconUrl of ContentSummary if set", function () {
                    expect(ContentSummaryAndCompareStatus.fromContentSummary(contentSummary).getIconUrl()).toEqual(
                        contentSummary.getIconUrl());
                    expect(ContentSummaryAndCompareStatus.fromUploadItem(createUploadItem()).getIconUrl()).toBeNull();
                })
            });

            describe("hasChildren()", function () {
                it("returns ContentSummary has children", function () {
                    expect(ContentSummaryAndCompareStatus.fromContentSummary(contentSummary).hasChildren()).toEqual(
                        contentSummary.hasChildren());
                    expect(ContentSummaryAndCompareStatus.fromUploadItem(createUploadItem()).hasChildren()).toBeFalsy();
                })
            });

            describe("readonly status", function () {
                it("sets readonly", function () {
                    let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                        contentSummary);
                    expect(contentSummaryAndCompareStatus.isReadOnly()).toBeFalsy();
                    contentSummaryAndCompareStatus.setReadOnly(true);
                    expect(contentSummaryAndCompareStatus.isReadOnly()).toBeTruthy();
                })
            });

            describe("equals()", function () {
                let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                    contentSummary);

                it("returns false for other type of object", function () {
                    spyOn(api.ObjectHelper, "equals").and.callThrough();
                    spyOn(api.ObjectHelper, "iFrameSafeInstanceOf").and.callThrough();

                    expect(contentSummaryAndCompareStatus.equals(createUploadItem())).toBeFalsy();
                    expect(api.ObjectHelper.equals).not.toHaveBeenCalled();
                    expect(api.ObjectHelper.iFrameSafeInstanceOf).toHaveBeenCalledTimes(1);
                });

                it("returns false when UploadItems are not equal", function () {
                    let uploadItem1: UploadItem<ContentSummary> = createUploadItem(<FineUploaderFile>{id: "id1", name: "name1"});
                    let uploadItem2: UploadItem<ContentSummary> = createUploadItem(<FineUploaderFile>{id: "id2", name: "name2"});
                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem2);

                    expect(uploadItem1.equals(uploadItem2)).toBeFalsy();
                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeFalsy();
                });

                it("returns false when ContentSummaries are not equal", function () {
                    let contentSummary1: ContentSummary = createContentSummary();
                    let contentSummary2: ContentSummary = new ContentSummaryBuilder(contentSummary1).setId("otherId").build();

                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                        contentSummary1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                        contentSummary2);

                    expect(contentSummary1.equals(contentSummary2)).toBeFalsy();
                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeFalsy();
                });

                it("returns false when CompareStatus are not equal", function () {
                    let compareStatus1: CompareStatus = CompareStatus.EQUAL;
                    let compareStatus2: CompareStatus = CompareStatus.MOVED;

                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentAndCompareStatus(
                        contentSummary, compareStatus1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentAndCompareStatus(
                        contentSummary, compareStatus2);

                    expect(compareStatus1).not.toEqual(compareStatus2);
                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeFalsy();
                });

                it("returns true when ContentSummaries and CompareStatuses are equal", function () {
                    let compareStatus1: CompareStatus = CompareStatus.EQUAL;
                    let compareStatus2: CompareStatus = CompareStatus.EQUAL;

                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentAndCompareStatus(
                        createContentSummary(), compareStatus1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentAndCompareStatus(
                        createContentSummary(), compareStatus2);

                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeTruthy();
                });

                it("returns true when UploadItems are equal", function () {
                    let uploadItem1: UploadItem<ContentSummary> = createUploadItem(
                        <FineUploaderFile>{id: "id1", name: "name1", size: 1, uuid: "uuid", status: "ok", percent: 100});
                    let uploadItem2: UploadItem<ContentSummary> = createUploadItem(
                        <FineUploaderFile>{id: "id1", name: "name1", size: 1, uuid: "uuid", status: "ok", percent: 100});
                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem2);

                    expect(uploadItem1.equals(uploadItem2)).toBeTruthy();
                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeTruthy();
                });
            });

        });
    });

    export function createContentSummary(): ContentSummary {
        let json: api.content.json.ContentSummaryJson = <api.content.json.ContentSummaryJson> {
            "childOrder": {
                "orderExpressions": [
                    {
                        "FieldOrderExpr": {
                            "direction": "DESC",
                            "fieldName": "modifiedtime"
                        }
                    }
                ]
            },
            "requireValid": false,
            "contentState": "DEFAULT",
            "createdTime": "2016-11-08T11:10:52.239Z",
            "creator": "user:system:su",
            "deletable": true,
            "displayName": "444",
            "editable": true,
            "hasChildren": true,
            "iconUrl": "/admin/rest/schema/content/icon/portal:site?hash=fbb03168ba310b23909201cdd2bf0c12",
            "id": "39d79832-d5fe-4f75-945e-f0bbe5bdb156",
            "isPage": true,
            "isRoot": true,
            "isValid": true,
            "language": null,
            "modifiedTime": "2016-11-17T13:24:04.835Z",
            "modifier": "user:system:su",
            "name": "444",
            "owner": "user:system:su",
            "path": "/444",
            "publish": null,
            "thumbnail": null,
            "type": "portal:site"
        }

        return ContentSummary.fromJson(json);
    }

    export function createUploadItem(file?: FineUploaderFile): UploadItem<ContentSummary> {
        if (file) {
            return new UploadItem<ContentSummary>(file);
        }

        return new UploadItem<ContentSummary>(<FineUploaderFile>{id: "id", name: "name"});
    }
}
