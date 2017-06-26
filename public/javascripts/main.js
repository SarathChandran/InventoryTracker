$(document).ready(function() {
  $("#btnUpload").click(function(event) {
    event.preventDefault();
    upload_file();
  });
  $("#btnTruncate").click(function(event) {
    truncate_data();
  });
  $("#btnTrack").click(function(event) {
    event.preventDefault();
    track_status();
  });
});

function upload_file() {
  var form = $('#fileUploadForm')[0];
  var data = new FormData(form);
  data.append("CustomField", "This is some extra data, testing");
  $("#btnUpload").prop("disabled", true);
  $.ajax({
    type: "POST",
    enctype: 'multipart/form-data',
    url: "/upload",
    data: data,
    processData: false,
    contentType: false,
    cache: false,
    timeout: 600000,
    success: function(data) {
      $("#status").text(data);
      console.log("SUCCESS : ", data);
      $("#btnUpload").prop("disabled", false);
      $("#changelog").val('');
    },
    error: function(e) {
      $("#status").text(e.responseText);
      console.log("ERROR : ", e);
      $("#btnUpload").prop("disabled", false);
    }
  });
}

function truncate_data() {

  $.ajax({
    type: "DELETE",
    url: "/truncate",
    processData: false,
    contentType: false,
    cache: false,
    timeout: 600000,
    success: function(data) {
      $("#status").text(data);
    },
    error: function(e) {
      $("#status").text(e.responseText);
    }
  });
}

function track_status() {

  var formData = JSON.stringify({
    "object_id": parseInt($("#txtObjectId").val()),
    "object_type": $("#txtObjectType").val(),
    "timestamp": parseInt($("#txtTimeStamp").val())
  });

  $.ajax({
    type: "POST",
    url: "/track",
    data: formData,
    processData: false,
    contentType: "application/json",
    cache: false,
    timeout: 600000,
    success: function(data) {
      $("#result").text(JSON.stringify(data, null, 4));
    },
    error: function(e) {
      $("#result").text(e.responseText);
    }
  });
}