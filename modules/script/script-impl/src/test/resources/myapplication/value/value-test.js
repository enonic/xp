exports.testUndefined = function () {
    return undefined;
};

exports.testValue = function () {
    return 1;
};

exports.testObject = function () {
    return {a: 1, b: 2};
};

exports.testArray = function () {
    return [1, 2];
};

exports.testFunction = function () {
    return function (a) {
        return a;
    }
};

exports.testFunctionError = function () {
    return function () {
        throw 'error';
    }
};

