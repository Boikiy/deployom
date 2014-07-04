/*
 * The MIT License
 *
 * Copyright (c) 2014 DeployOM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
$(function() {

    $("#loginDialog").dialog({
        autoOpen: true,
        modal: true,
        width: 400,
        minHeight: 260,
        height: 260,
        draggable: false,
        closeOnEscape: false,
        resizable: false,
        dialogClass: 'login',
        open: function() {
            $("#loginDialog").keypress(function(e) {
                if (e.keyCode === $.ui.keyCode.ENTER) {
                    $(this).parent().find("button").click();
                }
            });
        },
        buttons: {
            Login: function() {

                // Check User Name
                if ($('#loginUserName').val() === "") {
                    alert('Please enter User Name');
                    return false;
                }

                // Check Password
                if ($('#loginPassword').val() === "") {
                    alert('Please enter Password');
                    return false;
                }

                // Set cookie
                $.cookie("userName", $("#loginUserName").val(), {expires: 30, path: '/'});
                $.cookie("password", $("#loginPassword").val(), {expires: 30, path: '/'});

                // Go to Server
                location.replace("/");
            }
        },
        position: "center"
    });

    // Check Cookies
    if ($.cookie("userName") && $.cookie("password")) {
        $("#loginUserName").val($.cookie("userName"));
        $("#loginPassword").val($.cookie("password"));
    }
});
