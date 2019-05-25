$(document).ready(function () {

    // search user detail
    $("#searchUser").click(function (e) { 
        e.preventDefault();
        var data = $("#searchUserInput").val();
        alert(data);
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/admin/search",
            data: {id : data },
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
    
                // var json = "<h4>Ajax Response</h4><pre>"
                //     + JSON.stringify(data, null, 4) + "</pre>";
                // $('#feedback').html(json);
    
                console.log("SUCCESS : ", data);
                var url = "/admin/load";
                $("#userTable").load(url);
    
            },
            error: function (e) {
    
                var json = "<h4>Ajax Response</h4><pre>"
                    + e.responseText + "</pre>";
                $('#feedback').html(json);
    
                console.log("ERROR : ", e);
                alert("Error in request");
    
            }
        });
        
    });

    // search user detail


    // register new user

    $("#register").click(function (e) { 
        e.preventDefault();

        var url = "/admin/register";
        $("#userTable").load(url);
        
    });
    // register new user



});