exports.getChildrenById = function () {
    var result = execute('content.getChildren', {
        key: '123456'
    });

    // assert.assertJson(expectedJson, result);
};

exports.getChildrenByPath = function () {
    var result = execute('content.getChildren', {
        key: '/a/b/mycontent'
    });

    // assert.assertJson(expectedJson, result);
};
