var expectedJson = {
    "_id": "100123",
    "_name": "my-content",
    "_path": "/my-content",
    "data": {
        "moduleConfig": {
            "config": {
                "Field": 42
            },
            "moduleKey": "mymodule"
        }
    },
    "draft": false,
    "hasChildren": false,
    "isPageTemplate": false,
    "isSite": true,
    "meta": {},
    "moduleConfigs": {
        "mymodule": {
            "Field": 42
        }
    },
    "page": {},
    "type": "system:unstructured"
};

exports.getCurrentSite = function () {
    var result = execute('portal.getSite');

    assert.assertJson(expectedJson, result);
};

exports.getById = function () {
    var result = execute('portal.getSite', {
        key: '100123'
    });

    assert.assertJson(expectedJson, result);
};

exports.getByPath = function () {
    var result = execute('portal.getSite', {
        key: '/a/b/mycontent'
    });

    assert.assertJson(expectedJson, result);
};

exports.getById_notFound = function () {
    var result = execute('portal.getSite', {
        key: '123456'
    });

    assert.assertNull(result);
};

exports.getByPath_notFound = function () {
    var result = execute('portal.getSite', {
        key: '/a/b/mycontent'
    });

    assert.assertNull(result);
};
