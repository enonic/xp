module ContentSummaryAndCompareStatusSpec {

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummary = api.content.ContentSummary;
    import CompareStatus = api.content.CompareStatus;
    import UploadItem = api.ui.uploader.UploadItem;
    import FineUploaderFile = api.ui.uploader.FineUploaderFile;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;

    describe('api.content.ContentSummaryAndCompareStatus', function () {
        let contentSummary: ContentSummary;
        let uploadItem: UploadItem<ContentSummary>;

        beforeEach(() => {
            contentSummary = createContentSummary();
            uploadItem = createUploadItem();
        });

        describe('static methods', function () {

            it('create ContentSummaryAndCompareStatus from ContentSummary', function () {
                let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                    contentSummary);

                expect(contentSummaryAndCompareStatus.getContentSummary()).toBeDefined();
                expect(contentSummaryAndCompareStatus.getCompareStatus()).toBeUndefined();
                expect(contentSummaryAndCompareStatus.getUploadItem()).toBeUndefined();
            });

            it('create ContentSummaryAndCompareStatus from ContentSummary and CompareStatus', function () {
                let compareStatus: CompareStatus = CompareStatus.NEWER;
                let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus =
                    ContentSummaryAndCompareStatus.fromContentAndCompareStatus(contentSummary, compareStatus);

                expect(contentSummaryAndCompareStatus.getContentSummary()).toBeDefined();
                expect(contentSummaryAndCompareStatus.getCompareStatus()).toEqual(CompareStatus.NEWER);
                expect(contentSummaryAndCompareStatus.getUploadItem()).toBeUndefined();
            });

            it('create ContentSummaryAndCompareStatus from UploadItem', function () {
                let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                    uploadItem);

                expect(contentSummaryAndCompareStatus.getContentSummary()).toBeUndefined();
                expect(contentSummaryAndCompareStatus.getCompareStatus()).toBeUndefined();
                expect(contentSummaryAndCompareStatus.getUploadItem()).toBeDefined();
            });
        });

        describe('public methods', function () {

            let cntntSmmrAndCmprSttsFrmCntntSumm: ContentSummaryAndCompareStatus;
            let cntntSmmrAndCmprSttsFrmUpldItm: ContentSummaryAndCompareStatus;

            beforeEach(() => {
                cntntSmmrAndCmprSttsFrmCntntSumm = ContentSummaryAndCompareStatus.fromContentSummary(contentSummary);
                cntntSmmrAndCmprSttsFrmUpldItm = ContentSummaryAndCompareStatus.fromUploadItem(uploadItem);
            });

            describe('getContentId()', function () {
                it('returns contentId object if ContentSummary is set', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.getContentId()).toEqual(contentSummary.getContentId());
                });

                it('returns null if ContentSummary is not set', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.getContentId()).toBeNull();
                });
            });

            describe('getId()', function () {
                it('returns id of ContentSummary if set', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.getId()).toEqual(contentSummary.getId());
                });

                it('returns id of UploadItem if set', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.getId()).toEqual('1');
                });

                it('returns empty string if nothing is set', function () {
                    expect(new ContentSummaryAndCompareStatus().getId()).toEqual('');
                });
            });

            describe('setUploadItem()', function () {
                beforeEach(() => {
                    uploadItem.setModel(contentSummary);
                });

                it('sets ContentSummary on item uploaded', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.getContentSummary()).toEqual(contentSummary);
                });

                it('sets ContentSummary when setting UploadItem with already uploaded item', function () {
                    let contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem);

                    expect(contentSummaryAndCompareStatus.getContentSummary()).toEqual(contentSummary);
                });
            });

            describe('getPath()', function () {
                it('returns path of ContentSummary if set', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.getPath()).toEqual(contentSummary.getPath());
                });

                it('returns null if ContentSummary is not set', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.getPath()).toBeNull();
                });
            });

            describe('getType()', function () {
                it('returns type of ContentSummary if set', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.getType()).toEqual(contentSummary.getType());
                });

                it('returns null when no ContentSummary is set', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.getType()).toBeNull();
                });
            });

            describe('getDisplayName()', function () {
                it('returns displayName when ContentSummary is set', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.getDisplayName()).toEqual(
                        contentSummary.getDisplayName());
                });

                it('returns null when no ContentSummary is set', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.getDisplayName()).toBeNull();
                });
            });

            describe('getIconUrl()', function () {
                it('returns iconUrl of ContentSummary if set', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.getIconUrl()).toEqual(
                        contentSummary.getIconUrl());
                });

                it('returns null when no ContentSummary is set', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.getIconUrl()).toBeNull();
                });
            });

            describe('hasChildren()', function () {
                it('returns ContentSummary has children', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.hasChildren()).toEqual(
                        contentSummary.hasChildren());
                });

                it('returns false when no ContentSummary is set', function () {
                    expect(cntntSmmrAndCmprSttsFrmUpldItm.hasChildren()).toBeFalsy();
                });
            });

            describe('readonly status', function () {
                it('is false after create', function () {
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.isReadOnly()).toBeFalsy();
                });

                it('is set via setReadOnly method', function () {
                    cntntSmmrAndCmprSttsFrmCntntSumm.setReadOnly(true);
                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.isReadOnly).toBeTruthy();
                });
            });

            describe('equals()', function () {
                it('returns false for other type of object', function () {
                    spyOn(api.ObjectHelper, 'equals').and.callThrough();
                    spyOn(api.ObjectHelper, 'iFrameSafeInstanceOf').and.callThrough();

                    expect(cntntSmmrAndCmprSttsFrmCntntSumm.equals(createUploadItem())).toBeFalsy();
                    expect(api.ObjectHelper.equals).not.toHaveBeenCalled();
                    expect(api.ObjectHelper.iFrameSafeInstanceOf).toHaveBeenCalledTimes(1);
                });

                it('returns false when UploadItems are not equal', function () {
                    let uploadItem1: UploadItem<ContentSummary> = createUploadItem(<FineUploaderFile>{id: 1, name: 'name1'});
                    let uploadItem2: UploadItem<ContentSummary> = createUploadItem(<FineUploaderFile>{id: 2, name: 'name2'});
                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem2);

                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeFalsy();
                });

                it('returns false when ContentSummaries are not equal', function () {
                    let contentSummary1: ContentSummary = createContentSummary();
                    let contentSummary2: ContentSummary = new ContentSummaryBuilder(contentSummary1).setId('otherId').build();

                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                        contentSummary1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromContentSummary(
                        contentSummary2);

                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeFalsy();
                });

                it('returns false when CompareStatus are not equal', function () {
                    let compareStatus1: CompareStatus = CompareStatus.EQUAL;
                    let compareStatus2: CompareStatus = CompareStatus.MOVED;

                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus =
                        ContentSummaryAndCompareStatus.fromContentAndCompareStatus(contentSummary, compareStatus1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus =
                        ContentSummaryAndCompareStatus.fromContentAndCompareStatus(contentSummary, compareStatus2);

                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeFalsy();
                });

                it('returns true when ContentSummaries and CompareStatuses are equal', function () {
                    let compareStatus1: CompareStatus = CompareStatus.EQUAL;
                    let compareStatus2: CompareStatus = CompareStatus.EQUAL;

                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus =
                        ContentSummaryAndCompareStatus.fromContentAndCompareStatus(createContentSummary(), compareStatus1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus =
                        ContentSummaryAndCompareStatus.fromContentAndCompareStatus(createContentSummary(), compareStatus2);

                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeTruthy();
                });

                it('returns true when UploadItems are equal', function () {
                    let uploadItem1: UploadItem<ContentSummary> = createUploadItem(
                        <FineUploaderFile>{id: 1, name: 'name1', size: 1, uuid: 'uuid', status: 'ok', percent: 100});
                    let uploadItem2: UploadItem<ContentSummary> = createUploadItem(
                        <FineUploaderFile>{id: 1, name: 'name1', size: 1, uuid: 'uuid', status: 'ok', percent: 100});
                    let contentSummaryAndCompareStatus1: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem1);
                    let contentSummaryAndCompareStatus2: ContentSummaryAndCompareStatus = ContentSummaryAndCompareStatus.fromUploadItem(
                        uploadItem2);

                    expect(contentSummaryAndCompareStatus1.equals(contentSummaryAndCompareStatus2)).toBeTruthy();
                });
            });

        });
    });

    export function createContentSummary(): ContentSummary {
        let json: api.content.json.ContentSummaryJson = <api.content.json.ContentSummaryJson> {
            childOrder: {
                orderExpressions: [
                    {
                        FieldOrderExpr: {
                            direction: 'DESC',
                            fieldName: 'modifiedtime'
                        }
                    }
                ]
            },
            requireValid: false,
            contentState: 'DEFAULT',
            createdTime: '2016-11-08T11:10:52.239Z',
            creator: 'user:system:su',
            deletable: true,
            displayName: '444',
            editable: true,
            hasChildren: true,
            iconUrl: '/admin/rest/schema/content/icon/portal:site?hash=fbb03168ba310b23909201cdd2bf0c12',
            id: '39d79832-d5fe-4f75-945e-f0bbe5bdb156',
            isPage: true,
            isRoot: true,
            isValid: true,
            language: null,
            modifiedTime: '2016-11-17T13:24:04.835Z',
            modifier: 'user:system:su',
            name: '444',
            owner: 'user:system:su',
            path: '/444',
            publish: null,
            thumbnail: null,
            type: 'portal:site'
        };

        return ContentSummary.fromJson(json);
    }

    export function createUploadItem(file?: FineUploaderFile): UploadItem<ContentSummary> {
        if (file) {
            return new UploadItem<ContentSummary>(file);
        }

        return new UploadItem<ContentSummary>(<FineUploaderFile>{id: 1, name: 'name'});
    }
}
