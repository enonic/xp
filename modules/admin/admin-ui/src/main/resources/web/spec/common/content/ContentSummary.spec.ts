module ContentSummarySpec {

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ChildOrderJson = api.content.json.ChildOrderJson;
    import ChildOrder = api.content.order.ChildOrder;

    describe("api.content.ContentSummary", function () {

        let content: ContentSummary;

        beforeEach(function () {
            content = createContentSummary(getDefaultContentSummaryJson());
        });

        describe("constructor", function () {

            const json: ContentSummaryJson = getDefaultContentSummaryJson();

            it("should initialize properly", function () {
                expect(content).not.toBeNull();
            });

            it("should two instances be equals", function () {
                expect(content.equals(ContentSummary.fromJson(json))).toBeTruthy();
            });
        });
    });

    export function createContentSummary(json: ContentSummaryJson): ContentSummary {
        return new ContentSummaryBuilder().fromContentSummaryJson(json).build();
    }

    export function getDefaultContentSummaryJson() {
        const childOrder = new ChildOrder();
        childOrder.addOrderExpr(OrderExprSpec.getOrderExpr());

        return <ContentSummaryJson> {
            childOrder: childOrder.toJson(),
            requireValid: false,
            contentState: "DEFAULT",
            createdTime: "2013-08-23T12:55:09.162Z",
            creator: "user:system:admin",
            deletable: true,
            displayName: "My Content",
            editable: true,
            hasChildren: false,
            iconUrl: "/admin/rest/schema/content/icon/myapplication:my_type?hash=f95b70fdc3088560732a5ac135644506",
            id: "aaa",
            isPage: false,
            isRoot: true,
            isValid: true,
            language: "en",
            modifiedTime: "2013-08-23T12:55:09.162Z",
            modifier: "user:system:admin",
            name: "my_a_content",
            owner: "user:myStore:me",
            path: "/my_a_content",
            publish: {
                from: "2016-11-02T10:36:00Z",
                to: "2017-11-02T10:36:00Z",
            },
            thumbnail: null,
            type: "myapplication:my_type"
        };
    }
}
