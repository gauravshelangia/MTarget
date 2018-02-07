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


public class IiiitvFirstFloor extends TileViewActivity {

    double tilewidth;
    double tileheight;
    View markerview;
    int no_row,no_col;
    public View markerviewleft, markerviewright, markerviewup, markerviewdown;
    int tileactual = -1, tileleft = -1, tileright = -1, tileup = -1, tiledown = -1;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.floor);

        // multiple references
        final TileView tileView = getTileView();

        // size of original image at 100% mScale
        tileView.setSize(3200, 2825);

        // we're running from assets, should be fairly fast decodes, go ahead and render asap
        tileView.setShouldRenderWhilePanning(true);

        // detail levels
        tileView.addDetailLevel(1.00f, "images_first_5000_23_26/%d_%d.png", 118, 123);

        tileView.setScaleLimits(5, 5);
        // disable zooming
        //tileView.setShouldScaleToFit(false);
        //tileView.setScaleLimits(1f,1f);

        // define bound for relatice postioning
        tileView.defineBounds(0.0f, 0.0f, 3200.0f, 2825.0f);

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
               // System.out.println("tile is : " + gettileindex(cx, cy, tilewidth, tileheight).first + ", " + gettileindex(cx, cy, tilewidth, tileheight).second);

                /*)
                //remove all previous marker
                ((TileView) v).removeMarker(markerview);
                ((TileView) v).removeMarker(markerviewleft);
                ((TileView) v).removeMarker(markerviewright);
                ((TileView) v).removeMarker(markerviewup);
                ((TileView) v).removeMarker(markerviewdown);
                */

                // place all the marker
                placeMarker(R.drawable.green, cx, cy);
                placeMarkeronleft(v, R.drawable.green, cx, cy);
                placeMarkeronright(v, R.drawable.green, cx, cy);
                placeMarkerondown(v, R.drawable.green, cx, cy);
                placeMarkeronup(v, R.drawable.green, cx, cy);
                //adddot(R.drawable.reddot,cx,cy);
                return false;
            }
        });

/*
        // set markerTap listener
        tileView.setMarkerTapListener(new MarkerLayout.MarkerTapListener() {
            @Override
            public void onMarkerTap(View view, int x, int y) {
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
*/

        DetailLevelManager detailLevelManager = tileView.getDetailLevelManager();
        DetailLevel detailLevel = detailLevelManager.getCurrentDetailLevel();

        tilewidth = detailLevel.getTileWidth();
        tileheight = detailLevel.getTileHeight();
        no_row = (int)Math.ceil (tileView.getBaseHeight() / tileheight);
        no_col = (int) Math.ceil(tileView.getBaseWidth()/tilewidth);

        // frame the troll
        frameTo(1550, 1550);
        ((RelativeLayout) findViewById(R.id.groundfloormaplayout)).addView(tileView);

    }

    public void addtiledetail(View view) {
        Intent intent = new Intent(getApplicationContext(), Addtiledetail.class);

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
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams((int)(tilewidth*getTileView().getScale())-10, (int)(tileheight*getTileView().getScale())-10);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);

        //coordinate of left tile
        double xon = tilewidth * tilenum.first - tilewidth / 2;
        double yon = tileheight * tilenum.second - tileheight / 2;

        tileactual = gettilenumber(gettileindex(x,y,tilewidth,tileheight));
        //System.out.println("actual tile is " + tileactual);
        //System.out.println("tile index is "+tilenum.first+","+tilenum.second);

        markerview = getTileView().addMarker(imageView, xon, yon, null, null);
    }


    private void placeMarkeronleft(View v, int resId, double x, double y) {
        //((TileView) v).removeMarker(markerviewleft);
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams((int)(tilewidth*getTileView().getScale())-10, (int)(tileheight*getTileView().getScale())-10);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);

        //coordinate of left tile
        double xleft = tilewidth * tilenum.first - 3 * tilewidth / 2;
        double yleft = tileheight * tilenum.second - tileheight / 2;
        Pair<Integer, Integer> tilenum1 = gettileindex(xleft, yleft, tilewidth, tileheight);

        tileleft = gettilenumber(tilenum1);
        markerviewleft = getTileView().addMarker(imageView, xleft, yleft, null, null);
    }

    private void placeMarkeronright(View v, int resId, double x, double y) {
       // ((TileView) v).removeMarker(markerviewright);
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams((int)(tilewidth*getTileView().getScale())-10, (int)(tileheight*getTileView().getScale())-10);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);
        //coordinate of right tile
        double xright = tilewidth * tilenum.first + tilewidth / 2;
        double yright = tileheight * tilenum.second - tileheight / 2;
        Pair<Integer, Integer> tilenum1 = gettileindex(xright, yright, tilewidth, tileheight);
        tileright = gettilenumber(tilenum1);
        markerviewright = getTileView().addMarker(imageView, xright, yright, null, null);
    }

    private void placeMarkeronup(View v, int resId, double x, double y) {
       // ((TileView) v).removeMarker(markerviewup);
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams((int)(tilewidth*getTileView().getScale())-10, (int)(tileheight*getTileView().getScale())-10);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);
        //coordinate of upper tile
        double xup = tilewidth * tilenum.first - tilewidth / 2;
        double yup = tileheight * tilenum.second - 3 * tileheight / 2;
        Pair<Integer, Integer> tilenum1 = gettileindex(xup, yup, tilewidth, tileheight);
        tileup = gettilenumber(tilenum1);

        markerviewright = getTileView().addMarker(imageView, xup, yup, null, null);
    }

    private void placeMarkerondown(View v, int resId, double x, double y) {
        //((TileView) v).removeMarker(markerviewdown);
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams((int)(tilewidth*getTileView().getScale())-10, (int)(tileheight*getTileView().getScale())-10);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);
        Pair<Integer, Integer> tilenum = gettileindex(x, y, tilewidth, tileheight);
        //coordinate of down tile
        double xdown = tilewidth * tilenum.first - tilewidth / 2;
        double ydown = tileheight * tilenum.second + tileheight / 2;

        Pair<Integer, Integer> tilenum1 = gettileindex(xdown, ydown, tilewidth, tileheight);
        tiledown = gettilenumber(tilenum1);

        markerviewright = getTileView().addMarker(imageView, xdown, ydown, null, null);
    }

    private void adddot(int resId, double x, double y) {
        ImageView imageView = new ImageView(this);
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams(300,300);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resId);

        //System.out.println("actual tile is " + tileactual);
        //System.out.println("tile index is "+tilenum.first+","+tilenum.second);

        getTileView().addMarker(imageView, x, y, null, null);
    }

    private Pair<Integer, Integer> gettileindex(double x, double y, double tilewidth, double tileheight) {
        String tile;
        int X = (int) Math.ceil(x / tilewidth);
        int Y = (int) Math.ceil(y / tileheight);
        Pair<Integer, Integer> tile_no = new Pair<>(X, Y);
        return tile_no;
    }

    // get tile number as unique id to store in database
    public int gettilenumber(Pair<Integer, Integer> tileindex) {
        int tilenum = 0;
        //System.out.println("tile index is "+tileindex.first+","+tileindex.second);
        //624 number of tiles on ground floor
        tilenum = 624 + (tileindex.second - 1) * no_col + tileindex.first;
        return tilenum;
    }

}
