describe("api.query.QueryFieldsTest", function () {

    describe("toString", function () {

        it("single QueryField", function () {
            var queryFields = new api.query.QueryFields();
            queryFields.add(new api.query.QueryField("test"));
            expect(queryFields.toString()).toBe("test");
        });

        it("more than one queryField", function () {
            var queryFields = new api.query.QueryFields();
            queryFields.add(new api.query.QueryField("test1"));
            queryFields.add(new api.query.QueryField("test2"));
            queryFields.add(new api.query.QueryField("test3"));
            expect(queryFields.toString()).toBe("test1,test2,test3");
        });

        it("with weigth", function () {
            var queryFields = new api.query.QueryFields();
            queryFields.add(new api.query.QueryField("test1", 5));
            queryFields.add(new api.query.QueryField("test2"));
            expect(queryFields.toString()).toBe("test1^5,test2");
        });

    });

});