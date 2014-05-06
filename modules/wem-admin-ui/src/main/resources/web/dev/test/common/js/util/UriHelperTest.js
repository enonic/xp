describe("Tests UriHelper functions", function () {

    window.CONFIG = {
        baseUri: 'http://localhost:8080/wem'
    };

    it("test getUri function", function () {
        var uri = api.util.getUri('a/b/c');
        expect(uri).toBe('http://localhost:8080/wem/a/b/c');
    });

    it("test getAdminUri function", function () {
        var uri = api.util.getAdminUri('a/b/c');
        expect(uri).toBe('http://localhost:8080/wem/admin/a/b/c');
    });

    it("test getRestUri function", function () {
        var uri = api.util.getRestUri('a/b/c');
        expect(uri).toBe('http://localhost:8080/wem/admin/rest/a/b/c');
    });

});

