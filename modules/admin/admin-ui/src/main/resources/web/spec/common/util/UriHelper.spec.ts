import UriHelper = api.util.UriHelper;

describe("api.util.UriHelper", () => {

    window['CONFIG'] = {
        baseUri: 'http://localhost:8080/wem'
    };

    var uh = UriHelper;

    describe("getUri", () => {
        it("should return '/' string if no CONFIG.baseUri is present", () => {
            var config = window['CONFIG'];
            window['CONFIG'] = undefined;
            expect(uh.getUri(undefined)).toBe('/');
            expect(uh.getUri('a/b/c')).toBe('/a/b/c');
            window['CONFIG'] = config;
        });
        it("should return CONFIG.baseUri if invalid arguments are passed", () => {
            var expected = window['CONFIG'].baseUri;
            expect(uh.getUri(undefined)).toBe(expected);
            expect(uh.getUri('')).toBe(expected);
            expect(uh.getUri('/')).toBe(expected);
            expect(uh.getUri(null)).toBe(expected);
        });
        it("should escape appended path", () => {
            expect(uh.getUri('/a/b/c')).toBe(window['CONFIG'].baseUri + '/a/b/c');
        });
        it("should append path to base uri", () => {
            expect(uh.getUri('a/b/c')).toBe(window['CONFIG'].baseUri + '/a/b/c');
        })
    });

    describe("getAdminUri", () => {
        it("should return '/admin' string if no CONFIG.baseUri is present", () => {
            var config = window['CONFIG'];
            window['CONFIG'] = undefined;
            expect(uh.getAdminUri(undefined)).toBe('/admin');
            expect(uh.getAdminUri('a/b/c')).toBe('/admin/a/b/c');
            window['CONFIG'] = config;
        });
        it("should return CONFIG.baseUri/admin if invalid arguments are passed", () => {
            var expected = window['CONFIG'].baseUri + '/admin';
            expect(uh.getAdminUri(undefined)).toBe(expected);
            expect(uh.getAdminUri('')).toBe(expected);
            expect(uh.getAdminUri('/')).toBe(expected);
            expect(uh.getAdminUri(null)).toBe(expected);
        });
        it("should escape appended path", () => {
            expect(uh.getAdminUri('/a/b/c?d=1&e=false&foo=bar')).toBe(window['CONFIG'].baseUri + '/admin/a/b/c?d=1&e=false&foo=bar');
        });
        it("should append path to base uri", () => {
            expect(uh.getAdminUri('a/b/c?d=1&e=false&foo=bar')).toBe(window['CONFIG'].baseUri + '/admin/a/b/c?d=1&e=false&foo=bar');
        })
    });

    describe("getRestUri", () => {
        it("should return '/admin/rest' string if no CONFIG.baseUri is present", () => {
            var config = window['CONFIG'];
            window['CONFIG'] = undefined;
            expect(uh.getRestUri(undefined)).toBe('/admin/rest');
            expect(uh.getRestUri("a/b/c")).toBe('/admin/rest/a/b/c');
            window['CONFIG'] = config;
        });
        it("should return CONFIG.baseUri/admin/rest if invalid arguments are passed", () => {
            var expected = window['CONFIG'].baseUri + '/admin/rest';
            expect(uh.getRestUri(undefined)).toBe(expected);
            expect(uh.getRestUri('')).toBe(expected);
            expect(uh.getRestUri('/')).toBe(expected);
            expect(uh.getRestUri(null)).toBe(expected);
        });
        it("should convert path to relative", () => {
            expect(uh.getRestUri('/a/b/c?d=1&e=false&foo=bar')).toBe('http://localhost:8080/wem/admin/rest/a/b/c?d=1&e=false&foo=bar');
        });
        it("should append path to base uri", () => {
            expect(uh.getRestUri('a/b/c?d=1&e=false&foo=bar')).toBe('http://localhost:8080/wem/admin/rest/a/b/c?d=1&e=false&foo=bar');
        })
    });

    describe("getPortalUri", () => {
        it("should return '/admin/portal' string if no CONFIG.baseUri is present", () => {
            var config = window['CONFIG'];
            window['CONFIG'] = undefined;
            expect(uh.getPortalUri(undefined)).toBe('/admin/portal');
            expect(uh.getPortalUri('a/b/c')).toBe('/admin/portal/a/b/c');
            window['CONFIG'] = config;
        });
        it("should return CONFIG.baseUri/admin/portal if invalid arguments are passed", () => {
            var expected = window['CONFIG'].baseUri + '/admin/portal';
            expect(uh.getPortalUri(undefined)).toBe(expected);
            expect(uh.getPortalUri('')).toBe(expected);
            expect(uh.getPortalUri('/')).toBe(expected);
            expect(uh.getPortalUri(null)).toBe(expected);
        });
        it("should escape appended path", () => {
            expect(uh.getPortalUri('/a/b/c?d=1&e=false&foo=bar')).toBe(
                window['CONFIG'].baseUri + '/admin/portal/a/b/c?d=1&e=false&foo=bar');
        });
        it("should append path to base uri", () => {
            expect(uh.getPortalUri('a/b/c?d=1&e=false&foo=bar')).toBe(window['CONFIG'].baseUri + '/admin/portal/a/b/c?d=1&e=false&foo=bar');
        })
    });

    describe("relativePath", () => {
        it("should return empty string if invalid arguments are passed", () => {
            var expected = '';
            expect(uh.relativePath(undefined)).toBe(expected);
            expect(uh.relativePath('')).toBe(expected);
            expect(uh.relativePath('/')).toBe(expected);
            expect(uh.relativePath(null)).toBe(expected);
        });
        it("should escape path", () => {
            expect(uh.relativePath('/a/b/c?d=1&e=false&foo=bar')).toBe('a/b/c?d=1&e=false&foo=bar');
        });
        it("should not change already relative path", () => {
            expect(uh.relativePath('a/b/c?d=1&e=false&foo=bar')).toBe('a/b/c?d=1&e=false&foo=bar');
        })
    });

    describe("joinPath", () => {
        it("should return empty string if invalid arguments are passed", () => {
            expect(uh.joinPath()).toBe('');
            expect(uh.joinPath(null, undefined)).toBe('');
        });
        it("should filter extra slashes", () => {
            expect(uh.joinPath('/admin', '//rest', '/uri')).toBe('/admin/rest/uri');
        });
        it("should filter empty strings", () => {
            expect(uh.joinPath('admin', null, '', 'uri')).toBe('admin/uri');
        })
    });

    describe("getUrlLocation", () => {
        it("should return empty string if invalid arguments are passed", () => {
            var expected = '';
            expect(uh.getUrlLocation(undefined)).toBe(expected);
            expect(uh.getUrlLocation('')).toBe(expected);
            expect(uh.getUrlLocation(null)).toBe(expected);
        });
        it("should return location from url", () => {
            expect(uh.getUrlLocation('http://enonic.com/admin?d=1&e=false&foo=bar')).toBe('http://enonic.com/admin');
            expect(uh.getUrlLocation('https://www.enonic.com/admin/rest/uri?d=1&e=false&foo=bar')).toBe(
                'https://www.enonic.com/admin/rest/uri');
        })
    });

    describe("decodeUrlParams", () => {
        it("should return empty object if invalid arguments are passed", () => {
            var expected = {};
            expect(uh.decodeUrlParams(undefined)).toEqual(expected);
            expect(uh.decodeUrlParams('')).toEqual(expected);
            expect(uh.decodeUrlParams(null)).toEqual(expected);
        });
        it("should return url params as object", () => {
            expect(uh.decodeUrlParams('http://enonic.com/admin?d=1&e=false&foo=b%25%20ar!')).toEqual({d: '1', e: 'false', foo: 'b% ar!'});
        })
    });

    describe("encodeUrlParams", () => {
        it("should return empty string if invalid arguments are passed", () => {
            var expected = '';
            expect(uh.encodeUrlParams(undefined)).toBe(expected);
            expect(uh.encodeUrlParams('')).toBe(expected);
            expect(uh.encodeUrlParams(null)).toBe(expected);
        });
        it("should return encoded string", () => {
            expect(uh.encodeUrlParams({d: 1, e: false, foo: { one: 'b% ar!', two: ['a', 'b', 'c']}})).toBe(
                'd=1&e=false&foo%5Bone%5D=b%25%20ar!&foo%5Btwo%5D%5B0%5D=a&foo%5Btwo%5D%5B1%5D=b&foo%5Btwo%5D%5B2%5D=c');
            // d=1&e=false&foo[one]=b% ar!&foo[two][0]=a&foo[two][1]=b&foo[two][2]=c
        })
    });

});

