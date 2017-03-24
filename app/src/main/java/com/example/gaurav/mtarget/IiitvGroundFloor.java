package com.example.gaurav.mtarget;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qozix.tileview.TileView;
import com.qozix.tileview.detail.DetailLevel;
import com.qozix.tileview.detail.DetailLevelManager;
import com.qozix.tileview.geom.CoordinateTranslater;
import com.qozix.tileview.markers.MarkerLayout;
import com.qozix.tileview.tiles.Tile;

import java.util.Iterator;
import java.util.Set;

public class IiitvGroundFloor extends TileViewActivity {

    double tilewidth;
    double tileheight;
    View markerview;
    private View markerviewleft, markerviewright, markerviewup, markerviewdown;
    int no_row=0;
    int tileactual=-1, tileleft = -1, tileright = -1, tileup = -1, tiledown = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.floor);

        // multiple references
        final TileView tileView = getTileView();

        // size of original image at 100% mScale
        tileView.setSize(3000, 2700);

        // we're running from assets, should be fairly fast decodes, go ahead and render asap
        tileView.setShouldRenderWhilePanning(true);

        // detail levels
        tileView.addDetailLevel(1.000f, "images_ground_5000_23_25/%d_%d.png", 116, 116);

        tileView.setScaleLimits(2, 2);
        // disable zooming
        //tileView.setShouldScaleToFit(false);
        //tileView.setScaleLimits(1f,1f);

        // define bound for relatice postioning
        tileView.defineBounds(0.0f, 0.0f, 3000.0f, 2700.0f);

        // lets center all markers both horizontally and vertically
        tileView.setMarkerAnchorPoints(-0.5f, -0.5f);

        // set touchevent listener
        tileView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v = (TileView) v;
                CoordinateTranslater coordinateTranslater = ((TileView) v).getCoordinateTranslater();
                double x = v.getScrollX() + event.getX();
                double y = v.getScrollY() + event.getY();
                //System.out.println("coordinate : " + x + " y :" + y);

                double cx = coordinateTranslater.translateAndScaleAbsoluteToRelativeX((float) x, ((TileView) v).getScale());
                double cy = coordinateTranslater.translateAndScaleAbsoluteToRelativeY((float) y, ((TileView) v).getScale());
                //System.out.println("coordinate relaticve : " + cx + " y :" + cy);
                //System.out.println("tile is : " + gettileindex(cx, cy, tilewidth, tileheight).first + ", " + gettileindex(cx, cy, tilewidth, tileheight).second);

                //remove all previous marker
                ((TileView) v).removeMarker(markerview);
                //((TileView) v).removeMarker(markerviewleft);
                //((TileView) v).removeMarker(markerviewright);
                //((TileView) v).removeMarker(markerviewup);
                //((TileView) v).removeMarker(markerviewdown);

                // again place the marker
                placeMarker(R.drawable.reddot, cx, cy);
                placeMarkeronleft(R.drawable.reddot, cx, cy);
                placeMarkeronright(R.drawable.reddot, cx, cy);
                placeMarkerondown(R.drawable.reddot, cx, cy);
                placeMarkeronup(R.drawable.reddot, cx, cy);

                return false;
            }
        });


        // set markerTap listener
        tileView.setMarkerTapListener(new MarkerLayout.MarkerTapListener() {
            @Override
            public void onMarkerTap(View view, int x, int y) {
                //  Toast.makeText(getApplicationContext(),"marker tap ", Toast.LENGTH_LONG).show();
                // get reference to the TileView
                TileView tileView = getTileView();
                // we saved the coordinate in the marker's tag
                CoordinateTranslater coordinateTranslater = tileView.getCoordinateTranslater();
                double cx = coordinateTranslater.translateAndScaleAbsoluteToRelativeX((float) x, tileView.getScale());
                double cy = coordinateTranslater.translateAndScaleAbsoluteToRelativeY((float) y, (tileView).getScale());
                double position[] = new double[]{cx, cy};
                // lets center the screen to that coordinate
                tileView.slideToAndCenter(position[0], position[1]);
                Log.d("position", position[0] + " " + position[1] + " ");
                // create a simple callout
                SampleCallout callout = new SampleCallout(view.getContext());
                // add it to the view tree at the same position and offset as the marker that invoked it
                tileView.addCallout(callout, position[0], position[1], -0.5f, -1.0f);
                // a little sugar
                callout.transitionIn();
                // stub out some text
                callout.setTitle("MAP CALLOUT");
                callout.setSubtitle("Info window at coordinate:\n" + position[1] + ", " + position[0]);
            }
        });


        DetailLevelManager detailLevelManager = tileView.getDetailLevelManager();
        DetailLevel detailLevel = detailLevelManager.getCurrentDetailLevel();

        tilewidth = detailLevel.getTileWidth();
        tileheight = detailLevel.getTileHeight();
        no_row = (int)Math.ceil (tileView.getBaseHeight() / tileheight);
        //System.out.println("base height "+tileView.getBaseHeight()+"base width"+tileView.getBaseWidth());
        // frame the troll
        frameTo(1550, 1550);

        ((RelativeLayout)findViewById(R.id.groundfloormaplayout)).addView(tileView);


    }

    public void addtiledetail(View view){
        Intent intent = new Intent(getApplicationContext(),Addtiledetail.class);
        Bundle data = new Bundle();
        data.putInt("tileactual",tileactual);
        data.putInt("tileleft",tileleft);
        data.putInt("tileright",tileright);
        data.putInt("tileup",tileup);
        data.putInt("tiledown",tiledown);

        intent.putExtras(data);
        startActivity(intent);
    }

    private void placeMarker(int resId, double x, double y) {
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams(256, 256);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        tileactual = gettilenumber(gettileindex(x,y,tilewidth,tileheight));
        markerview = getTileView().addMarker(imageView, x, y, null, null);
    }

    private void placeMarkeronleft(int resId, double x, double y) {
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams(356, 356);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);
        //coordinate of left tile
        double xleft = tilewidth * tilenum.first - 3 * tilewidth / 2;
        double yleft = tileheight * tilenum.second - tilewidth / 2;

        Pair<Integer, Integer> tilenum1 = gettileindex(xleft, yleft, tilewidth, tileheight);
        tileleft = gettilenumber(tilenum1);

        markerviewleft = getTileView().addMarker(imageView, xleft, yleft, null, null);
    }

    private void placeMarkeronright(int resId, double x, double y) {
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams(356, 356);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);
        //coordinate of right tile
        double xright = tilewidth * tilenum.first + tilewidth / 2;
        double yright = tileheight * tilenum.second - tilewidth / 2;

        Pair<Integer, Integer> tilenum1 = gettileindex(xright, yright, tilewidth, tileheight);
        tileright = gettilenumber(tilenum1);

        markerviewright = getTileView().addMarker(imageView, xright, yright, null, null);
    }

    private void placeMarkeronup(int resId, double x, double y) {
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams(356, 356);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);

        //coordinate of upper tile
        double xup = tilewidth * tilenum.first - tilewidth / 2;
        double yup = tileheight * tilenum.second - 3 * tilewidth / 2;

        Pair<Integer, Integer> tilenum1 = gettileindex(xup, yup, tilewidth, tileheight);
        tileup = gettilenumber(tilenum1);

        markerviewright = getTileView().addMarker(imageView, xup, yup, null, null);
    }

    private void placeMarkerondown(int resId, double x, double y) {
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams(356, 356);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);

        //coordinate of down tile
        double xdown = tilewidth * tilenum.first - tilewidth / 2;
        double ydown = tileheight * tilenum.second + tilewidth / 2;

        Pair<Integer, Integer> tilenum1 = gettileindex(xdown, ydown, tilewidth, tileheight);
        tiledown = gettilenumber(tilenum1);

        markerviewright = getTileView().addMarker(imageView, xdown, ydown, null, null);
    }

    // get tile index as col and row number
    private Pair<Integer, Integer> gettileindex(double x, double y, double tilewidth, double tileheight) {
        String tile;
        int X = (int) Math.ceil(x / tilewidth);
        int Y = (int) Math.ceil(y / tileheight);
        Pair<Integer, Integer> tile_no = new Pair<>(X, Y);
        return tile_no;
    }

    // get tile number as unique id to store in database
    public int gettilenumber(Pair<Integer,Integer> tileindex){
        int tilenum=0;
        //System.out.println("tile index is "+tileindex.first+","+tileindex.second);
        tilenum = (tileindex.second - 1)*no_row + tileindex.first;
        return tilenum;
    }

}
