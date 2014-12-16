exports.query = function () {
    var result = execute('content.query', {
    });

    // assert.assertJson(expectedJson, result);
};

exports.queryEmpty = function () {
    var result = execute('content.query', {
    });

    // assert.assertJson(expectedJson, result);
};
