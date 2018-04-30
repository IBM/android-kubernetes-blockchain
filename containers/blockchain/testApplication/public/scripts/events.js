/*eslint no-undef:0*/
class Events {
  constructor() {
    var self = this;
    self.block = io.connect('http://localhost:3030/block');
    self.block.on('block', (data) => {
      console.log(data);
      self.update(JSON.parse(data));
    });
    self.block.on('connect', () => {
      console.log("Connected");
    });
    self.requestBlocks();
  }
  requestBlocks() {
    var query = {
      type: "blocks",
      queue: "seller_queue",
      params: {
        "noOfLastBlocks": "20"
      }
    };
    var self = this;
    $.ajax({
      url: "http://localhost:3000/api/execute",
      type: "POST",
      data: JSON.stringify(query),
      dataType: 'json',
      contentType: 'application/json; charset=utf-8',
      success: function (data) {
        data = typeof data !== "string" ? data : JSON.parse(data);
        console.log(" Result ID " + data.resultId);
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
          self.loadBlocks(JSON.parse(data.result));
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
    var rowData = "<tr class='anim highlight'><td width='10%'>" + eventData["id"] + "</td><td width='20%'>" + eventData["fingerprint"] + "</td><td width='50%'>" + JSON.stringify(eventData["transactions"]) + "</td></tr>";
    $(rowData).hide().prependTo('#table_view tbody').fadeIn("slow").addClass('normal');
  }
  loadBlocks(data) {
    //console.log(data);
    data = data === "string" ? JSON.parse(data) : data;
    data = data.result.sort((a, b) => a.id > b.id);
    data.forEach(function (eventData) {
      var rowData = "<tr class='anim highlight'><td width='10%'>" + eventData["id"] + "</td><td width='20%'>" + eventData["fingerprint"] + "</td><td width='50%'>" + JSON.stringify(eventData["transactions"]) + "</td></tr>";
      $(rowData).hide().prependTo('#table_view tbody').fadeIn("slow").addClass('normal');
    });
  }
}
new Events();