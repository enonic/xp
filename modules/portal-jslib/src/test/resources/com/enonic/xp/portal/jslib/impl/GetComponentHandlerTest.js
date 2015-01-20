var expectedJson = {
    "config": {
        "a": ["1"]
    },
    "descriptor": "mymodule:mylayout",
    "name": "mylayout",
    "path": "main/-1",
    "regions": {
        "bottom": {
            "components": [{
                "config": {
                    "a": ["1"]
                },
                "descriptor": "mymodule:mypart",
                "name": "mypart",
                "path": "main/-1/bottom/0",
                "type": "part"
            }]
        }
    },
    "type": "layout"
};

exports.getComponent = function () {
    var result = execute('portal.getComponent');

    assert.assertJson(expectedJson, result);
};

exports.getComponent_notFound = function () {
    var result = execute('portal.getComponent');

    assert.assertNull(result);
};
