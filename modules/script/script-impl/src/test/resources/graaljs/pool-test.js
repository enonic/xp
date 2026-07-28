var counter = 0;

exports.block = function (sync) {
    sync.await();
    counter++;
    return counter;
};

exports.inc = function () {
    counter++;
    return counter;
};

exports.mkCallback = function () {
    return function () {
        return require('/pool-lib.js').value;
    };
};

exports.mkBlocker = function (sync) {
    return function () {
        sync.await();
        counter++;
        return counter;
    };
};

exports.readJson = function () {
    return require('/pool-data.json').key;
};

exports.echo = function (value) {
    return value;
};
