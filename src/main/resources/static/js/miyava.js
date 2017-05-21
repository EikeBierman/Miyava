
var bootstrapRemoteTabSettings = {
    customLoadFn : function(isLoading) {
        var loader = $('.custom-loader');

        if (isLoading) {
            loader.show();
        } else {
            loader.hide();
        }
    },
    loadFirstTab : true
};

$(document).on("ready ajaxComplete", function() {

    // Load at least the first tab
    // $('.ajax-tabs a:first').tab("show");

    // Formularvalidierung
    $('.form-custom').validate({
        ignore : [],
        rules : {
            textInput : {
                required : true
            },
            textArea : {
                required : true
            }
        },
        highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight : function(element) {
            $(element).closest('.form-group').removeClass('has-error');
        },
        errorElement : 'span',
        errorClass : 'help-block'
    });

    $(document).on('submit', '.ajax-form', function(e) {
        e.preventDefault();
        ajaxSubmitForm($(this).attr("action"), $(this).serialize());
    });
    
}); // end ready and ajaxComplete

function ajaxSubmitForm(target, targetParams) {
    $.ajax({
        url : target,
        type : "POST",
        data : targetParams,
        success : function(response) {
            message = response.message;
            if (message) {
                showMessage(message.message, message.type.toLowerCase());
            }
            if (response.status == 'FAIL') {
                for (var i = 0; i < response.errorMessageList.length; i++) {
                    var item = response.errorMessageList[i];
                    var $controlGroup = $('#' + item.fieldName);
                    var $formGroup = $controlGroup.closest(".form-group");
                    $formGroup.addClass('has-error');
                    $controlGroup.parent().find('span').remove().end().append(
                            "<span class='help-block'>" + item.message
                                    + "</span>");
                }
            }
        }
    });
}

function showMessage(messageText, messageType) {
    $().toastmessage('showToast', {
        text : messageText,
        type : messageType
    });
}