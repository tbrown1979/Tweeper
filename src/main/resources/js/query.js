$(function () {
  function StreamStats(data) {
    this.data = data;
    var callbacks = {};
    callbacks.def = function(d) {
      var avg = d.avg;
      var count = d.count;
      data.push(avg);
      $(".result").html(avg + " " + count);
    }
    callbacks.initial = function(data) {
      callbacks.def(data);
      tick();
    }
    this.callbacks = callbacks
  }

  StreamStats.prototype.init = function () {
    var self = this;
    console.log(this.callbacks);
    this.getStats(this.callbacks.initial);
    setInterval(
      function() { self.getStats(self.callbacks.def) },
      15000
    )
  }


  StreamStats.prototype.getStats = function(cb) {
    $.get("http://localhost:8081/stats", cb);
  }

  var n = 10;
  var data = Array.apply(null, new Array(10)).map(Number.prototype.valueOf,0);
  var streamStats = new StreamStats(data);
  streamStats.init();


  var margin = {top: 20, right: 20, bottom: 20, left: 40},
  width = 960 - margin.left - margin.right,
  height = 500 - margin.top - margin.bottom;
  yStartingPoint = 40;

  var x = d3.scale.linear()
    .domain([0, n - 1])
    .range([0, width]);

  var y = d3.scale.linear()
    .domain([yStartingPoint, 70])
    .range([height, 0]);

  var line = d3.svg.line()
    .x(function(d, i) { return x(i); })
    .y(function(d, i) { return y(d); });

  var svg = d3.select("body").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  svg.append("defs").append("clipPath")
    .attr("id", "clip")
    .append("rect")
    .attr("width", width)
    .attr("height", height);

  svg.append("g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + y(yStartingPoint) + ")")
    .call(d3.svg.axis().scale(x).orient("bottom"));

  svg.append("g")
    .attr("class", "y axis")
    .call(d3.svg.axis().scale(y).orient("left"));

  var path = svg.append("g")
    .attr("clip-path", "url(#clip)")
    .append("path")
    .datum(data)
    .attr("class", "line")
    .attr("d", line);


  function updateIfFallBehind() {
    var potentialOverflow = data.length - (n+1)
    for (i=0; i < potentialOverflow; i++) {
      data.shift();
    }
  }
  function tick() {
    updateIfFallBehind();
    path
      .attr("d", line)
      .attr("transform", null)
      .transition()
      .duration(15000)
      .ease("linear")
      .attr("transform", "translate(" + x(-1) + ",0)")
      .each("end", tick);

    data.shift();
  }

})
