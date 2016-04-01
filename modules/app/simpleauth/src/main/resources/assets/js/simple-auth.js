function handleAuthenticateResponse(loginResult) {
    console.log("handleAuthenticateResponse  + " + loginResult.authenticated);
    if (loginResult.authenticated) {
        location.reload();
    } else {
        $("#message-container").html("Login failed!");
        $("#message-container").focus();
        $("#username-input, #password-input, #login-button").addClass("invalid");
    }
}

function loginButtonClick() {
    if (checkFieldsEmpty()) {
        return;
    }

    $("#username-input, #password-input, #login-button").removeClass("invalid");

    var data = {
        user: $("#username-input").val(),
        password: $("#password-input").val(),
        userStore: CONFIG.userStoreKey
    }
    $.ajax({
        url: CONFIG.appLoginServiceUrl,
        type: 'post',
        dataType: 'json',
        contentType: 'application/json',
        success: handleAuthenticateResponse,
        data: JSON.stringify(data)
    });
}

function checkFieldsEmpty() {
    return $("#username-input").val() === "" || $("#password-input").val() === "";
}

function onInputTyped(event) {
    $("#username-input, #password-input, #login-button").removeClass("invalid");

    var fieldsEmpty = checkFieldsEmpty();
    if (fieldsEmpty) {
        $("#login-button").hide();
        $("#message-container").html("");
    } else {
        $("#login-button").show();
        if (event.which !== 13) {
            $("#message-container").html("");
        }
    }
}


$("#login-button").click(function () {
    loginButtonClick();
    return false;
});

$("#username-input, #password-input").keyup(function (event) {
    onInputTyped(event);
});

