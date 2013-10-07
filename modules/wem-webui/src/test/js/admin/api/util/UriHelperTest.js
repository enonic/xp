describe("UriHelper", function () {

    it("getUri", function () {

        var url = api_util.getUri("a/path");
        expect(url).toBe("../../../a/path");

    });

    it("getAdminUri", function () {

        var url = api_util.getAdminUri("a/path");
        expect(url).toBe("../../../admin/a/path");

    });

    it("getRestUri", function () {

        var url = api_util.getRestUri("a/path");
        expect(url).toBe("../../../admin/rest/a/path");

    });

});
