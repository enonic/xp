var assert = require('/lib/xp/assert.js');
var http = require('/lib/xp/http-client.js');

exports.simpleGetRequest = function (mockServer) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url'
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "GET request",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "11",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};

exports.simplePostRequest = function (mockServer) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'post',
        body: 'POST body'
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "POST request",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "12",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};

exports.simpleHeadRequest = function (mockServer) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'head'
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "11",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};

exports.getRequestWithParams = function (mockServer) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'get',
        params: {
            'a': 123,
            'b': 456,
            'c': null
        }
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "GET request",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "11",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);
    assert.assertNotNull('http.request stream body null', result.bodyStream);

};

exports.postRequestWithParams = function (mockServer) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'post',
        params: {
            'a': 123,
            'b': 456,
            'c': null
        }
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "POST request",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "12",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};

exports.postJsonRequest = function (mockServer) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'post',
        contentType: 'application/json; charset=utf-8',
        body: JSON.stringify({'a': 123, 'b': 456})
    });


    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "POST request",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "12",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};

exports.getWithHeadersRequest = function (mockServer) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'get',
        headers: {
            'X-Custom-Header': 'some-value'
        }
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "GET request",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "11",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};

exports.getWithResponseTimeout = function (mockServer) {

    try {
        http.request({
            url: 'http://' + mockServer + '/my/url',
            method: 'get',
            readTimeout: 1000
        });

        assert.assertTrue('Expected exception', false);

    } catch (e) {
        var expectedResult = ("timeout" == e.message) || ("Read timed out" == e.message);
        assert.assertTrue('Expected exception', expectedResult);
    }
};

exports.getWithConnectTimeout = function (mockServer) {

    try {
        http.request({
            url: 'http://' + mockServer + '/my/url',
            method: 'get',
            connectionTimeout: 1000
        });
        assert.assertTrue('Expected exception', false);

    } catch (e) {
        var expectedResult = ("timeout" == e.message) || ("Read timed out" == e.message);
        assert.assertTrue('Expected exception', expectedResult);
    }

};

exports.requestWithProxy = function (mockServer, proxyHost, proxyPort) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'post',
        proxy: {
            host: proxyHost,
            port: proxyPort
        }
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "POST request",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "12",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};

exports.requestWithProxyAuth = function (mockServer, proxyHost, proxyPort) {

    var result = http.request({
        url: 'http://' + mockServer + '/my/url',
        method: 'post',
        proxy: {
            host: proxyHost,
            port: proxyPort,
            user: 'admin',
            password: 'secret'
        }
    });

    var expectedJson = {
        "status": 200,
        "message": "OK",
        "body": "POST request authenticated",
        "contentType": "text/plain",
        "headers": {
            "Content-Length": "26",
            "content-type": "text/plain"
        }
    };

    assert.assertJsonEquals('http.request result not equals', expectedJson, result);

};