import QueryFields = api.query.QueryFields;
import QueryField = api.query.QueryField;

describe("api.query.QueryFields", () => {

    describe("toString", () => {

        it("single QueryField", () => {
            let queryFields = new QueryFields();
            queryFields.add(new QueryField("test"));
            expect(queryFields.toString()).toBe("test");
        });

        it("more than one queryField", () => {
            let queryFields = new QueryFields();
            queryFields.add(new QueryField("test1"));
            queryFields.add(new QueryField("test2"));
            queryFields.add(new QueryField("test3"));
            expect(queryFields.toString()).toBe("test1,test2,test3");
        });

        it("with weigth", () => {
            let queryFields = new QueryFields();
            queryFields.add(new QueryField("test1", 5));
            queryFields.add(new QueryField("test2"));
            expect(queryFields.toString()).toBe("test1^5,test2");
        });

    });

});
