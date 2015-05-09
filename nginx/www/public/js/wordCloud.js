function wordCloud() {

  var fill = d3.scale.category20();

  //Construct the word cloud's SVG element
  var svg = d3.select("body")
    .select(".content")
    .select(".stats")
    .select(".word-cloud")
    .append("svg")
    .attr("width", 653)
    .attr("height", 400)
    .attr("position", "relative")

    .append("g")
    .attr("transform", "translate(326,200)");

  //Draw the word cloud
  function draw(words) {
    var cloud = svg.selectAll("g text")
      .data(words, function(d) { return d.text; })

    //Entering words
    cloud.enter()
      .append("text")
      .style("font-family", "Impact")
      .style("fill", function(d, i) { return fill(i); })
      .attr("text-anchor", "middle")
      .attr('font-size', 1)
      .text(function(d) { return d.text; });

    //Entering and existing words
    cloud
      .transition()
      .duration(600)
      .style("font-size", function(d) { return d.size + "px"; })
      .attr("transform", function(d) {
        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
      })
      .style("fill-opacity", 1);

    //Exiting words
    cloud.exit()
      .transition()
      .duration(200)
      .style('fill-opacity', 1e-6)
      .attr('font-size', 1)
      .remove();
  }

  return {

    update: function(words) {
      var textScale = d3.scale.linear()
        .domain([d3.min(words, function(d){return d.size}), d3.max(words, function(d){ return d.size })])
        .range([18, 72]);

      d3.layout.cloud().size([600, 400])
        .words(words)
        .padding(1)
        .rotate(function() { return ~~(Math.random() * 2) * 90; })//return 0; })
        .font("Impact")
        .fontSize(function(d) { return textScale(d.size); })
        .on("end", draw)
        .start();
    }
  }
}
