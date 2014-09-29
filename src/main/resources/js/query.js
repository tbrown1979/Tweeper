$(function () {
  var t         = -1;
  var n         = 40;
  var duration  = 15000
  var newValues = [];
  var data      = d3.range(n).map(zeroedDataPoint);

  function newDataPoint(value) {
    return {time: ++t, value: value};
  }

  function zeroedDataPoint() {
    return newDataPoint(0);
  }

  function getStats(cb) {
    $.get("http://localhost:8081/stats", cb);
  }

  function defaultTick(d) {
    var avg = d.avg;
    newValues.push(d.avg);
  }

  function initialTick(d) {
    defaultTick(d);
    tick();
  }

  function randInt() {
    return Math.random()*50;
  }

  var margin = {
    top: 6,
    right: 0,
    bottom: 20,
    left: 40
  },
  width = 560 - margin.right,
  height = 120 - margin.top - margin.bottom;

  var x = d3.scale.linear()
    .domain([t-n+1, t])
    .range([0, width]);

  var y = d3.time.scale()
    .range([height, 0])
    .domain([40, 50]);;

  var line = d3.svg.area()
    .interpolate("basis")
    .x(function (d, i) {return x(d.time);})
    .y0(height)
    .y1(function (d, i) {return y(d.value);});

  var svg = d3.select("body").append("p").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .style("margin-left", -margin.left + "px")
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  svg.append("defs").append("clipPath")
    .attr("id", "clip")
    .append("rect")
    .attr("width", width)
    .attr("height", height);

  var xAxis = d3.svg.axis().scale(x).orient("bottom");
  var axis = svg.append("g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + height + ")")
    .call(x.axis=xAxis);

  var path = svg.append("g")
    .attr("clip-path", "url(#clip)")
    .append("path")
    .data([data])
    .attr("class", "line");

  getStats(initialTick);
  setInterval(
    function() {
      getStats(defaultTick);
    },
    14750
  )

  function updateIfFallBehind() {//needed?
    var potentialOverflow = data.length - (n+1)
    for (i=0; i < potentialOverflow; i++) {
      console.log("fell behind, POPPING");
      data.shift();
    }
  }


  function tick() {
    // update the domains
    x.domain([t - n + 2 , t]);

    // push the accumulated count onto the back, and reset the count
    var newValue = newValues.shift();
    console.log("new value " + newValue);
    data.push(newDataPoint(newValue));

    // redraw the line
    svg.select(".line")
      .attr("d", line)
      .attr("transform", null);

    // slide the x-axis left
    axis.transition()
      .duration(duration)
      .ease("linear")
      .call(x.axis);

    // slide the line left
    path.transition()
      .duration(duration)
      .ease("linear")
      .attr("transform", "translate(" + x(t-n) + ")")
      .each("end", tick);

    // pop the old data point off the front
    data.shift();

  }
})
