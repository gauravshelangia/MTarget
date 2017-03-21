package com.example.gaurav.mtarget;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

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
    @Override
	public void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );



		// multiple references
		final TileView tileView = getTileView();
		
		// size of original image at 100% mScale
		tileView.setSize( 3100, 3100 );

		// we're running from assets, should be fairly fast decodes, go ahead and render asap
		tileView.setShouldRenderWhilePanning( true );
		
		// detail levels
		tileView.addDetailLevel( 1.000f, "images_ground_5000_23_25/%d_%d.png",116,116);
		tileView.addDetailLevel( 2.0f, "Using/%d_%d.png",116,116);

        tileView.setScaleLimits(2,2);
        // disable zooming
        //tileView.setShouldScaleToFit(false);
        //tileView.setScaleLimits(1f,1f);

        // define bound for relatice postioning
        tileView.defineBounds(0.0f,0.0f,3100.0f,3100.0f);

        // lets center all markers both horizontally and vertically
		tileView.setMarkerAnchorPoints( -0.5f, -0.5f );

        // set touchevent listener
		tileView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v = (TileView)v;
				CoordinateTranslater coordinateTranslater = ((TileView) v).getCoordinateTranslater();
                double x = v.getScrollX()+event.getX();
                double y = v.getScrollY()+event.getY();
                System.out.println("coordinate : " + x + " y :" + y);

                double cx = coordinateTranslater.translateAndScaleAbsoluteToRelativeX((float) x, ((TileView) v).getScale());
                double cy = coordinateTranslater.translateAndScaleAbsoluteToRelativeY((float)y,((TileView) v).getScale());
                System.out.println("coordinate relaticve : " + cx + " y :" + cy);
                System.out.println("tile is : " +gettilenumber(cx,cy,tilewidth,tileheight));

                placeMarker( R.drawable.reddot, cx,cy );
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
                double cx = coordinateTranslater.translateAndScaleAbsoluteToRelativeX((float) x,  tileView.getScale());
                double cy = coordinateTranslater.translateAndScaleAbsoluteToRelativeY((float)y,(tileView).getScale());
                double position[] = new double[]{cx,cy};
                // lets center the screen to that coordinate
                tileView.slideToAndCenter( position[0], position[1] );
                Log.d("position", position[0]+" "+ position[1]+" ");
                // create a simple callout
                SampleCallout callout = new SampleCallout( view.getContext() );
                // add it to the view tree at the same position and offset as the marker that invoked it
                tileView.addCallout( callout, position[0], position[1], -0.5f, -1.0f );
                // a little sugar
                callout.transitionIn();
                // stub out some text
                callout.setTitle( "MAP CALLOUT" );
                callout.setSubtitle( "Info window at coordinate:\n" + position[1] + ", " + position[0] );
            }
        });


        DetailLevelManager detailLevelManager = tileView.getDetailLevelManager();
		DetailLevel detailLevel = detailLevelManager.getCurrentDetailLevel();

        tilewidth = detailLevel.getTileWidth();
        tileheight = detailLevel.getTileHeight();

		Set<Tile> mTilesVisibleInViewport;
		//detailLevel.computeVisibleTilesFromViewport();
		Log.d("compute screen)" , detailLevel.computeCurrentState()+" ");
		detailLevel.computeVisibleTilesFromViewport();
		mTilesVisibleInViewport = detailLevel.getVisibleTilesFromLastViewportComputation();

        for(Iterator<Tile> it = mTilesVisibleInViewport.iterator(); it.hasNext();){
            Tile tile = it.next();
            System.out.println( "column "+ tile.getColumn());
        }
		Log.d("size of the set ", mTilesVisibleInViewport.size()+" ");


		// frame the troll
		frameTo( 1550, 1550 );

	}
	
	private void placeMarker( int resId, double x, double y ) {
		ImageView imageView = new ImageView( this );
        MarkerLayout.LayoutParams params = new MarkerLayout.LayoutParams(256,256);
        imageView.setLayoutParams(params);
        imageView.setImageResource( resId );
        getTileView().addMarker( imageView, x, y, null, null );
	}

    private String gettilenumber(double x, double y, double tilewidth, double tileheight){
        String tile;
        int X = (int) Math.ceil(x/tilewidth);
        int Y = (int) Math.ceil(y/tileheight);

        return X+"_"+Y;
    }
	
}
