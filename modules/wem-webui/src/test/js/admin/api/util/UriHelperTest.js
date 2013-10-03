describe("UriHelper", function () {

    it("create absolute url", function () {

        var url = api_util.getAbsoluteUri("a/path");
        expect(url).toBe("../../../a/path");

    });

});
