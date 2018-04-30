/*eslint no-undef:0 */
class ReqEvents {
  constructor() {}
  requestServer(params) {
    var self = this;
    $.ajax({
      url: "http://localhost:3000/api/execute",
      type: "POST",
      data: JSON.stringify(params),
      dataType: 'json',
      contentType: 'application/json; charset=utf-8',
      success: function (data) {
        data = typeof data !== "string" ? data : JSON.parse(data);
        //console.log(" Result ID " + data.resultId);
        self.getResults(data.resultId, 0, self);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        console.log(errorThrown);
        console.log(textStatus);
        console.log(jqXHR);
      }
    });
  }
  getResults(resultId, attemptNo, self) {
    if(attemptNo < 60) {
      //console.log("Attempt no " + attemptNo);
      $.get("http://localhost:3000/api/results/" + resultId).done(function (data) {
        data = typeof data !== "string" ? data : JSON.parse(data);
        //console.log(" Status  " + data.status);
        if(data.status === "done") {
          self.update(JSON.parse(data.result));
        } else {
          setTimeout(function () {
            self.getResults(resultId, attemptNo + 1, self);
          }, 3000);
        }
      }).fail(function () {
        console.log("error");
      });
    } else {
      console.error("exceeded Number of attempts");
    }
  }
  update(eventData) {
    var rowData = "<tr class='anim highlight' style='width: 100%'><td><center>" + JSON.stringify(eventData) + "</center></td></tr>";
    $(rowData).hide().prependTo('#results_table tbody').fadeIn("slow").addClass('normal');
  }
}
var requestEvents = new ReqEvents();
/*
{
  type: "invoke",
  params: {
    "userId": "admin",
    "fcn": "move",
    "args": ["a", "b", "10"]
  }
}
*/
function amqpFunc() {
  var type = $('#type').val();
  var userId = $('#userId').val();
  var fcn = $('#fcn').val();
  var args = $('#argsValues').val().split(',');
  var input = {
    type: type,
    queue: "user_queue",
    params: {
      userId: userId,
      fcn: fcn,
      args: args
    }
  };
  requestEvents.requestServer(input);
}
document.getElementById("request_server").addEventListener("click", amqpFunc);