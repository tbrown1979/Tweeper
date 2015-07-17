var chartRT = function () {
  var _self = this;

  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  };

  function guid() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
      s4() + '-' + s4() + s4() + s4();
  }

  _self.guid = guid();
  _self.DataSeries = [];
  _self.Ticks = 20;
  _self.TickDuration = 1000; //1 Sec
  _self.MaxValue = 100;
  _self.w = 600;
  _self.h = 400;
  _self.margin = { top: 20, right: 15, bottom: 40, left: 40 };
  _self.width = _self.w - _self.margin.left - _self.margin.right;
  _self.height = _self.h - _self.margin.top - _self.margin.bottom;
  _self.xText = '';
  _self.yText = '';
  _self.titleText = '';
  _self.chartSeries = {};
  _self.backFillValue = 0;
  _self.extent = [0,0];

  _self.Init = function () {
    d3.select('#chart-' + _self.guid).remove();
    //_self.fillDataSeries();
    //
    //  SVG Canvas
    //
    _self.svg = d3.select("body")
      .select(".content").select(".stats").select(".stream-graph")
      .append("svg")
      .attr("viewBox", "0 0 600 400")
      .attr("preserveAspectRatio", "xMinYMin meet")
      .attr("id", 'chart-' + _self.guid)
      .append("g")

      .attr("width", "100%")//_self.w)
      .attr("height", _self.h)
      .attr("transform", "translate(" + _self.margin.left + "," + _self.margin.top + ")");

    // console.log(_self.svg.parent());
    // d3.select(window).on('resize', _self.resize);

    // _self.resize = function() {
    //   // update width
    //   width = parseInt(d3.select('.stream-graph').style('width'), 10);
    //   console.log("WIDTH: " + width);
    //   width = width - margin.left - margin.right;
    //   _self.svg.attr("width", width);
    // }


    //
    //  Use Clipping to hide chart mechanics
    //
    _self.svg.append("defs").append("clipPath")
      .attr("id", "clip-" + _self.guid)
      .append("rect")
      .attr("width", _self.width)
      .attr("height", _self.height);
    //
    // Generate colors from DataSeries Names
    //
    _self.color = d3.scale.category10();
    _self.color.domain(_self.DataSeries.map(function (d) { return d.Name; }));
    //
    //  X,Y Scale
    //
    _self.xscale = d3.scale.linear().domain([0, _self.Ticks]).range([0, _self.width]);
    _self.yscale = d3.scale.linear().domain([0, _self.MaxValue]).range([_self.height, 0]);
    //_self.yscale.domain(d3.extent(data, function(d) { return d.close; }));
    //
    //  X,Y Axis
    //
    _self.xAxis = d3.svg.axis()
      .scale(d3.scale.linear().domain([0, _self.Ticks * (_self.TickDuration / 1000) ]).range([_self.width, 0]))
      .orient("bottom");
    _self.yAxis = d3.svg.axis()
      .scale(_self.yscale)
      .orient("left");
    //
    //Line/Area Chart
    //
    _self.line = d3.svg.line()
      .interpolate("basis")
      .x(function (d, i) { return _self.xscale(i-1); })
      .y(function (d) { return _self.yscale(d.Value); });

    _self.area = d3.svg.area()
      .interpolate("basis")
      .x(function (d, i) { return _self.xscale(i-1); })
      .y0(_self.height)
      .y1(function (d) { return _self.yscale(d.Value); });
    //
    //  Title
    //
    _self.Title = _self.svg.append("text")
      .attr("id", "title-" + _self.guid)
      .style("text-anchor", "middle")
      .text(_self.titleText)
      .attr("transform", function (d, i) { return "translate(" + _self.width / 2 + "," + -10 + ")"; });
    //
    //  X axis text
    //
    _self.svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + _self.yscale(0) + ")")
      .call(_self.xAxis)
      .append("text")
      .attr("id", "xName-" + _self.guid)
      .attr("x",_self.width / 2)
      .attr("dy", "3em")
      .style("text-anchor", "middle")
      .text(_self.xText);
    //
    // Y axis text
    //
    _self.yaxis = _self.svg.append("g")
      .attr("class", "y axis")
      .call(_self.yAxis);

    _self.yaxis
      .append("text")
      .attr("id", "yName-" + _self.guid)
      .attr("transform", "rotate(-90)")
      .attr("y", 0)
      .attr("x", -_self.height / 2)
      .attr("dy", "-3em")
      .style("text-anchor", "middle")
      .text(_self.yText);
    //
    // Vertical grid lines
    //
    // _self.svg.selectAll(".vline").data(d3.range(_self.Ticks)).enter()
    //   .append("line")
    //   .attr("x1", function (d) { return d * (_self.width / _self.Ticks); })
    //   .attr("x2", function (d) { return d * (_self.width / _self.Ticks); })
    //   .attr("y1", function (d) { return 0; })
    //   .attr("y2", function (d) { return _self.height; })
    //   .style("stroke", "#eee")
    //   .style("opacity", .5)
    //   .attr("clip-path", "url(#clip-" + _self.guid + ")")
    //   .attr("transform", "translate(" + (_self.width / _self.Ticks) + "," + 0 + ")");
    //
    // Horizontal grid lines
    //
    _self.svg.selectAll(".hline").data(d3.range(_self.Ticks)).enter()
      .append("line")
      .attr("x1", function (d) { return 0; })
      .attr("x2", function (d) { return _self.width; })
      .attr("y1", function (d) { return d * (_self.height / (_self.MaxValue / 10)); })
      .attr("y2", function (d) { return d * (_self.height / (_self.MaxValue / 10)); })
      .style("stroke", "#eee")
      .style("opacity", .5)
      .attr("clip-path", "url(#clip-" + _self.guid + ")")
      .attr("transform", "translate(" + 0 + "," + 0 + ")");
    //
    //  Bind DataSeries to chart
    //
    _self.Series = _self.svg.selectAll(".Series")
      .data(_self.DataSeries)
      .enter().append("g")
      .attr("clip-path", "url(#clip-" + _self.guid + ")")
      .attr("class", "Series");
    //
    //  Draw path from Series Data Points
    //
    _self.path = _self.Series.append("path")
      .attr("class", "area")
      .attr("d", function (d) { return _self.area(d.Data); })
      .style("fill", function (d) { return _self.color(d.Name); })
      .style("fill-opacity", .25)
      .style("stroke", function (d) { return _self.color(d.Name); });
    //
    //  Legend
    //
    // _self.Legend = _self.svg.selectAll(".Legend")
    //   .data(_self.DataSeries)
    //   .enter().append("g")
    //   .attr("class", "Legend");

    // _self.Legend.append("circle")
    //   .attr("r", 4)
    //   .style("fill", function (d) { return _self.color(d.Name); })
    //   .style("fill-opacity", .5)
    //   .style("stroke", function (d) { return _self.color(d.Name); })
    //   .attr("transform", function (d, i) { return "translate(" + (_self.width + 6) + "," + (10 + (i * 20)) + ")"; });

    // _self.Legend.append("text")
    //   .text(function (d) { return d.Name; })
    //   .attr("dx", "0.5em")
    //   .attr("dy", "0.25em")
    //   .style("text-anchor", "start")
    //   .attr("transform", function (d, i) { return "translate(" + (_self.width + 6) + "," + (10 + (i * 20)) + ")"; });

    _self.tick = function (id) {
      //console.log("ticking");
      _self.thisTick = new Date();
      var elapsed = parseInt(_self.thisTick - _self.lastTick);
      var elapsedTotal = parseInt(_self.lastTick - _self.firstTick);
      if (elapsed < 900 && elapsedTotal > 0) {
        _self.lastTick = _self.thisTick;
        return;
      }
      if (id < _self.DataSeries.length - 1 && elapsedTotal > 0) {
        return;
      }
      _self.lastTick = _self.thisTick;


      //Add new values
      for (i in _self.DataSeries) {
        _self.DataSeries[i].Data.push({ Value: _self.chartSeries[_self.DataSeries[i].Name] });
        //Backfill missing values
        while (_self.DataSeries[i].Data.length -1<_self.Ticks+3 ) {
          _self.DataSeries[i].Data.unshift({ Value: _self.backFillValue })
        }
      }
      var scaleArea = false;
      var newExtent = d3.extent(_self.DataSeries[0].Data, function(d) { return d.Value; });
      function extentChanged(newExtent, old) {
        return newExtent[0] !== _self.extent[0] || newExtent[1] !== _self.extent[1]
      }
      //if changed, set the new extent, and set true to change the area scale size
      if (extentChanged(newExtent, _self.extent)) {
        _self.extent = d3.extent(_self.DataSeries[0].Data, function(d) { return d.Value; });
        var scaleArea = true;
      }

      //change domain to match max/min values in data
      _self.yscale.domain(_self.extent);

      _self.yaxis.transition()
        .duration(_self.TickDuration)
        .ease("linear")
        .call(_self.yAxis);

      d3.select("#yName-" + _self.guid).text(_self.yText);
      d3.select("#xName-" + _self.guid).text(_self.xText);
      d3.select("#title-" + _self.guid).text(_self.titleText);

      if (scaleArea) {
        _self.path.transition()
          .ease("linear")
          .duration(5000)
          .attr("d", function (d) {  return _self.area(d.Data); })
          .attr("transform", null)
          .attr("transform", "translate(" + _self.xscale(-1) + ",0)")
          .each("end", function (d, i) { _self.tick(i); });
      } else {
        _self.path
          .attr("d", function (d) {  return _self.area(d.Data); })
          .attr("transform", null)
          .transition()
          .ease("linear")
          .attr("transform", "translate(" + _self.xscale(-1) + ",0)")
          .duration(_self.TickDuration)
          .each("end", function (d, i) { _self.tick(i); });
      }

      //Remove oldest values
      for (i in _self.DataSeries) {
        _self.DataSeries[i].Data.shift();
      }
    }
    _self.firstTick = new Date();
    _self.lastTick = new Date();
    _self.start = function () {
      _self.firstTick = new Date();
      _self.lastTick = new Date();
      _self.tick(0);

    }
    _self.start();
  }
  _self.addSeries = function (SeriesName) {
    _self.chartSeries[SeriesName] = _self.backFillValue;
    _self.DataSeries.push({ Name: SeriesName, Data: [{ Value: _self.backFillValue}] });
    _self.Init();
  }

}
//
//
//
//    Real Time Chart Example
//
//
//
function launchChart() {
  // var chart = new chartRT();
  // chart.xText = "Seconds Ago";
  // chart.yText = "";
  // chart.titleText = "Tweets Per Second";
  // chart.Ticks = 10;
  // chart.TickDuration = 5000;
  // chart.MaxValue = 60;

  // var series = "tweets";
  // chart.addSeries(series);
  // var source = new EventSource(config.hostName + "/stats");
  // source.onmessage = function(event) {
  //   var json = JSON.parse(event.data);
  //   chart.chartSeries[series] = json.avg;
  // };

  var areaChartData = [
    {
      label: "Layer 1",
      values: [ {time: 1370044800, y: 100}, {time: 1370044801, y: 1000} ]
    }
  
  $(".stream-graph").epoch({
    type: 'time.area',
    data: areaChartData
  })
  
}
launchChart();


