var assert = Java.type('org.junit.jupiter.api.Assertions');

exports.checkPhrases = function (o, count) {
    var lost = [];
    for (var i = 0; i < count; i++) {
        var key = 'phrase.key.number.' + i;
        if (o[key] !== 'value-' + i) {
            lost.push(key + '=' + o[key]);
        }
    }
    assert.assertEquals('', lost.slice(0, 10).join(', '), 'properties lost or overwritten');
    assert.assertEquals(String(count), String(Object.keys(o).length), 'wrong number of own properties');
};
