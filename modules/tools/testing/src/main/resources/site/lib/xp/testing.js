var helper = Java.type('com.enonic.xp.testing.TestHelper');

exports.mock = function (name, object) {
    __.registerMock(name, object);
};

exports.load = function (name) {
    return helper.load(name);
};
