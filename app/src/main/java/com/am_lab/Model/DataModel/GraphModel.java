package com.am_lab.Model.DataModel;

import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class GraphModel {    //graph description
    int samplingTime;
    String titleX;

    public GraphView getDataGraph() {
        return dataGraph;
    }

    public void setDataGraph(GraphView dataGraph) {
        this.dataGraph = dataGraph;
    }

       public void setDataSeries() {
        this.dataSeries1 = new LineGraphSeries<DataPoint> ();
        this.dataSeries2=new LineGraphSeries<DataPoint> ();
        this.dataSeries3=new LineGraphSeries<DataPoint> ();
        this.dataSeries1.setColor(Color.BLUE);
        this.dataSeries2.setColor(Color.RED);
        this.dataSeries3.setColor(Color.GREEN);
    }


    public void setPointDataSeries() {
        this.pointDataSeries = new PointsGraphSeries<DataPoint>();
        this.pointDataSeries.setColor(Color.RED);
        this.pointDataSeries.setShape(PointsGraphSeries.Shape.POINT);
        this.pointDataSeries.setSize(20);
    }


    public void setAllTitles(String dataTitle1, String dataTitle2, String dataTitle3)
    {
        this.dataSeries1.setTitle(dataTitle1);
        this.dataSeries2.setTitle(dataTitle2);
        this.dataSeries3.setTitle(dataTitle3);
    }

    //graph parameters
    public GraphView dataGraph;
    public PointsGraphSeries<DataPoint> pointDataSeries;
    public LineGraphSeries<DataPoint> dataSeries1;
    public LineGraphSeries<DataPoint> dataSeries2;
    public LineGraphSeries<DataPoint> dataSeries3;
    private int dataGraphMaxDataPointsNumber=1000;
    int[] rangeX;
    int[] rangeY;
    public int[] getRangeY() {
        return rangeY;
    }

    public void setRangeY(int[] rangeY) {
        this.rangeY = rangeY;
    }
    public int[] getRangeX() {
        return rangeX;
    }

    public void setRangeX(int[] rangeX) {
        this.rangeX = rangeX;
    }

    public int getSamplingTime() {
        return samplingTime;
    }

    public void setSamplingTime(int samplingTime) {
        this.samplingTime = samplingTime;
    }

    public String getTitleY() {
        return titleY;
    }

    public void setTitleY(String titleY) {
        this.dataGraph.getGridLabelRenderer().setVerticalAxisTitle(titleY);
    }

    String titleY;

    public String getTitleX() {
        return titleX;
    }

    public void setTitleX(String titleX) {
        this.dataGraph.getGridLabelRenderer().setHorizontalAxisTitle(titleX);
    }

    public void initPointGraph(int samplingTime, int[] rangeX, int[] rangeY, String titleY, String titleX, GraphView view)
    {
        this.setSamplingTime(samplingTime);
        this.setDataGraph(view);
        this.setPointDataSeries();
        this.dataGraph.getViewport().setXAxisBoundsManual(true);
        this.dataGraph.getViewport().setMinX( rangeX[0]);
        this.dataGraph.getViewport().setMaxX(rangeX[1]);
        this.dataGraph.getViewport().setYAxisBoundsManual(true);
        this.dataGraph.getViewport().setMinY(rangeY[0]);
        this.dataGraph.getViewport().setMaxY(rangeY[1]);
        this.setTitleX(titleX);
        this.setTitleY(titleY);
    }

    public void init(int samplingTime,int[] rangeX,int[] rangeY,String titleY,String titleX, GraphView view) {
        this.setSamplingTime(samplingTime);
        this.setDataGraph(view);
        this.setDataSeries();
        this.dataGraph.getViewport().setXAxisBoundsManual(true);
        this.dataGraph.getViewport().setMinX( rangeX[0]);
        this.dataGraph.getViewport().setMaxX(rangeX[1]);
        this.dataGraph.getViewport().setYAxisBoundsManual(true);
        this.dataGraph.getViewport().setMinY(rangeY[0]);
        this.dataGraph.getViewport().setMaxY(rangeY[1]);
        this.setTitleX(titleX);
        this.setTitleY(titleY);

    }
    public void updateFirst(double timeStamp,double rawData,boolean scrollGraph) {
        this.dataSeries1.appendData(new DataPoint(timeStamp, rawData), scrollGraph, dataGraphMaxDataPointsNumber);

        // refresh chart
        this.dataGraph.onDataChanged(true, true);

    }
    public void updateSecond(double timeStamp,double rawData,boolean scrollGraph) {
        this.dataSeries3.appendData(new DataPoint(timeStamp, rawData), scrollGraph, dataGraphMaxDataPointsNumber);

        // refresh chart
        this.dataGraph.onDataChanged(true, true);

    }
    public void updateThird(double timeStamp,double rawData,boolean scrollGraph) {
        this.dataSeries2.appendData(new DataPoint(timeStamp, rawData), scrollGraph, dataGraphMaxDataPointsNumber);

        // refresh chart
        this.dataGraph.onDataChanged(true, true);

    }

    public void addPoint(int X, int Y)
    {
        this.pointDataSeries.appendData(new DataPoint(X,Y),false,dataGraphMaxDataPointsNumber);
        this.dataGraph.onDataChanged(true, true);
    }

}
