package com.am_lab.Model.DataModel;

import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class GraphModel {

    //region GraphModel variables
    int samplingTime;
    int dataGraphMaxDataPointsNumber=1000;
    int[] rangeX;
    int[] rangeY;

    public GraphView dataGraph;
    public PointsGraphSeries<DataPoint> pointDataSeries;
    public LineGraphSeries<DataPoint> dataSeries1;
    public LineGraphSeries<DataPoint> dataSeries2;
    public LineGraphSeries<DataPoint> dataSeries3;
    //endregion

    //region getters+setters
    public void setSamplingTime(int samplingTime) {
        this.samplingTime = samplingTime;
    }

    public void setTitleY(String titleY) { this.dataGraph.getGridLabelRenderer().setVerticalAxisTitle(titleY); }

    public void setTitleX(String titleX) { this.dataGraph.getGridLabelRenderer().setHorizontalAxisTitle(titleX); }

    public GraphView getDataGraph() {
        return dataGraph;
    }

    public void setDataGraph(GraphView dataGraph) { this.dataGraph = dataGraph; }
    //endregion

    /**
     * Initializes three dataseries for a linear graph
     * Sets default colors for each line
     */
       public void setDataSeries() {
        this.dataSeries1 = new LineGraphSeries<DataPoint> ();
        this.dataSeries2=new LineGraphSeries<DataPoint> ();
        this.dataSeries3=new LineGraphSeries<DataPoint> ();
        this.dataSeries1.setColor(Color.BLUE);
        this.dataSeries2.setColor(Color.RED);
        this.dataSeries3.setColor(Color.GREEN);
    }

    /**
     * Initializes dataseries for a points graph
     */
    public void setPointDataSeries() {
        this.pointDataSeries = new PointsGraphSeries<DataPoint>();
        this.pointDataSeries.setColor(Color.RED);
        this.pointDataSeries.setShape(PointsGraphSeries.Shape.POINT);
        this.pointDataSeries.setSize(20);
    }

    /**
     * Sets titles for all linear data series
     * @param dataTitle1 title for the first data series
     * @param dataTitle2 title for the second data series
     * @param dataTitle3 title for the third data series
     */
    public void setAllTitles(String dataTitle1, String dataTitle2, String dataTitle3)
    {
        this.dataSeries1.setTitle(dataTitle1);
        this.dataSeries2.setTitle(dataTitle2);
        this.dataSeries3.setTitle(dataTitle3);
    }

    /**
     * Initializes point graph with received parameters
     * @param samplingTime sampling time, used only to store information
     * @param rangeX range of X axis
     * @param rangeY range of Y axis
     * @param titleY data series title
     * @param titleX X axis title
     * @param view datagraph view found in xml layout
     */
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

    /**
     * Initializes linear graph with received parameters
     * @param samplingTime sampling time, used only to store information
     * @param rangeX range of X axis
     * @param rangeY range of Y axis
     * @param titleY data series title
     * @param titleX X axis title
     * @param view datagraph view found in xml layout
     */
    public void init(int samplingTime,int[] rangeX,int[] rangeY,String titleY,String titleX, GraphView view) {
        this.setSamplingTime(samplingTime);
        this.setDataGraph(view);
        this.setDataSeries();
        dataGraph.getGridLabelRenderer().setNumVerticalLabels(10);
        this.dataGraph.getViewport().setXAxisBoundsManual(true);
        this.dataGraph.getViewport().setMinX( rangeX[0]);
        this.dataGraph.getViewport().setMaxX(rangeX[1]);
        this.dataGraph.getViewport().setYAxisBoundsManual(true);
        this.dataGraph.getViewport().setMinY(rangeY[0]);
        this.dataGraph.getViewport().setMaxY(rangeY[1]);
        this.setTitleX(titleX);
        this.setTitleY(titleY);
    }


    //region updating methods for linear graph
    public void updateFirst(double timeStamp,double rawData,boolean scrollGraph) {
        this.dataSeries1.appendData(new DataPoint(timeStamp, rawData), scrollGraph, dataGraphMaxDataPointsNumber);
        this.dataGraph.onDataChanged(true, true);
    }
    public void updateSecond(double timeStamp,double rawData,boolean scrollGraph) {
        this.dataSeries2.appendData(new DataPoint(timeStamp, rawData), scrollGraph, dataGraphMaxDataPointsNumber);
        this.dataGraph.onDataChanged(true, true);
    }
    public void updateThird(double timeStamp,double rawData,boolean scrollGraph) {
        this.dataSeries3.appendData(new DataPoint(timeStamp, rawData), scrollGraph, dataGraphMaxDataPointsNumber);
        this.dataGraph.onDataChanged(true, true);
    }
    //endregion

    /**
     * adds new point to point data graph
     * @param X position on X axis
     * @param Y position on Y axis
     */
    public void addPoint(int X, int Y)
    {
        this.pointDataSeries.appendData(new DataPoint(X,Y),false,dataGraphMaxDataPointsNumber);
        this.dataGraph.onDataChanged(true, true);
    }

}
